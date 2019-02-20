package br.dbbackup.constant;

import lombok.Getter;

public enum Database {
    ORACLE("ORACLE"),
    MYSQL("MYSQL"),
    MARIADB("MARIADB"),
    POSTGRESQL("POSTGRESQL"),
    H2("H2"),
    SQLITE("SQLITE");

    @Getter
    private final String value;

    Database(String value){
        this.value = value;
    }
}
