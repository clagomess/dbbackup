package br.dbbackup.core;

import org.apache.commons.io.IOUtils;

import java.net.URL;

public class Resource {
    public static byte[] getByteArray(String path) throws Throwable {
        URL url = Thread.currentThread().getContextClassLoader().getResource(path);

        if(url == null){
            throw new DbbackupException(String.format("%s não encontrado", path));
        }

        return IOUtils.toByteArray(url);
    }

    public static String getString(String path) throws Throwable {
        return new String(getByteArray(path));
    }
}
