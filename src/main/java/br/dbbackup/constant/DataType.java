package br.dbbackup.constant;

import lombok.Getter;

public enum DataType {
    NUMBER("NUMBER"),
    DATE("DATE"), // yyyy-MM-dd
    DATETIME("DATETIME"), // yyyy-MM-dd HH:mm:ss
    TIME("TIME"), // HH:mm:ss
    BLOB("BLOB"), // lob file
    CLOB("CLOB"), // lob file
    VARCHAR("VARCHAR"), // Base64
    DEFAULT("DEFAULT");

    @Getter
    private final String value;

    DataType(String value){
        this.value = value;
    }
}
