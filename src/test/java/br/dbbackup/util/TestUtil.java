package br.dbbackup.util;

import br.dbbackup.core.DbbackupException;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.util.UUID;

@Slf4j
public class TestUtil {
    public static final String URL_MYSQL = "jdbc:mysql://localhost:3306/dbbackup";
    public static final String URL_ORACLE = "jdbc:oracle:thin:@localhost:1521:ORCLCDB";
    public static final String URL_POSTGRESQL = "jdbc:postgresql://localhost:5432/postgres";
    public static final String URL_H2 = "jdbc:h2:./src/test/resources/h2/dbbackup";

    public static final String USER_MYSQL = "root";
    public static final String USER_ORACLE = "DBBACKUP";
    public static final String USER_POSTGRESQL = "postgres";
    public static final String USER_H2 = "";

    public static final String PASS_MYSQL = "010203";
    public static final String PASS_ORACLE = "010203";
    public static final String PASS_POSTGRESQL = "";
    public static final String PASS_H2 = "";

    public static final String SCHEMA_MYSQL = "dbbackup";
    public static final String SCHEMA_ORACLE = "DBBACKUP";
    public static final String SCHEMA_POSTGRESQL = "public";
    public static final String SCHEMA_H2 = "PUBLIC";

    public static String getNewWorkDir(){
        String dir = "target";
        dir += File.separator;
        dir += "dbbackup_";
        dir += UUID.randomUUID().toString();

        log.info("workdir: {}", dir);

        return dir;
    }

    public static byte[] getResource(String path) throws Throwable {
        URL url = Thread.currentThread().getContextClassLoader().getResource(path);

        if(url == null){
            throw new DbbackupException("não encontrado");
        }

        return Files.readAllBytes(new File(url.getPath()).toPath());
    }
}
