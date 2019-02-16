package br.dbbackup.core;

import br.dbbackup.dto.OptionsDto;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileOutputStream;
import java.util.UUID;

@Slf4j
public class LobWriter {
    public static String write(OptionsDto options, byte[] rs) throws Throwable {
        final String bindName = UUID.randomUUID().toString().replace("-", "");

        File outDir = new File(options.getWorkdir() + File.separator + "lob");

        if(!outDir.exists()){
            outDir.mkdir();
        }

        FileOutputStream out = new FileOutputStream(String.format("%s/lob/lob_%s.bin", options.getWorkdir(), bindName));
        out.write(rs);
        out.flush();
        out.close();

        return ":lob_" + bindName;
    }
}
