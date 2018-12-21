package br.dbbackup.sgbd;

import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;

@Slf4j
public class H2Test {
    @Test
    public void encodeHex() {
        H2 h2 = new H2();

        Assert.assertEquals("0042004c004f0042", h2.encodeHex("BLOB".getBytes()));
    }
}
