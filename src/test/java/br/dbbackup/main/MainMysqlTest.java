package br.dbbackup.main;

import br.dbbackup.constant.Database;
import br.dbbackup.util.TestUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.nio.file.Files;

@Slf4j
public class MainMysqlTest {
    @Test
    public void info() throws Throwable {
        Main.main(new String[]{
                "-db", "MYSQL",
                "-ope", "INFO",
                "-url", TestUtil.paramMysql.getUrl(),
                "-user", TestUtil.paramMysql.getUser(),
                "-pass", TestUtil.paramMysql.getPass(),
                "-schema", TestUtil.paramMysql.getSchema()
        });
    }

    @Test
    public void dump() throws Throwable {
        String workdir = TestUtil.getNewWorkDir();

        // DUMP
        Main.main(new String[]{
                "-db", "MYSQL",
                "-lob", "1",
                "-ope", "GET",
                "-url", TestUtil.paramMysql.getUrl(),
                "-user", TestUtil.paramMysql.getUser(),
                "-pass", TestUtil.paramMysql.getPass(),
                "-schema", TestUtil.paramMysql.getSchema(),
                "-workdir", workdir,
                "-table", "tbl_dbbackup"
        });

        Assert.assertTrue((new File(String.format("%s/001_%s.tbl_dbbackup.sql", workdir, TestUtil.paramMysql.getSchema()))).isFile());

        // PUMP
        Main.main(new String[]{
                "-db", "MYSQL",
                "-lob", "1",
                "-ope", "PUT",
                "-url", TestUtil.paramMysql.getUrl(),
                "-user", TestUtil.paramMysql.getUser(),
                "-pass", TestUtil.paramMysql.getPass(),
                "-schema", TestUtil.paramMysql.getSchema(),
                "-workdir", workdir
        });
    }

    @Test
    public void pumpOracle() throws Throwable {
        String workdir = TestUtil.getNewWorkDir();

        Main.main(new String[]{
                "-db", "MYSQL",
                "-lob", "1",
                "-ope", "GET",
                "-url", TestUtil.paramMysql.getUrl(),
                "-user", TestUtil.paramMysql.getUser(),
                "-pass", TestUtil.paramMysql.getPass(),
                "-schema", TestUtil.paramMysql.getSchema(),
                "-workdir", workdir,
                "-table", "tbl_dbbackup",
                "-dump_format", "ORACLE",
                "-schema_exp", TestUtil.paramOracle.getSchema()
        });

        File backupFile = new File(String.format("%s/001_%s.tbl_dbbackup.sql", workdir, TestUtil.paramMysql.getSchema()));

        String dml = new String(Files.readAllBytes(backupFile.toPath()));
        dml = dml.replace("tbl_dbbackup", "TBL_DBBACKUP_MYSQL");
        Files.write(backupFile.toPath(), dml.getBytes());

        Main.main(new String[]{
                "-db", "ORACLE",
                "-lob", "1",
                "-ope", "PUT",
                "-url", TestUtil.paramOracle.getUrl(),
                "-user", TestUtil.paramOracle.getUser(),
                "-pass", TestUtil.paramOracle.getPass(),
                "-schema", TestUtil.paramOracle.getSchema(),
                "-workdir", workdir
        });
    }

    @Test
    public void pumpPostgresql() throws Throwable {
        String workdir = TestUtil.getNewWorkDir();

        Main.main(new String[]{
                "-db", "MYSQL",
                "-lob", "1",
                "-ope", "GET",
                "-url", TestUtil.paramMysql.getUrl(),
                "-user", TestUtil.paramMysql.getUser(),
                "-pass", TestUtil.paramMysql.getPass(),
                "-schema", TestUtil.paramMysql.getSchema(),
                "-workdir", workdir,
                "-table", "tbl_dbbackup",
                "-dump_format", "POSTGRESQL",
                "-schema_exp", TestUtil.paramPostgresql.getSchema()
        });

        File backupFile = new File(String.format("%s/001_%s.tbl_dbbackup.sql", workdir, TestUtil.paramMysql.getSchema()));

        String dml = new String(Files.readAllBytes(backupFile.toPath()));
        dml = dml.replace("tbl_dbbackup", "tbl_dbbackup_mysql");
        Files.write(backupFile.toPath(), dml.getBytes());

        Main.main(new String[]{
                "-db", "POSTGRESQL",
                "-lob", "1",
                "-ope", "PUT",
                "-url", TestUtil.paramPostgresql.getUrl(),
                "-user", TestUtil.paramPostgresql.getUser(),
                "-pass", TestUtil.paramPostgresql.getPass(),
                "-schema", TestUtil.paramPostgresql.getSchema(),
                "-workdir", workdir
        });
    }

    @Test
    public void pumpH2() throws Throwable {
        String workdir = TestUtil.getNewWorkDir();

        Main.main(new String[]{
                "-db", "MYSQL",
                "-lob", "1",
                "-ope", "GET",
                "-url", TestUtil.paramMysql.getUrl(),
                "-user", TestUtil.paramMysql.getUser(),
                "-pass", TestUtil.paramMysql.getPass(),
                "-schema", TestUtil.paramMysql.getSchema(),
                "-workdir", workdir,
                "-table", "tbl_dbbackup",
                "-dump_format", "H2",
                "-schema_exp", TestUtil.paramH2.getSchema()
        });

        File backupFile = new File(String.format("%s/001_%s.tbl_dbbackup.sql", workdir, TestUtil.paramMysql.getSchema()));

        String dml = new String(Files.readAllBytes(backupFile.toPath()));
        dml = dml.replace("tbl_dbbackup", "tbl_dbbackup_mysql");
        Files.write(backupFile.toPath(), dml.getBytes());

        Main.main(new String[]{
                "-db", "H2",
                "-lob", "1",
                "-ope", "PUT",
                "-url", TestUtil.paramH2.getUrl(),
                "-user", TestUtil.paramH2.getUser(),
                "-pass", TestUtil.paramH2.getPass(),
                "-schema", TestUtil.paramH2.getSchema(),
                "-workdir", workdir
        });
    }

    @Test
    public void pumpSqlite() throws Throwable {
        String workdir = TestUtil.getNewWorkDir();

        Main.main(new String[]{
                "-db", "MYSQL",
                "-lob", "1",
                "-ope", "GET",
                "-url", TestUtil.paramMysql.getUrl(),
                "-user", TestUtil.paramMysql.getUser(),
                "-pass", TestUtil.paramMysql.getPass(),
                "-schema", TestUtil.paramMysql.getSchema(),
                "-workdir", workdir,
                "-table", "tbl_dbbackup",
                "-dump_format", "SQLITE",
                "-schema_exp", TestUtil.paramSqlite.getSchema()
        });

        File backupFile = new File(String.format("%s/001_%s.tbl_dbbackup.sql", workdir, TestUtil.paramMysql.getSchema()));

        String dml = new String(Files.readAllBytes(backupFile.toPath()));
        dml = dml.replace("tbl_dbbackup", "tbl_dbbackup_mysql");
        Files.write(backupFile.toPath(), dml.getBytes());

        Main.main(new String[]{
                "-db", "SQLITE",
                "-lob", "1",
                "-ope", "PUT",
                "-url", TestUtil.paramSqlite.getUrl(),
                "-user", TestUtil.paramSqlite.getUser(),
                "-pass", TestUtil.paramSqlite.getPass(),
                "-schema", TestUtil.paramSqlite.getSchema(),
                "-workdir", workdir
        });
    }

    @Test
    public void ddl() throws Throwable {
        String workdir = TestUtil.getNewWorkDir();

        MainTemplateTest.unitDll(Database.MYSQL, TestUtil.paramMysql, workdir);
    }
}
