package br.dbbackup.core;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;

public class Resource {
    public static byte[] getByteArray(String path) throws Throwable {
        URL url = Thread.currentThread().getContextClassLoader().getResource(path);

        if(url == null){
            throw new DbbackupException(String.format("%s não encontrado", path));
        }

        return Files.readAllBytes(new File(url.getPath()).toPath());
    }

    public static String getString(String path) throws Throwable {
        return new String(getByteArray(path));
    }
}
