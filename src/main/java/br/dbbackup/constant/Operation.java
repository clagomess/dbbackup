package br.dbbackup.constant;

import lombok.Getter;

public enum Operation {
    GET("GET"),
    PUT("PUT"),
    INFO("INFO"),
    DDL("DDL");

    @Getter
    private final String value;

    Operation(String value){
        this.value = value;
    }
}
