package br.dbbackup.main;

import br.dbbackup.util.TestUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.nio.file.Files;

@Slf4j
public class MainH2Test {
    @Test
    public void dump() throws Throwable {
        String workdir = TestUtil.getNewWorkDir();

        // DUMP
        Main.main(new String[]{
                "-db", "H2",
                "-lob", "1",
                "-ope", "GET",
                "-url", TestUtil.URL_H2,
                "-user", TestUtil.USER_H2,
                "-pass", TestUtil.PASS_H2,
                "-schema", TestUtil.SCHEMA_H2,
                "-workdir", workdir,
                "-table", "TBL_DBBACKUP"
        });

        Assert.assertTrue((new File(String.format("%s/%s.TBL_DBBACKUP.sql", workdir, TestUtil.SCHEMA_H2))).isFile());

        // PUMP
        Main.main(new String[]{
                "-db", "H2",
                "-lob", "1",
                "-ope", "PUT",
                "-url", TestUtil.URL_H2,
                "-user", TestUtil.USER_H2,
                "-pass", TestUtil.PASS_H2,
                "-schema", TestUtil.SCHEMA_H2,
                "-workdir", workdir
        });
    }

    @Test
    public void pumpMysql() throws Throwable {
        String workdir = TestUtil.getNewWorkDir();

        Main.main(new String[]{
                "-db", "H2",
                "-lob", "1",
                "-ope", "GET",
                "-url", TestUtil.URL_H2,
                "-user", TestUtil.USER_H2,
                "-pass", TestUtil.PASS_H2,
                "-schema", TestUtil.SCHEMA_H2,
                "-workdir", workdir,
                "-table", "TBL_DBBACKUP",
                "-dump_format", "MYSQL",
                "-schema_exp", TestUtil.SCHEMA_MYSQL
        });

        File backupFile = new File(String.format("%s/%s.TBL_DBBACKUP.sql", workdir, TestUtil.SCHEMA_H2));

        String dml = new String(Files.readAllBytes(backupFile.toPath()));
        dml = dml.replace("TBL_DBBACKUP", "tbl_dbbackup_h2");
        Files.write(backupFile.toPath(), dml.getBytes());

        Main.main(new String[]{
                "-db", "MYSQL",
                "-lob", "1",
                "-ope", "PUT",
                "-url", TestUtil.URL_MYSQL,
                "-user", TestUtil.USER_MYSQL,
                "-pass", TestUtil.PASS_MYSQL,
                "-schema", TestUtil.SCHEMA_MYSQL,
                "-workdir", workdir
        });
    }

    @Test
    public void pumpPostgresql() throws Throwable {
        String workdir = TestUtil.getNewWorkDir();

        Main.main(new String[]{
                "-db", "H2",
                "-lob", "1",
                "-ope", "GET",
                "-url", TestUtil.URL_H2,
                "-user", TestUtil.USER_H2,
                "-pass", TestUtil.PASS_H2,
                "-schema", TestUtil.SCHEMA_H2,
                "-workdir", workdir,
                "-table", "TBL_DBBACKUP",
                "-dump_format", "POSTGRESQL",
                "-schema_exp", TestUtil.SCHEMA_POSTGRESQL
        });

        File backupFile = new File(String.format("%s/%s.TBL_DBBACKUP.sql", workdir, TestUtil.SCHEMA_H2));

        String dml = new String(Files.readAllBytes(backupFile.toPath()));
        dml = dml.replace("TBL_DBBACKUP", "tbl_dbbackup_h2");
        Files.write(backupFile.toPath(), dml.getBytes());

        Main.main(new String[]{
                "-db", "H2",
                "-lob", "1",
                "-ope", "PUT",
                "-url", TestUtil.URL_POSTGRESQL,
                "-user", TestUtil.USER_POSTGRESQL,
                "-pass", TestUtil.PASS_POSTGRESQL,
                "-schema", TestUtil.SCHEMA_POSTGRESQL,
                "-workdir", workdir
        });
    }

    @Test
    public void pumpOracle() throws Throwable {
        String workdir = TestUtil.getNewWorkDir();

        Main.main(new String[]{
                "-db", "H2",
                "-lob", "1",
                "-ope", "GET",
                "-url", TestUtil.URL_H2,
                "-user", TestUtil.USER_H2,
                "-pass", TestUtil.PASS_H2,
                "-schema", TestUtil.SCHEMA_H2,
                "-workdir", workdir,
                "-table", "TBL_DBBACKUP",
                "-dump_format", "ORACLE",
                "-schema_exp", TestUtil.SCHEMA_ORACLE
        });

        File backupFile = new File(String.format("%s/%s.TBL_DBBACKUP.sql", workdir, TestUtil.SCHEMA_H2));

        String dml = new String(Files.readAllBytes(backupFile.toPath()));
        dml = dml.replace("TBL_DBBACKUP", "tbl_dbbackup_h2");
        Files.write(backupFile.toPath(), dml.getBytes());

        Main.main(new String[]{
                "-db", "ORACLE",
                "-lob", "1",
                "-ope", "PUT",
                "-url", TestUtil.URL_ORACLE,
                "-user", TestUtil.USER_ORACLE,
                "-pass", TestUtil.PASS_ORACLE,
                "-schema", TestUtil.SCHEMA_ORACLE,
                "-workdir", workdir
        });
    }
}