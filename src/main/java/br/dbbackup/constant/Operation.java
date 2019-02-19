package br.dbbackup.constant;

import lombok.Getter;

public enum Operation {
    GET("GET"),
    PUT("PUT"),
    INFO("INFO");

    @Getter
    private final String value;

    Operation(String value){
        this.value = value;
    }
}
