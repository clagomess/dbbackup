package br.dbbackup.sgbd;


import br.dbbackup.core.DbbackupException;
import br.dbbackup.core.Msg;
import br.dbbackup.dto.OptionsDto;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class Sgbd<T extends SgbdImpl> {
    private T instance;
    private Map<String, Map<String, String>> tabcolumns = new HashMap<>();
    private Connection conn;
    private OptionsDto options;

    public Sgbd(T instance, Connection conn, OptionsDto options){
        this.instance = instance;
        this.conn = conn;
        this.options = options;
    }

    public void startDump() throws Throwable {
        ResultSet rs = null;

        try(Statement stmt = conn.createStatement()) {
            log.info(Msg.MSG_CONECTADO);
            log.info(Msg.MSG_TBL_EXPORTACAO);

            rs = stmt.executeQuery(String.format(instance.getSqlTabColumns(), options.getSchema()));

            while (rs.next()) {
                setTabColumn(
                        rs.getString("table_name"),
                        rs.getString("column_name"),
                        rs.getString("data_type")
                );
            }

            this.startDumpProcess(stmt);
        } catch (SQLException e) {
            log.warn(Mysql.class.getName(), e);
            throw new DbbackupException(e.getMessage());
        } finally {
            if(rs != null){
                try {
                    rs.close();
                } catch (SQLException e) {
                    log.warn(Mysql.class.getName(), e);
                }
            }
        }
    }

    public void startPump() throws Throwable {
        startPumpProcess(conn);
    }

    private void startDumpProcess(Statement stmt) throws Throwable {
        for (String table : getTables()){
            log.info("-> {}.{}", options.getSchema(), table);
        }

        File outDir = new File("dump/");
        if(!outDir.exists()){
            outDir.mkdir();
        }

        // Inicio processamento
        log.info("### Iniciando DUMP ###");
        for (String table : getTables()){
            log.info("DUMP Table: \"{}.{}\"", options.getSchema(), table);

            try (
                FileOutputStream fos = new FileOutputStream(String.format("dump/%s.%s.sql", options.getSchema(), table));
                ResultSet rs = stmt.executeQuery(String.format("SELECT * FROM %s.%s", options.getSchema(), table))
            ){
                OutputStreamWriter out = new OutputStreamWriter(fos, "UTF-8");

                while (rs.next()) {
                    List<String> param = new ArrayList<>();

                    for (String column : getColumns(table)) {
                        param.add(options.getSgbdInstance().formatColumn(options, tabcolumns, rs, table, column));
                    }

                    out.write(String.format(
                            "INSERT INTO %s.%s (%s) VALUES (%s);\r\n",
                            options.getSchemaNewName(),
                            table,
                            String.join(", ", getColumns(table)),
                            String.join(", ", param)
                    ));

                    out.flush();
                }
            } catch (Throwable e) {
                log.warn(Sgbd.class.getName(), e);
                throw new DbbackupException(e.getMessage());
            }
        }
    }

    private void startPumpProcess(Connection conn) throws DbbackupException {
        File dumpDir = new File("dump/");
        File[] sqlList = dumpDir.listFiles();
        PreparedStatement pstmt = null;
        Statement stmt = null;

        if(sqlList == null) {
            throw new DbbackupException("Pasta não localizada!");
        }

        for (File sql : sqlList) {
            if(sql.isFile() && sql.getName().contains(".sql")) {
                // abre aquivo para leitura
                log.info("### {}", sql.getName());

                try (FileReader fr = new FileReader("dump/" + sql.getName())){
                    BufferedReader br = new BufferedReader(fr);

                    String dml;
                    while ((dml = br.readLine()) != null) {
                        log.info(dml);

                        // Verifica se tem lob
                        if(dml.matches("(.*)lob_([a-f0-9]{32})(.*)")){
                            Matcher matcher = Pattern.compile("([a-f0-9]{32})").matcher(dml);

                            List<String> hash = new ArrayList<>();
                            while(matcher.find()) {
                                hash.add(matcher.group(0));
                            }

                            for(String hitem : hash){
                                dml = dml.replace(":lob_" + hitem, "?");
                            }

                            pstmt = conn.prepareStatement(dml);

                            int bindIdx = 1;
                            for(String hitem : hash){
                                dml = dml.replace(":lob_" + hash, "?");
                                pstmt.setBinaryStream(bindIdx, new FileInputStream(String.format("dump/lob/lob_%s.bin", hitem)));
                                bindIdx++;
                            }

                            pstmt.execute();
                        }else{
                            stmt = conn.createStatement();
                            stmt.execute(dml);
                        }
                    }
                } catch (IOException | SQLException e) {
                    log.warn(Sgbd.class.getName(), e);
                    throw new DbbackupException(e.getMessage());
                } finally {
                    if(pstmt != null){
                        try {
                            pstmt.close();
                        } catch (SQLException e) {
                            log.warn(Sgbd.class.getName(), e);
                        }
                    }

                    if(stmt != null){
                        try {
                            stmt.close();
                        } catch (SQLException e) {
                            log.warn(Sgbd.class.getName(), e);
                        }
                    }
                }
            }
        }
    }

    private List<String> getColumns(String table) {
        ArrayList<String> columns = new ArrayList<>();

        columns.addAll(tabcolumns.get(table).keySet());

        return columns;
    }

    private List<String> getTables() {
        ArrayList<String> tables = new ArrayList<>();

        tables.addAll(tabcolumns.keySet());

        return tables;
    }

    private void setTabColumn(String table, String column, String type) {
        if(!tabcolumns.containsKey(table)){
            tabcolumns.put(table, new HashMap<>());
        }

        tabcolumns.get(table).put(column, type);
    }
}
