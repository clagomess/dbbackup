package br.dbbackup.sgbd;

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
        String sample001 = new String(TestUtil.getResource("samples/sample_001.sql"));
        Sgbd sgbd = new Sgbd<>(new H2(), null, null);

        List bind = sgbd.getLobBindList(sample001.trim());

        log.info("{}", bind);

        Assert.assertEquals(2, bind.size());
        Assert.assertTrue(bind.contains(":lob_d04d3341e1b648fc897639bb16beb784"));
        Assert.assertTrue(bind.contains(":lob_73e5c010830c40dfb6324c10574afa48"));
    }

    @Test
    public void getSqlList() throws Throwable {
        String workdir = TestUtil.getNewWorkDir();

        File outDir = new File(workdir);
        if(!outDir.exists()){
            outDir.mkdir();
        }

        OptionsDto dto = new OptionsDto();
        dto.setWorkdir(workdir);

        Sgbd sgbd = new Sgbd<>(new H2(), null, dto);

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
}
