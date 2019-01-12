package br.dbbackup.main;

import br.dbbackup.core.Multithread;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.RandomStringUtils;
import org.junit.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
public class MainMultithreadTest {
    @Test
    public void gerarMassa() throws Throwable {
        final String ddl = "create table tbl_%s (\n" +
                "  field_id bigint auto_increment,\n" +
                "  field_varchar varchar,\n" +
                "  field_blob blob\n" +
                ")";

        final String dml = "insert into tbl_%s (field_varchar, field_blob) values ( ?, ? )";

        Connection conn = DriverManager.getConnection("jdbc:h2:./src/test/resources/samples/sample_002");

        Multithread mt = new Multithread();

        for(int t = 0; t < 80; t++){
            final int tableNum = t;

            mt.run(new Thread(() -> {
                try{
                    // CRIAR A TABELA
                    String tableName = RandomStringUtils.randomAlphabetic(15).toLowerCase();
                    log.info("Gerando a tabela n {}: tbl_{}", tableNum, tableName);
                    Statement stmt = conn.createStatement();
                    stmt.execute(String.format(ddl, tableName));
                    stmt.close();

                    // gerando 100mil registros
                    int qtdRegistro = ThreadLocalRandom.current().nextInt(1000, 100000);
                    for (int r = 0; r < qtdRegistro; r++){
                        // INSERIR REGISTROS
                        PreparedStatement pstmt = conn.prepareStatement(String.format(dml, tableName));
                        pstmt.setString(1, RandomStringUtils.randomAlphabetic(60));
                        pstmt.setBytes(2, RandomStringUtils.randomAlphabetic(60).getBytes());
                        pstmt.execute();
                        pstmt.close();
                    }
                }catch (Throwable e){
                    log.error(e.getMessage());
                }
            }));
        }
    }
}