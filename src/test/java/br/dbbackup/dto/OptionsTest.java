package br.dbbackup.dto;

import br.dbbackup.constant.Database;
import br.dbbackup.core.MainOptions;
import br.dbbackup.sgbd.Oracle;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.junit.Assert;
import org.junit.Test;

@Slf4j
public class OptionsTest {

    @Test
    public void dumpFormat() throws Throwable {
        Options opt = MainOptions.get();
        CommandLineParser parser = new DefaultParser();
        OptionsDto dto = new OptionsDto(parser.parse(opt, new String[]{
                "-db", "MYSQL",
                "-lob", "1",
                "-ope", "GET",
                "-url", "jdbc:mysql://localhost/xxx",
                "-user", "root",
                "-pass", "010203",
                "-schema", "xxx",
                "-dump_format", "ORACLE",
        }));

        Assert.assertEquals(dto.getDatabase(), Database.MYSQL);
        Assert.assertEquals(dto.getDumpFormat(), Database.ORACLE);
        Assert.assertTrue(dto.getSgbdInstance() instanceof Oracle);
    }
}
