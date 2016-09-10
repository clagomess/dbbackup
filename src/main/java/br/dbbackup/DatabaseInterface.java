package br.dbbackup;


import java.io.IOException;
import java.io.Writer;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

interface DatabaseInterface {
    String SQL_TPL = "INSERT INTO %s.%s (%s) VALUES (%s);\r\n";
    String SQL_QUERY = "SELECT * FROM %s.%s";

    List<String> getTables();
    List<String> getColumns(String table);
    Writer getSqlWriter(String owner, String table);

    // PARA SGDB
    void startDump() throws SQLException;

    String formatColumn(ResultSet rs, String table, String column);

    default void startDumpProcess(Statement stmt, String owner, String owner_exp) throws SQLException {
        ResultSet rs;

        for (String table : getTables()){
            System.out.println(String.format("-> %s.%s", owner, table));
        }

        // Inicio processamento
        System.out.println("\n\n### Iniciando DUMP ###");
        for (String table : getTables()){
            System.out.println(String.format("DUMP Table: \"%s.%s\"", owner, table));

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
                    e.printStackTrace();
                    System.exit(0);
                }
            }

            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(0);
            }
        }
    }
}
