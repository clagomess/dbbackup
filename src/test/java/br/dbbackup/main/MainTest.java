package br.dbbackup.main;

import br.dbbackup.core.DbbackupException;
import br.dbbackup.util.TestUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.ExpectedSystemExit;

import java.util.UUID;

@Slf4j
public class MainTest {
    @Rule
    public final ExpectedSystemExit exit = ExpectedSystemExit.none();

    @Test
    public void noArgs() throws Throwable {
        exit.expectSystemExit();

        Main.main(new String[]{});
    }

    @Test(expected = DbbackupException.class)
    public void exit() throws Throwable {
        // Create sample
        String sample002 = new String(TestUtil.getResource("samples/sample_002.sql"));
        String workdir = TestUtil.getNewWorkDir();
        TestUtil.createFile(String.format("%s/002_%s.sql", workdir, UUID.randomUUID().toString()), sample002);

        // PUMP
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
}
