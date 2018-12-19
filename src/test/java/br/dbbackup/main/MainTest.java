package br.dbbackup.main;

import br.dbbackup.util.TestUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.ExpectedSystemExit;

import java.io.File;

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
        log.info("workdir: {}", workdir);

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
}
