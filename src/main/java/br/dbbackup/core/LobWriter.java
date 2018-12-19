package br.dbbackup.core;

import br.dbbackup.dto.OptionsDto;
import br.dbbackup.sgbd.Sgbd;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

@Slf4j
public class LobWriter {
    public static String write(OptionsDto options, byte[] rs) throws DbbackupException {
        final String bindName = UUID.randomUUID().toString();

        File outDir = new File(options.getWorkdir() + File.separator + "lob");

        if(!outDir.exists()){
            outDir.mkdir();
        }

        try (
                FileOutputStream out = new FileOutputStream(String.format("%s/lob/lob_%s.bin", options.getWorkdir(), bindName))
        ){
            out.write(rs);
            out.flush();
        } catch (IOException e) {
            log.warn(Sgbd.class.getName(), e);
            throw new DbbackupException(e.getMessage());
        }

        return bindName;
    }
}
