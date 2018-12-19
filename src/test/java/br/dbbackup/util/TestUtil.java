package br.dbbackup.util;

import java.io.File;
import java.util.UUID;

public class TestUtil {
    public static final String URL_MYSQL = "jdbc:mysql://localhost/rst";
    public static final String URL_ORACLE = "";
    public static final String URL_POSTGRESQL = "";

    public static final String USER_MYSQL = "root";
    public static final String USER_ORACLE = "";
    public static final String USER_POSTGRESQL = "";

    public static final String PASS_MYSQL = "010203";
    public static final String PASS_ORACLE = "";
    public static final String PASS_POSTGRESQL = "";

    public static final String SCHEMA_MYSQL = "rst";
    public static final String SCHEMA_ORACLE = "";
    public static final String SCHEMA_POSTGRESQL = "";

    public static String getNewWorkDir(){
        String dir = System.getProperty("java.io.tmpdir");
        dir += File.separator;
        dir += UUID.randomUUID().toString();

        return dir;
    }
}
