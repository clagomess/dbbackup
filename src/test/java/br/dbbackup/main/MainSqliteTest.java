package br.dbbackup.main;

import br.dbbackup.util.TestUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;

@Slf4j
public class MainSqliteTest {
    @Test
    public void info() throws Throwable {
        Main.main(new String[]{
                "-db", "SQLITE",
                "-ope", "INFO",
                "-url", TestUtil.paramSqlite.getUrl(),
                "-user", TestUtil.paramSqlite.getUser(),
                "-pass", TestUtil.paramSqlite.getPass(),
                "-schema", TestUtil.paramSqlite.getSchema()
        });
    }

    @Test
    public void dump() throws Throwable {
        String workdir = TestUtil.getNewWorkDir();

        // DUMP
        Main.main(new String[]{
                "-db", "SQLITE",
                "-lob", "1",
                "-ope", "GET",
                "-url", TestUtil.paramSqlite.getUrl(),
                "-user", TestUtil.paramSqlite.getUser(),
                "-pass", TestUtil.paramSqlite.getPass(),
                "-schema", TestUtil.paramSqlite.getSchema(),
                "-workdir", workdir,
                "-table", "tbl_dbbackup"
        });

        Assert.assertTrue((new File(String.format("%s/001_%s.tbl_dbbackup.sql", workdir, TestUtil.paramSqlite.getSchema()))).isFile());

        // PUMP
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

    //@TODO: pumpMysql
    //@TODO: pumpPostgresql
    //@TODO: pumpOracle
    //@TODO: pumpH2
}
