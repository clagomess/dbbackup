package br.dbbackup.dto;

import br.dbbackup.constant.Database;
import br.dbbackup.core.MainOptions;
import br.dbbackup.sgbd.Mysql;
import br.dbbackup.sgbd.Oracle;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.cli.CommandLine;
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
        CommandLine cmd = parser.parse(opt, new String[]{
                "-db", "MYSQL",
                "-lob", "1",
                "-ope", "GET",
                "-url", "jdbc:mysql://localhost/xxx",
                "-user", "root",
                "-pass", "010203",
                "-schema", "xxx",
                "-dump_format", "ORACLE",
                "-table", "foo",
                "-table", "bar",
        });

        OptionsDto dto = new OptionsDto(cmd);

        Assert.assertEquals(dto.getDatabase(), Database.MYSQL);
        Assert.assertEquals(dto.getDumpFormat(), Database.ORACLE);
        Assert.assertTrue(dto.getSgbdFromInstance() instanceof Mysql);
        Assert.assertTrue(dto.getSgbdToInstance() instanceof Oracle);
        Assert.assertEquals(2, dto.getTable().size());
    }
}
