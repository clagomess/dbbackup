package br.dbbackup.sgbd;


import br.dbbackup.constant.Database;
import br.dbbackup.core.DbbackupException;
import br.dbbackup.dto.OptionsDto;
import br.dbbackup.dto.TabColumnsDto;
import br.dbbackup.dto.TabInfoDto;
import com.github.clagomess.asciitable.AsciiTable;
import lombok.extern.slf4j.Slf4j;
import me.tongfei.progressbar.ProgressBar;
import me.tongfei.progressbar.ProgressBarStyle;

import java.io.*;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

@Slf4j
public class Sgbd<T extends SgbdImpl> {
    private T instance;
    private TabColumnsDto tabcolumns;
    private Connection conn;
    private OptionsDto options;

    public Sgbd(T instance, Connection conn, OptionsDto options){
        this.instance = instance;
        this.conn = conn;
        this.options = options;
        this.tabcolumns = new TabColumnsDto(options);
    }

    public void startDump() throws Throwable {
        log.info("Tabelas para exportação:");
        Statement stmt = conn.createStatement();

        String sql = instance.getSqlTabColumns();
        sql = sql.replaceAll(":TABLE_SCHEMA", String.format("'%s'", options.getSchema()));
        sql = sql.replaceAll(":TABLE_NAME_AR", options.getTable() == null ? "NULL" : String.format("'%s'", String.join("','", options.getTable())));
        sql = sql.replaceAll(":TABLE_NAME", options.getTable() == null ? "NULL" : "'OK'");

        ResultSet rs = stmt.executeQuery(sql);

        while (rs.next()) {
            tabcolumns.setTabColumn(
                    rs.getString("table_name"),
                    rs.getString("column_name"),
                    rs.getString("data_type")
            );
        }

        rs.close();
        stmt.close();

        this.startDumpProcess();
    }

    public void startPump() throws Throwable {
        startPumpProcess();
    }

    private void startDumpProcess() throws Throwable {
        for (String table : tabcolumns.getTables()){
            log.info("-> {}.{}", options.getSchema(), table);
        }

        File outDir = new File(options.getWorkdir());
        if(!outDir.exists()){
            outDir.mkdir();
        }

        Statement stmt = conn.createStatement();

        // Inicio processamento
        log.info("### Iniciando DUMP ###");
        List<String> tables = tabcolumns.getTables();
        int tableNum = 1;

        for (String table : tables){
            // Informando quantidade de registro
            ResultSet rsCount = stmt.executeQuery(String.format(
                    "SELECT count(*) \"cnt\" FROM %s.%s%s%s",
                    options.getSchema(),
                    instance.getQuote(),
                    table,
                    instance.getQuote()
            ));
            rsCount.next();
            int count = rsCount.getInt("cnt");
            rsCount.close();
            log.info("# DUMP Table: \"{}.{}\" ({}/{})", options.getSchema(), table, tableNum, tables.size());
            log.info("QTD Registro: {}", count);

            if(count == 0){
                tableNum++;
                continue;
            }

            // Montando query
            String query = options.getTableQuery(table);
            if(query == null) {
                query = String.format(
                        "SELECT %s%s%s FROM %s.%s%s%s",
                        instance.getQuote(),
                        String.join(instance.getQuote() + ", " + instance.getQuote(), tabcolumns.getColumns(table)),
                        instance.getQuote(),
                        options.getSchema(),
                        instance.getQuote(),
                        table,
                        instance.getQuote()
                );
            }

            log.info("QUERY: {}", query);
            ResultSet rs = stmt.executeQuery(query);

            String sqlFile = String.format(
                    "%s/%s_%s.%s.sql",
                    options.getWorkdir(),
                    String.format("%03d", tableNum),
                    options.getSchema(),
                    table
            );
            log.info("FILE: {}", sqlFile);
            FileOutputStream fos = new FileOutputStream(sqlFile);
            OutputStreamWriter out = new OutputStreamWriter(fos, options.getCharset());

            // Inicio Dump
            ProgressBar pb = new ProgressBar("Dump", count, ProgressBarStyle.ASCII);

            try {
                while (rs.next()) {
                    List<String> param = new ArrayList<>();

                    for (String column : tabcolumns.getColumns(table)) {
                        param.add(options.getSgbdToInstance().formatColumn(options, tabcolumns, rs, table, column));
                    }

                    out.write(String.format(
                            "INSERT INTO %s.%s (%s) VALUES (%s);\r\n",
                            options.getSchemaNewName(),
                            table,
                            String.join(", ", tabcolumns.getColumns(table)),
                            String.join(", ", param)
                    ));

                    out.flush();
                    pb.step();
                }
            } catch (Throwable e){
                throw e;
            } finally {
                out.close();
                fos.close();
                rs.close();
                pb.close();
                tableNum++;
            }
        }

        stmt.close();
    }

    List<File> getSqlList() throws DbbackupException{
        List<File> result = new LinkedList<>();

        File dumpDir = new File(options.getWorkdir());
        File[] sqlList = dumpDir.listFiles();

        if(sqlList == null) {
            throw new DbbackupException("Pasta não localizada ou não possui scripts a ser importado!");
        }

        for (File sql : sqlList) {
            if (sql.isFile() && sql.getName().contains(".sql")) {
                result.add(sql);
            }
        }

        return result;
    }

    private void startPumpProcess() throws Throwable {
        log.info("### Iniciando PUMP ###");

        for (File sql : getSqlList()) {
            // abre aquivo para leitura
            log.info("# PUMP Script: {}", sql.getName());

            // Contar quantidade de linhas
            Stream<String> fls = Files.lines(sql.toPath());
            long rows = fls.count();
            fls.close();
            log.info("ROWS: {}", rows);

            FileReader fr = new FileReader(options.getWorkdir() + "/" + sql.getName());
            BufferedReader br = new BufferedReader(fr);

            ProgressBar pb = new ProgressBar("Pump", rows, ProgressBarStyle.ASCII);

            try {
                String dml;
                while ((dml = br.readLine()) != null) {
                    pumpScript(dml);
                    pb.step();
                }
            }catch (Throwable e){
                throw e;
            } finally {
                br.close();
                fr.close();
                pb.close();
            }
        }
    }

    private void pumpScript(String dml) throws Throwable {
        // remover ponto virgula
        dml = dml.replace(";", "");

        // Verifica se tem lob
        List<String> lobs = getLobBindList(dml);
        if(!lobs.isEmpty()){
            for(String lob : lobs) {
                dml = dml.replace(lob, "?");
            }

            PreparedStatement pstmt = conn.prepareStatement(dml);

            List<FileInputStream> fisList = new ArrayList<>();
            int bindIdx = 1;
            for(String lob : lobs){
                String binFile = String.format("%s/lob/%s.bin", options.getWorkdir(), lob.replace(":", ""));

                if(options.getDatabase() == Database.SQLITE){
                    pstmt.setBytes(bindIdx, Files.readAllBytes((new File(binFile)).toPath()));
                }else {
                    FileInputStream fis = new FileInputStream(binFile);
                    fisList.add(fis);
                    pstmt.setBinaryStream(bindIdx, fis);
                }

                bindIdx++;
            }

            pstmt.execute();
            pstmt.close();

            for(FileInputStream fis : fisList){
                fis.close();
            }
        }else{
            Statement stmt = conn.createStatement();
            stmt.execute(dml);
            stmt.close();
        }
    }

    List<String> getLobBindList(String dml){
        List<String> hash = new ArrayList<>();

        if(dml.matches("(.*):lob_([a-f0-9]{32})(.*)")){
            Matcher matcher = Pattern.compile(":lob_([a-f0-9]{32})").matcher(dml);

            while(matcher.find()) {
                hash.add(matcher.group(0));
            }
        }

        return hash;
    }

    public void buildInfo() throws Throwable{
        List<TabInfoDto> dtoList = new LinkedList<>();
        Statement stmt = conn.createStatement();

        String sql = options.getSgbdFromInstance().getSqlInfo();
        sql = sql.replaceAll(":TABLE_SCHEMA", String.format("'%s'", options.getSchema()));
        ResultSet rsAllTab = stmt.executeQuery(sql);

        log.info("### Coletando Informações ###");

        while (rsAllTab.next()) {
            TabInfoDto dto = new TabInfoDto();
            dto.setTable(rsAllTab.getString("table_name"));
            dto.setQtdColumn(rsAllTab.getLong("qtd_columns"));
            dto.setPkName(rsAllTab.getString("pk_column"));
            dto.setLob(rsAllTab.getLong("lob"));

            dtoList.add(dto);
        }

        rsAllTab.close();

        ProgressBar pb = new ProgressBar("Tables", dtoList.size(), ProgressBarStyle.ASCII);

        for(TabInfoDto dto : dtoList){
            // get table count
            ResultSet rsCount = stmt.executeQuery(String.format(
                    "SELECT count(*) \"cnt\" FROM %s.%s%s%s",
                    options.getSchema(),
                    instance.getQuote(),
                    dto.getTable(),
                    instance.getQuote()
            ));
            rsCount.next();
            dto.setQtdRows(rsCount.getLong("cnt"));
            rsCount.close();

            // get last pk value
            if(dto.getQtdRows() > 0 && dto.getPkName() != null){
                ResultSet rsPk = stmt.executeQuery(String.format(
                        "SELECT MAX(%s) \"last_pk_value\" FROM %s.%s",
                        dto.getPkName(),
                        options.getSchema(),
                        dto.getTable()
                ));
                rsPk.next();
                dto.setLastPkValue(rsPk.getString("last_pk_value"));
                rsPk.close();
            }

            pb.step();
        }

        pb.close();
        stmt.close();

        if(dtoList.size() > 0){
            System.out.println(getAsciiTable(dtoList));
        }else{
            log.warn("Nenhuma tabela encontrada");
        }
    }

    private String getAsciiTable(List<TabInfoDto> dtoList){
        List<Map<String, String>> result = new LinkedList<>();

        int tableNum = 1;
        for(TabInfoDto dto : dtoList){
            Map<String, String> item = new LinkedHashMap<>();
            item.put("#", String.valueOf(tableNum));
            item.put("Table", dto.getTable());
            item.put("QTD. Rows", String.valueOf(dto.getQtdRows()));
            item.put("PK Name", dto.getPkName());
            item.put("Last PK Value", dto.getLastPkValue());
            item.put("QTD. Column(s)", String.valueOf(dto.getQtdColumn()));
            item.put("LOB?", dto.getLob() == 1 ? "SIM" : "NAO");

            result.add(item);
            tableNum++;
        }

        return AsciiTable.build(result);
    }
}
