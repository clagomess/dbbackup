package br.dbbackup.sgbd;


import br.dbbackup.core.DatabaseInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public interface Sgbd extends DatabaseInterface {
    Logger logger = LoggerFactory.getLogger(Sgbd.class);
    String EXCEPTION_LABEL = "ERRO NO Sgbd";

    String SQL_TPL = "INSERT INTO %s.%s (%s) VALUES (%s);\r\n";
    String SQL_QUERY = "SELECT * FROM %s.%s";

    void startDump() throws SQLException;

    void startPump() throws Exception;

    String formatColumn(ResultSet rs, String table, String column);

    default void startDumpProcess(Statement stmt, String owner, String owner_exp) throws SQLException {
        ResultSet rs;

        for (String table : getTables()){
            logger.info("-> {}.{}", owner, table);
        }

        // Inicio processamento
        logger.info("\n\n### Iniciando DUMP ###");
        for (String table : getTables()){
            logger.info("DUMP Table: \"{}.{}\"", owner, table);

            rs = stmt.executeQuery(String.format(SQL_QUERY, owner, table));

            Writer out = getSqlWriter(owner, table);

            while(rs.next()) {
                List<String> param = new ArrayList<>();

                for (String column : getColumns(table)) {
                    param.add(formatColumn(rs, table, column));
                }

                try {
                    out.write(String.format(
                            SQL_TPL,
                            owner_exp,
                            table,
                            String.join(", ", getColumns(table)),
                            String.join(", ", param)
                    ));
                } catch (IOException e) {
                    logger.warn(EXCEPTION_LABEL, e);
                    System.exit(0);
                }
            }

            try {
                out.close();
            } catch (IOException e) {
                logger.warn(EXCEPTION_LABEL, e);
                System.exit(0);
            }
        }
    }

    default void startPumpProcess(Connection conn) throws Exception {
        File dumpDir = new File("dump/");
        File[] sqlList = dumpDir.listFiles();

        if(sqlList != null) {
            for (File sql : sqlList) {
                if (sql.isFile()) {
                    if(sql.getName().contains(".sql")) {
                        // abre aquivo para leitura
                        logger.info("### {}", sql.getName());

                        BufferedReader br = new BufferedReader(new FileReader("dump/" + sql.getName()));

                        String dml;
                        while ((dml = br.readLine()) != null) {
                            logger.info("### {}", dml);

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

                                PreparedStatement pstmt = conn.prepareStatement(dml);

                                int bindIdx = 1;
                                for(String hitem : hash){
                                    dml = dml.replace(":lob_" + hash, "?");
                                    pstmt.setBinaryStream(bindIdx, new FileInputStream(String.format("dump/lob/lob_%s.bin", hitem)));
                                    bindIdx++;
                                }

                                pstmt.execute();
                            }else{
                                Statement stmt = conn.createStatement();
                                stmt.execute(dml);
                            }
                        }
                    }
                }
            }
        }else{
            throw new Exception("Pasta não localizada!");
        }
    }
}
