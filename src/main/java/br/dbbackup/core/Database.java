package br.dbbackup.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public abstract class Database implements DatabaseInterface {
    private Map<String, Map<String, String>> tabcolumns = new HashMap<>();
    private static final Logger logger = LoggerFactory.getLogger(Database.class);
    private static final String EXCEPTION_LABEL = "ERRO NO Database";

    protected void setTabColumn(String table, String column, String type) {
        if(!tabcolumns.containsKey(table)){
            tabcolumns.put(table, new HashMap<>());
        }

        tabcolumns.get(table).put(column, type);
    }

    protected String getColumnType(String table, String column) {
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
        } catch (UnsupportedEncodingException|FileNotFoundException e) {
            logger.warn(EXCEPTION_LABEL, e);
            System.exit(0);
        }

        return out;
    }

    protected static String lobWriter(byte[] rs){
        String bindName = Double.toString(Calendar.getInstance().getTime().getTime() * Math.random());

        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            bindName = String.format("%032x", new BigInteger(1, md.digest(bindName.getBytes())));
        } catch (NoSuchAlgorithmException e) {
            logger.warn(EXCEPTION_LABEL, e);
            System.exit(0);
        }

        try {
            File outDir = new File("dump/lob/");

            if(!outDir.exists()){
                outDir.mkdir();
            }

            FileOutputStream out = new FileOutputStream(String.format("dump/lob/lob_%s.bin", bindName));

            out.write(rs);
        } catch (IOException e) {
            logger.warn(EXCEPTION_LABEL, e);
            System.exit(0);
        }

        return bindName;
    }
}
