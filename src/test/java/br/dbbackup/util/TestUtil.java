package br.dbbackup.util;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Slf4j
public class TestUtil {
    public static final DbParamDto paramMysql = new DbParamDto("jdbc:mysql://localhost:3306/dbbackup", "dbbackup", "root", "010203");
    public static final DbParamDto paramMariadb = new DbParamDto("jdbc:mariadb://localhost:3306/dbbackup", "dbbackup", "root", "010203");
    public static final DbParamDto paramOracle = new DbParamDto("jdbc:oracle:thin:@localhost:1521:ORCLCDB", "DBBACKUP", "DBBACKUP", "010203");
    public static final DbParamDto paramPostgresql = new DbParamDto("jdbc:postgresql://localhost:5432/postgres", "public", "postgres", "010203");
    public static final DbParamDto paramH2 = new DbParamDto("jdbc:h2:./target/test-classes/h2/dbbackup", "PUBLIC");
    public static final DbParamDto paramSqlite = new DbParamDto("jdbc:sqlite:./target/test-classes/sqlite/dbbackup.db", "main");

    public static String getNewWorkDir(){
        String dir = "target";
        dir += File.separator;
        dir += "dbbackup_";
        dir += UUID.randomUUID().toString();

        File outDir = new File(dir);
        if (!outDir.exists()) {
            outDir.mkdir();
        }

        log.info("workdir: {}", dir);

        return dir;
    }

    public static void createFile(String filename, String content) throws IOException {
        FileOutputStream fos = new FileOutputStream(filename);

        OutputStreamWriter out = new OutputStreamWriter(fos, StandardCharsets.UTF_8);
        out.write(content);

        out.flush();
        out.close();
        fos.close();
    }
}
