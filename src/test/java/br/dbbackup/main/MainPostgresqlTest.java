package br.dbbackup.main;

import br.dbbackup.util.TestUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.nio.file.Files;

@Slf4j
public class MainPostgresqlTest {
    @Test
    public void dump() throws Throwable {
        String workdir = TestUtil.getNewWorkDir();

        // DUMP
        Main.main(new String[]{
                "-db", "POSTGRESQL",
                "-lob", "1",
                "-ope", "GET",
                "-url", TestUtil.URL_POSTGRESQL,
                "-user", TestUtil.USER_POSTGRESQL,
                "-pass", TestUtil.PASS_POSTGRESQL,
                "-schema", TestUtil.SCHEMA_POSTGRESQL,
                "-workdir", workdir,
                "-table", "tbl_dbbackup"
        });

        File backupFile = new File(String.format("%s/%s.tbl_dbbackup.sql", workdir, TestUtil.SCHEMA_POSTGRESQL));
        Assert.assertTrue(backupFile.isFile());

        // PUMP
        Main.main(new String[]{
                "-db", "POSTGRESQL",
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
                "-db", "POSTGRESQL",
                "-lob", "1",
                "-ope", "GET",
                "-url", TestUtil.URL_POSTGRESQL,
                "-user", TestUtil.USER_POSTGRESQL,
                "-pass", TestUtil.PASS_POSTGRESQL,
                "-schema", TestUtil.SCHEMA_POSTGRESQL,
                "-workdir", workdir,
                "-table", "tbl_dbbackup",
                "-dump_format", "ORACLE",
                "-schema_exp", TestUtil.SCHEMA_ORACLE
        });

        File backupFile = new File(String.format("%s/%s.tbl_dbbackup.sql", workdir, TestUtil.SCHEMA_POSTGRESQL));

        String dml = new String(Files.readAllBytes(backupFile.toPath()));
        dml = dml.replace("tbl_dbbackup", "tbl_dbbackup_postgresql");
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

    @Test
    public void pumpMysql() throws Throwable {
        String workdir = TestUtil.getNewWorkDir();

        Main.main(new String[]{
                "-db", "POSTGRESQL",
                "-lob", "1",
                "-ope", "GET",
                "-url", TestUtil.URL_POSTGRESQL,
                "-user", TestUtil.USER_POSTGRESQL,
                "-pass", TestUtil.PASS_POSTGRESQL,
                "-schema", TestUtil.SCHEMA_POSTGRESQL,
                "-workdir", workdir,
                "-table", "tbl_dbbackup",
                "-dump_format", "MYSQL",
                "-schema_exp", TestUtil.SCHEMA_MYSQL
        });

        File backupFile = new File(String.format("%s/%s.tbl_dbbackup.sql", workdir, TestUtil.SCHEMA_POSTGRESQL));

        String dml = new String(Files.readAllBytes(backupFile.toPath()));
        dml = dml.replace("tbl_dbbackup", "tbl_dbbackup_postgresql");
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
    public void pumpH2() throws Throwable {
        String workdir = TestUtil.getNewWorkDir();

        Main.main(new String[]{
                "-db", "POSTGRESQL",
                "-lob", "1",
                "-ope", "GET",
                "-url", TestUtil.URL_POSTGRESQL,
                "-user", TestUtil.USER_POSTGRESQL,
                "-pass", TestUtil.PASS_POSTGRESQL,
                "-schema", TestUtil.SCHEMA_POSTGRESQL,
                "-workdir", workdir,
                "-table", "tbl_dbbackup",
                "-dump_format", "H2",
                "-schema_exp", TestUtil.SCHEMA_H2
        });

        File backupFile = new File(String.format("%s/%s.tbl_dbbackup.sql", workdir, TestUtil.SCHEMA_POSTGRESQL));

        String dml = new String(Files.readAllBytes(backupFile.toPath()));
        dml = dml.replace("tbl_dbbackup", "tbl_dbbackup_postgresql");
        Files.write(backupFile.toPath(), dml.getBytes());

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
}
