package br.dbbackup.sgbd;


import br.dbbackup.core.DatabaseInterface;
import br.dbbackup.core.DbbackupException;
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
    String SQL_TPL = "INSERT INTO %s.%s (%s) VALUES (%s);\r\n";
    String SQL_QUERY = "SELECT * FROM %s.%s";

    void startDump() throws DbbackupException;

    void startPump() throws DbbackupException;

    String formatColumn(ResultSet rs, String table, String column) throws DbbackupException;

    default void startDumpProcess(Statement stmt, String owner, String owner_exp) throws DbbackupException {
        for (String table : getTables()){
            logger.info("-> {}.{}", owner, table);
        }

        File outDir = new File("dump/");
        if(!outDir.exists()){
            outDir.mkdir();
        }

        // Inicio processamento
        logger.info("### Iniciando DUMP ###");
        for (String table : getTables()){
            logger.info("DUMP Table: \"{}.{}\"", owner, table);

            try (
                FileOutputStream fos = new FileOutputStream(String.format("dump/%s.%s.sql", owner, table));
                ResultSet rs = stmt.executeQuery(String.format(SQL_QUERY, owner, table))
            ){
                OutputStreamWriter out = new OutputStreamWriter(fos, "UTF-8");

                while (rs.next()) {
                    List<String> param = new ArrayList<>();

                    for (String column : getColumns(table)) {
                        param.add(formatColumn(rs, table, column));
                    }

                    out.write(String.format(
                            SQL_TPL,
                            owner_exp,
                            table,
                            String.join(", ", getColumns(table)),
                            String.join(", ", param)
                    ));

                    out.flush();
                }
            } catch (IOException|SQLException e) {
                logger.warn(Sgbd.class.getName(), e);
                throw new DbbackupException(e.getMessage());
            }
        }
    }

    default void startPumpProcess(Connection conn) throws DbbackupException {
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
                logger.info("### {}", sql.getName());

                try (FileReader fr = new FileReader("dump/" + sql.getName())){
                    BufferedReader br = new BufferedReader(fr);

                    String dml;
                    while ((dml = br.readLine()) != null) {
                        logger.info(dml);

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
                    logger.warn(Sgbd.class.getName(), e);
                    throw new DbbackupException(e.getMessage());
                } finally {
                    if(pstmt != null){
                        try {
                            pstmt.close();
                        } catch (SQLException e) {
                            logger.warn(Sgbd.class.getName(), e);
                        }
                    }

                    if(stmt != null){
                        try {
                            stmt.close();
                        } catch (SQLException e) {
                            logger.warn(Sgbd.class.getName(), e);
                        }
                    }
                }
            }
        }
    }
}
