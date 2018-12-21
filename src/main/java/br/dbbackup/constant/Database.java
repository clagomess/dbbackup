package br.dbbackup.constant;

import lombok.Getter;

public enum Database {
    ORACLE("ORACLE"),
    MYSQL("MYSQL"),
    POSTGRESQL("POSTGRESQL");

    @Getter
    private final String value;

    Database(String value){
        this.value = value;
    }
}
