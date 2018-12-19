package br.dbbackup.main;

import br.dbbackup.util.TestUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.ExpectedSystemExit;

import java.io.File;
import java.nio.file.Files;

@Slf4j
public class MainTest {
    @Rule
    public final ExpectedSystemExit exit = ExpectedSystemExit.none();

    @Test
    public void noArgs() throws Throwable {
        exit.expectSystemExit();

        Main.main(new String[]{});
    }

    @Test
    public void mysql() throws Throwable {
        String workdir = TestUtil.getNewWorkDir();

        // DUMP
        Main.main(new String[]{
                "-db", "MYSQL",
                "-lob", "1",
                "-ope", "GET",
                "-url", TestUtil.URL_MYSQL,
                "-user", TestUtil.USER_MYSQL,
                "-pass", TestUtil.PASS_MYSQL,
                "-schema", TestUtil.SCHEMA_MYSQL,
                "-workdir", workdir,
                "-table", "tbl_dbbackup"
        });

        Assert.assertTrue((new File(String.format("%s/%s.tbl_dbbackup.sql", workdir, TestUtil.SCHEMA_MYSQL))).isFile());

        // PUMP
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
    public void mysqlPumpOracle() throws Throwable {
        String workdir = TestUtil.getNewWorkDir();

        Main.main(new String[]{
                "-db", "MYSQL",
                "-lob", "1",
                "-ope", "GET",
                "-url", TestUtil.URL_MYSQL,
                "-user", TestUtil.USER_MYSQL,
                "-pass", TestUtil.PASS_MYSQL,
                "-schema", TestUtil.SCHEMA_MYSQL,
                "-workdir", workdir,
                "-table", "tbl_dbbackup",
                "-dump_format", "ORACLE"
        });

        File backupFile = new File(String.format("%s/%s.tbl_dbbackup.sql", workdir, TestUtil.SCHEMA_MYSQL));

        String dml = new String(Files.readAllBytes(backupFile.toPath()));
        dml = dml.replace("tbl_dbbackup", "tbl_dbbackup_mysql");
        dml = dml.replace(TestUtil.SCHEMA_MYSQL, TestUtil.SCHEMA_ORACLE);
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
    public void postgresql() throws Throwable {
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
    public void oracle() throws Throwable {
        String workdir = TestUtil.getNewWorkDir();

        // DUMP
        Main.main(new String[]{
                "-db", "ORACLE",
                "-lob", "1",
                "-ope", "GET",
                "-url", TestUtil.URL_ORACLE,
                "-user", TestUtil.USER_ORACLE,
                "-pass", TestUtil.PASS_ORACLE,
                "-schema", TestUtil.SCHEMA_ORACLE,
                "-workdir", workdir,
                "-table", "TBL_DBBACKUP"
        });

        Assert.assertTrue((new File(String.format("%s/%s.tbl_dbbackup.sql", workdir, TestUtil.SCHEMA_ORACLE))).isFile());

        // PUMP
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

    @After
    public void after() throws Exception {
        //@TODO: implements
        /*for (File dir : TestUtil.workdirs){
            FileUtils.deleteDirectory(dir);
        }*/
    }
}
