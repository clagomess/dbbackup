package br.dbbackup.main;

import br.dbbackup.util.TestUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.ExpectedSystemExit;

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
    public void exit() throws Throwable {
        exit.expectSystemExit();

        Main.main(new String[]{
                "-db", "H2",
                "-lob", "1",
                "-ope", "GET",
                "-url", TestUtil.paramH2.getUrl(),
                "-user", TestUtil.paramH2.getUser(),
                "-pass", TestUtil.paramH2.getPass(),
                "-schema", TestUtil.paramH2.getSchema(),
                "-workdir", TestUtil.getNewWorkDir(),
                "-table", "TBL_DBBACKUP",
                "-table_query", "TBL_DBBACKUP;select foo from TBL_DBBACKUP_MYSQL",
        });
    }
}
