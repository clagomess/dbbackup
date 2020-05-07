package br.dbbackup.main;

import br.dbbackup.constant.Database;
import br.dbbackup.util.DbParamDto;

import java.io.File;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.UUID;

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
                "-ddl_add_table_prefix", UUID.randomUUID().toString().substring(0, 4) + "_" //@TODO: prefixo só com números
        });

        // TEST DDL
        File file = new File(String.format("%s/%s.sql", workdir, param.getSchema()));
        String ddl = new String(Files.readAllBytes(file.toPath()));

        Connection conn = DriverManager.getConnection(
                param.getUrl(),
                param.getUser(),
                param.getPass()
        );

        Statement stmt = conn.createStatement();
        stmt.addBatch(ddl);
        stmt.executeBatch();
        stmt.close();
    }
}
