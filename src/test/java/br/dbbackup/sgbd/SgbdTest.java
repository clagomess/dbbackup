package br.dbbackup.sgbd;

import br.dbbackup.core.Resource;
import br.dbbackup.dto.OptionsDto;
import br.dbbackup.util.TestUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.util.List;
import java.util.UUID;

@Slf4j
public class SgbdTest {
    @Test
    public void getLobBindList() throws Throwable {
        String sample001 = Resource.getString("samples/sample_001.sql");
        Sgbd<H2> sgbd = new Sgbd<>(new H2(), null, null);

        List<String> bind = sgbd.getLobBindList(sample001.trim());

        log.info("{}", bind);

        Assert.assertEquals(2, bind.size());
        Assert.assertTrue(bind.contains(":lob_d04d3341e1b648fc897639bb16beb784"));
        Assert.assertTrue(bind.contains(":lob_73e5c010830c40dfb6324c10574afa48"));
    }

    @Test
    public void getSqlList() throws Throwable {
        String workdir = TestUtil.getNewWorkDir();

        OptionsDto dto = new OptionsDto();
        dto.setWorkdir(workdir);

        Sgbd<H2> sgbd = new Sgbd<>(new H2(), null, dto);

        TestUtil.createFile(String.format("%s/002_%s.sql", workdir, UUID.randomUUID().toString()), "001");
        TestUtil.createFile(String.format("%s/001_%s.sql", workdir, UUID.randomUUID().toString()), "001");
        TestUtil.createFile(String.format("%s/004_%s.sql", workdir, UUID.randomUUID().toString()), "001");
        TestUtil.createFile(String.format("%s/003_%s.sql", workdir, UUID.randomUUID().toString()), "001");

        List<File> files = sgbd.getSqlList();

        for (File file : files){
            log.info("{}", file);
        }

        Assert.assertEquals(4, files.size());
    }

    @Test
    public void quote(){
        OptionsDto dto = new OptionsDto();
        dto.setSgbdToInstance(new H2());
        Sgbd<H2> sgbd = new Sgbd<>(new H2(), null, dto);

        Assert.assertEquals("ola_mundo", sgbd.quote(false, "ola_mundo"));
        Assert.assertEquals("OLA_MUNDO", sgbd.quote(false, "OLA_MUNDO"));
        Assert.assertEquals("\"Ola_Mundo\"", sgbd.quote(false, "Ola_Mundo"));
        Assert.assertEquals("ol4_m4ndo", sgbd.quote(false, "ol4_m4ndo"));
        Assert.assertEquals("\"ol4_N4do\"", sgbd.quote(false, "ol4_N4do"));
        Assert.assertEquals("\"oL4_mundo\"", sgbd.quote(false, "oL4_mundo"));
    }

    @Test
    public void buildTablePrefix(){
        Sgbd<H2> sgbd = new Sgbd<>(new H2(), null, null);

        // lower case
        Assert.assertEquals("pre_", sgbd.buildTablePrefix("pre_","table"));
        Assert.assertEquals("pre_", sgbd.buildTablePrefix("PRE_","table"));
        Assert.assertEquals("pre_", sgbd.buildTablePrefix("PrE_","table"));

        // upper case
        Assert.assertEquals("PRE_", sgbd.buildTablePrefix("pre_","TABLE"));
        Assert.assertEquals("PRE_", sgbd.buildTablePrefix("PRE_","TABLE"));
        Assert.assertEquals("PRE_", sgbd.buildTablePrefix("PrE_","TABLE"));

        // keep case
        Assert.assertEquals("pre_", sgbd.buildTablePrefix("pre_","Table"));
        Assert.assertEquals("PRE_", sgbd.buildTablePrefix("PRE_","Table"));
        Assert.assertEquals("PrE_", sgbd.buildTablePrefix("PrE_","Table"));
    }
}
