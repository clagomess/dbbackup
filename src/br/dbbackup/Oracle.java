package br.dbbackup;

import java.io.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class Oracle extends Database {
    private Connection conn = null;
    private String owner = null;

    private final String SQL_TAB_COLUMNS = "SELECT SATC.TABLE_NAME, SATC.COLUMN_NAME, SATC.DATA_TYPE\n" +
    "FROM SYS.ALL_TAB_COLUMNS SATC\n" +
    "JOIN SYS.ALL_TABLES SAT ON SAT.OWNER = SATC.OWNER AND SAT.TABLE_NAME = SATC.TABLE_NAME\n" +
    "WHERE SATC.OWNER = '%s'";

    public Oracle (Connection conn, String owner){
        this.conn = conn;
        this.owner = owner;
    }

    public void startDump() throws SQLException {
        ResultSet rs;
        Statement stmt = conn.createStatement();

        System.out.println("### Conectado! ###");
        System.out.println("Tabelas para exportação:");

        rs = stmt.executeQuery(String.format(SQL_TAB_COLUMNS, owner));

        while(rs.next()) {
            setTabColumn(
                    rs.getString("TABLE_NAME"),
                    rs.getString("COLUMN_NAME"),
                    rs.getString("DATA_TYPE")
            );

            System.out.println(String.format("-> %s.%s", owner, rs.getString("COLUMN_NAME")));
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
                            owner,
                            table,
                            String.join(", ", getColumns(table)),
                            String.join(", ", param)
                    ));
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                    System.exit(0);
                }
            }

            try {
                out.close();
            } catch (IOException e) {
                System.out.println(e.getMessage());
                System.exit(0);
            }
        }
    }

    private String formatColumn(ResultSet rs, String table, String column){
        String toReturn = "NULL";

        try {
            if(rs.getObject(column) != null){
                switch (getColumnType(table, column)){
                    case "NUMBER":
                        toReturn = rs.getString(column);
                        break;
                    case "CHAR":
                        toReturn = String.format("'%s'", rs.getString(column));
                        break;
                    case "DATE":
                        toReturn = "TO_DATE('%s', 'YYYY-MM-DD HH24-MI-SS.SSSSS')";
                        toReturn = String.format(toReturn, rs.getString(column));
                        break;
                    case "BLOB":
                        toReturn = "EMPTY_BLOB()";
                        break;
                    case "CLOB":
                        toReturn = "EMPTY_CLOB()";
                        break;
                    case "VARCHAR2":
                        toReturn = "UTL_RAW.CAST_TO_VARCHAR2(UTL_ENCODE.BASE64_DECODE(UTL_RAW.CAST_TO_RAW('%s')))";
                        toReturn = String.format(toReturn, Base64.getEncoder().encodeToString(rs.getString(column).getBytes()));
                        break;
                }
            }
        }catch (SQLException e){
            System.out.println(e.getMessage());
            System.exit(0);
        }

        return toReturn;
    }
}
