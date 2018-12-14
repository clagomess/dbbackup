package br.dbbackup.main;

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
    public void mysqlGet() throws Throwable {
        Main.main(new String[]{
                "-db", "MYSQL",
                "-lob", "1",
                "-ope", "GET",
                "-url", "jdbc:mysql://localhost/rst",
                "-user", "root",
                "-pass", "010203",
                "-schema", "rst",
        });
    }
}
