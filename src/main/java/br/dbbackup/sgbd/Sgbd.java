package br.dbbackup.sgbd;


import br.dbbackup.core.DbbackupException;
import br.dbbackup.core.Msg;
import br.dbbackup.dto.OptionsDto;
import br.dbbackup.dto.TabColumnsDto;
import lombok.extern.slf4j.Slf4j;
import me.tongfei.progressbar.ProgressBar;

import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        Statement stmt = conn.createStatement();
        log.info(Msg.MSG_CONECTADO);
        log.info(Msg.MSG_TBL_EXPORTACAO);

        ResultSet rs = stmt.executeQuery(instance.getSqlTabColumns(options));

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
        for (String table : tabcolumns.getTables()){
            log.info("DUMP Table: \"{}.{}\"", options.getSchema(), table);

            // Informando quantidade de registro
            ResultSet rsCount = stmt.executeQuery(String.format("SELECT count(*) \"cnt\" FROM %s.%s", options.getSchema(), table));
            rsCount.next();
            Integer count = rsCount.getInt("cnt");
            rsCount.close();
            log.info("QTD Registro: {}", count);

            // Montando query
            String query = String.format(
                    "SELECT %s FROM %s.%s",
                    String.join(", ", tabcolumns.getColumns(table)),
                    options.getSchema(),
                    table
            );

            log.info("QUERY: {}", query);

            FileOutputStream fos = new FileOutputStream(String.format("%s/%s.%s.sql", options.getWorkdir(), options.getSchema(), table));
            ResultSet rs = stmt.executeQuery(query);

            OutputStreamWriter out = new OutputStreamWriter(fos, "UTF-8");

            // Inicio Dump
            ProgressBar pb = new ProgressBar("Dump", count);
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

            rs.close();
            pb.close();
        }

        stmt.close();
    }

    private void startPumpProcess() throws Throwable {
        File dumpDir = new File(options.getWorkdir());
        File[] sqlList = dumpDir.listFiles();

        if(sqlList == null) {
            throw new DbbackupException("Pasta não localizada!");
        }

        for (File sql : sqlList) {
            if(sql.isFile() && sql.getName().contains(".sql")) {
                // abre aquivo para leitura
                log.info("### {}", sql.getName());
                FileReader fr = new FileReader(options.getWorkdir() + "/" + sql.getName());
                BufferedReader br = new BufferedReader(fr);

                String dml;
                while ((dml = br.readLine()) != null) {
                    log.info(dml);
                    pumpScript(dml);
                }
            }
        }
    }

    protected void pumpScript(String dml) throws Throwable {
        // remover ponto virgula
        dml = dml.replace(";", "");

        // Verifica se tem lob
        List<String> lobs = getLobBindList(dml);
        if(!lobs.isEmpty()){
            for(String lob : lobs) {
                dml = dml.replace(lob, "?");
            }

            PreparedStatement pstmt = conn.prepareStatement(dml);

            int bindIdx = 1;
            for(String lob : lobs){
                pstmt.setBinaryStream(bindIdx, new FileInputStream(String.format("%s/lob/%s.bin", options.getWorkdir(), lob.replace(":", ""))));
                bindIdx++;
            }

            pstmt.execute();
            pstmt.close();
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
}
