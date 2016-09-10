package br.dbbackup.core;

import java.io.*;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public abstract class Database implements DatabaseInterface {
    private HashMap<String, HashMap<String, String>> tabcolumns = new HashMap<>();

    public void setTabColumn(String table, String column, String type) {
        if(!tabcolumns.containsKey(table)){
            tabcolumns.put(table, new HashMap<>());
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
            File outDir = new File("dump/");

            if(!outDir.exists()){
                outDir.mkdir();
            }

            out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(String.format("dump/%s.%s.sql", owner, table)), "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            System.exit(0);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.exit(0);
        }

        return out;
    }

    public static String lobWriter(String owner, String table, String column, byte[] rs){
        String bindName = Double.toString(Calendar.getInstance().getTime().getTime() * Math.random());

        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            bindName = String.format("%032x", new BigInteger(1, md.digest(bindName.getBytes())));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            System.exit(0);
        }

        try {
            File outDir = new File("dump/" + table + "/");

            if(!outDir.exists()){
                outDir.mkdir();
            }

            FileOutputStream out = new FileOutputStream(String.format(
                    "dump/%s/%s_%s_%s_%s.bin",
                    table,
                    owner,
                    table,
                    column,
                    bindName
            ));

            out.write(rs);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            System.exit(0);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.exit(0);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(0);
        }

        return bindName;
    }
}
