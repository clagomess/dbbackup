package br.dbbackup.main;

import br.dbbackup.util.TestUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

@Slf4j
public class MainMariadbTest {
    @Test
    public void dump() throws Throwable {
        String workdir = TestUtil.getNewWorkDir();

        // DUMP
        Main.main(new String[]{
                "-db", "MARIADB",
                "-lob", "1",
                "-ope", "GET",
                "-url", TestUtil.paramMariadb.getUrl(),
                "-user", TestUtil.paramMariadb.getUser(),
                "-pass", TestUtil.paramMariadb.getPass(),
                "-schema", TestUtil.paramMariadb.getSchema(),
                "-workdir", workdir,
                "-table", "tbl_dbbackup"
        });
    }
}
