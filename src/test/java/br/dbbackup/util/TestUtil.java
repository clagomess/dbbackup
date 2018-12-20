package br.dbbackup.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
public class TestUtil {
    public static final String URL_MYSQL = "jdbc:mysql://localhost/rst";
    public static final String URL_ORACLE = "jdbc:oracle:thin:@localhost:1521:ORCLCDB";
    public static final String URL_POSTGRESQL = "jdbc:postgresql://127.0.0.1:5432/postgres";

    public static final String USER_MYSQL = "root";
    public static final String USER_ORACLE = "DBBACKUP";
    public static final String USER_POSTGRESQL = "postgres";

    public static final String PASS_MYSQL = "010203";
    public static final String PASS_ORACLE = "010203";
    public static final String PASS_POSTGRESQL = "010203";

    public static final String SCHEMA_MYSQL = "rst";
    public static final String SCHEMA_ORACLE = "DBBACKUP";
    public static final String SCHEMA_POSTGRESQL = "public";

    public static List<File> workdirs = new ArrayList<>();

    public static String getNewWorkDir(){
        String dir = System.getProperty("java.io.tmpdir");
        dir += File.separator;
        dir += "dbbackup_";
        dir += UUID.randomUUID().toString();

        workdirs.add(new File(dir));

        log.info("workdir: {}", dir);

        return dir;
    }

    public static void clearWorkDir() {
        for (File dir : workdirs){
            try {
                FileUtils.deleteDirectory(dir);
                workdirs.remove(dir);
                log.info("removed workdir: {}", dir);
            }catch (Throwable e){
                log.error(e.getMessage());
            }
        }
    }
}
