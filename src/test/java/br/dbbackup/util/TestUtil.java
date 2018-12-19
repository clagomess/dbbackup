package br.dbbackup.util;

import java.io.File;
import java.util.UUID;

public class TestUtil {
    public static final String URL_MYSQL = "jdbc:mysql://localhost/rst";
    public static final String URL_ORACLE = "";
    public static final String URL_POSTGRESQL = "jdbc:postgresql://127.0.0.1:5432/postgres";

    public static final String USER_MYSQL = "root";
    public static final String USER_ORACLE = "";
    public static final String USER_POSTGRESQL = "postgres";

    public static final String PASS_MYSQL = "010203";
    public static final String PASS_ORACLE = "";
    public static final String PASS_POSTGRESQL = "010203";

    public static final String SCHEMA_MYSQL = "rst";
    public static final String SCHEMA_ORACLE = "";
    public static final String SCHEMA_POSTGRESQL = "public";

    public static String getNewWorkDir(){
        String dir = System.getProperty("java.io.tmpdir");
        dir += File.separator;
        dir += UUID.randomUUID().toString();

        return dir;
    }
}
