package br.dbbackup.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public abstract class Database implements DatabaseInterface {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseInterface.class);
    private HashMap<String, HashMap<String, String>> tabcolumns = new HashMap<>();

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

        columns.addAll(tabcolumns.get(table).keySet());

        return columns;
    }

    public List<String> getTables() {
        ArrayList<String> tables = new ArrayList<>();

        tables.addAll(tabcolumns.keySet());

        return tables;
    }

    public static String lobWriter(byte[] rs) throws DbbackupException {
        String bindName = Double.toString(Calendar.getInstance().getTime().getTime() * Math.random());

        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            bindName = String.format("%032x", new BigInteger(1, md.digest(bindName.getBytes())));
        } catch (NoSuchAlgorithmException e) {
            logger.warn(Database.class.getName(), e);
            throw new DbbackupException(e.getMessage());
        }

        File outDir = new File("dump/lob/");

        if(!outDir.exists()){
            outDir.mkdir();
        }

        try (
            FileOutputStream out = new FileOutputStream(String.format("dump/lob/lob_%s.bin", bindName))
        ){
            out.write(rs);
            out.flush();
        } catch (IOException e) {
            logger.warn(Database.class.getName(), e);
            throw new DbbackupException(e.getMessage());
        }

        return bindName;
    }
}
