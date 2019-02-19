package br.dbbackup.util;

import br.dbbackup.core.DbbackupException;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.util.UUID;

@Slf4j
public class TestUtil {
    public static final DbParamDto paramMysql = new DbParamDto("jdbc:mysql://localhost:3306/dbbackup", "dbbackup", "root", "010203");
    public static final DbParamDto paramMariadb = new DbParamDto("jdbc:mariadb://localhost:3306/dbbackup", "dbbackup", "root", "010203");
    public static final DbParamDto paramOracle = new DbParamDto("jdbc:oracle:thin:@localhost:1521:ORCLCDB", "DBBACKUP", "DBBACKUP", "010203");
    public static final DbParamDto paramPostgresql = new DbParamDto("jdbc:postgresql://localhost:5432/postgres", "public", "postgres");
    public static final DbParamDto paramH2 = new DbParamDto("jdbc:h2:./target/test-classes/h2/dbbackup", "PUBLIC");

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

    public static byte[] getResource(String path) throws Throwable {
        URL url = Thread.currentThread().getContextClassLoader().getResource(path);

        if(url == null){
            throw new DbbackupException("não encontrado");
        }

        return Files.readAllBytes(new File(url.getPath()).toPath());
    }

    public static void createFile(String filename, String content) throws IOException {
        FileOutputStream fos = new FileOutputStream(filename);

        OutputStreamWriter out = new OutputStreamWriter(fos, "UTF-8");
        out.write(content);

        out.flush();
        out.close();
        fos.close();
    }
}
