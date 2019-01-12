package br.dbbackup.core;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.concurrent.ThreadLocalRandom;

@Slf4j
public class MultithreadTest {
    @Test
    public void run() throws Throwable {
        Multithread mt = new Multithread();

        for(int i = 0; i <= 30; i++){
            final int objnum = i;

            mt.run(new Thread(() -> {
                log.info("objeto n: {}", objnum);

                try {
                    Thread.sleep(ThreadLocalRandom.current().nextInt(100, 3000));
                } catch (InterruptedException e) {
                    log.error(e.getMessage());
                }
            }));
        }
    }
}
