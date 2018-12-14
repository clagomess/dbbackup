package br.dbbackup.core;

import br.dbbackup.sgbd.Sgbd;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;

@Slf4j
public class LobWriter {
    public static String write(byte[] rs) throws DbbackupException {
        String bindName = Double.toString(Calendar.getInstance().getTime().getTime() * Math.random());

        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            bindName = String.format("%032x", new BigInteger(1, md.digest(bindName.getBytes())));
        } catch (NoSuchAlgorithmException e) {
            log.warn(Sgbd.class.getName(), e);
            throw new DbbackupException(e.getMessage());
        }

        File outDir = new File("dump/lob/");

        if(!outDir.exists()){
            outDir.mkdir();
        }

        try (
                FileOutputStream out = new FileOutputStream(String.format("dump/lob/lob_%s.bin", bindName))
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
