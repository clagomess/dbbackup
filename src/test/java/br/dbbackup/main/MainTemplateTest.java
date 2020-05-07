package br.dbbackup.main;

import br.dbbackup.constant.Database;
import br.dbbackup.util.DbParamDto;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;

import java.io.File;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class MainTemplateTest {
    protected static void unitDll(Database database, DbParamDto param, String workdir) throws Throwable {
        Main.main(new String[]{
                "-db", database.getValue(),
                "-ope", "DDL",
                "-url", param.getUrl(),
                "-user", param.getUser(),
                "-pass", param.getPass(),
                "-schema", param.getSchema(),
                "-workdir", workdir,
                "-ddl_add_table_prefix", "x" + UUID.randomUUID().toString().substring(0, 4) + "_"
        });

        // TEST DDL
        File file = new File(String.format("%s/%s.sql", workdir, param.getSchema()));
        String ddl = new String(Files.readAllBytes(file.toPath()));

        Connection conn = DriverManager.getConnection(
                param.getUrl(),
                param.getUser(),
                param.getPass()
        );

        Assert.assertFalse("Contains quote in DDL", ddl.contains("TBL_DBBACKUP\""));
        Assert.assertFalse("Contains quote in DDL", ddl.contains("TBL_DBBACKUP`"));

        // Montar lista de create
        Pattern p = Pattern.compile("CREATE(.*?)\\);", Pattern.DOTALL);
        Matcher m = p.matcher(ddl);

        Statement stmt = conn.createStatement();

        while (m.find()){
            String sql = m.group(0);
            log.info("EXEC: {}", sql);
            stmt.execute(sql);
        }
    }
}
