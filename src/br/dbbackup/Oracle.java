package br.dbbackup;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class Oracle extends Database {
    private Connection conn = null;
    private String owner = null;

    private final String SQL_TPL = "INSERT INTO %s.%s (%s) VALUES (%s);\r\n";
    private final String SQL_TAB_COLUMNS = "SELECT TABLE_NAME, COLUMN_NAME, DATA_TYPE FROM SYS.ALL_TAB_COLUMNS " +
    " WHERE OWNER = '%s' AND TABLE_NAME = 'S_CATEGORIA'";
    private final String SQL_QUERY = "SELECT * FROM %s.%s";

    public Oracle (Connection conn, String owner){
        this.conn = conn;
        this.owner = owner;
    }

    public void startDump() throws SQLException {
        ResultSet rs;
        Statement stmt = conn.createStatement();

        System.out.println("Conectado!");

        rs = stmt.executeQuery(String.format(SQL_TAB_COLUMNS, owner));

        while(rs.next()) {
            setTabColumn(
                    rs.getString("TABLE_NAME"),
                    rs.getString("COLUMN_NAME"),
                    rs.getString("DATA_TYPE")
            );
        }

        // Inicio processamento
        for (String table : getTables()){
            rs = stmt.executeQuery(String.format(SQL_QUERY, owner, table));

            while(rs.next()) {
                List<String> param = new ArrayList<>();

                for (String column : getColumns(table)) {
                    param.add(String.format("'%s'", rs.getString(column)));

                    //@TODO: Adicionar Formatações
                }

                System.out.println(String.format(
                        SQL_TPL,
                        owner,
                        table,
                        String.join(", ", getColumns(table)),
                        String.join(", ", param)
                ));
            }
        }
    }
}
