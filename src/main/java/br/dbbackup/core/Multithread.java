package br.dbbackup.core;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class Multithread {
    @Getter
    private int cores;
    private Map<Integer, Thread> threads;

    public Multithread(){
        this.cores = Runtime.getRuntime().availableProcessors();
        this.threads = new HashMap<>();

        log.info("N threads: {}", this.cores);

        // preenchendo
        for(int i = 0; i < cores; i++){
            this.threads.put(i, null);
        }
    }

    public void run(Thread thread) throws Throwable {
        int currentThread = 0;

        while(true){
            if(threads.get(currentThread) == null || !threads.get(currentThread).isAlive()){
                threads.put(currentThread, thread);
                threads.get(currentThread).start();
                log.info("Rodando thread {}", currentThread);
                break;
            }

            currentThread++;
            if(currentThread == cores){
                currentThread = 0;
            }

            Thread.sleep(10);
        }
    }
}
