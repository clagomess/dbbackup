package br.dbbackup;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Database {
    public final String SQL_TPL = "INSERT INTO %s.%s (%s) VALUES (%s);\r\n";
    public final String SQL_QUERY = "SELECT * FROM %s.%s";

    private HashMap<String, HashMap<String, String>> tabcolumns = new HashMap<>();

    public void setTabColumn(String table, String column, String type) {
        if(!tabcolumns.containsKey(table)){
            tabcolumns.put(table, new HashMap<String, String>());
        }

        tabcolumns.get(table).put(column, type);
    }

    public String getColumnType(String table, String column) {
        return tabcolumns.get(table).get(column);
    }

    public List<String> getColumns(String table) {
        ArrayList<String> columns = new ArrayList<>();

        for (String column: tabcolumns.get(table).keySet()) {
            columns.add(column);
        }

        return columns;
    }

    public List<String> getTables() {
        ArrayList<String> tables = new ArrayList<>();

        for (String table: tabcolumns.keySet()) {
            tables.add(table);
        }

        return tables;
    }

    public Writer getSqlWriter(String owner, String table){
        Writer out = null;

        try {
            out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(String.format("%s.%s.sql", owner, table)), "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return out;
    }
}
