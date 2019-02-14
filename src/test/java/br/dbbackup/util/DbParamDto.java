package br.dbbackup.util;

import lombok.Data;

@Data
public class DbParamDto {
    private String url;
    private String schema;
    private String user = "";
    private String pass = "";

    public DbParamDto(String url, String schema, String user, String pass){
        this.url = url;
        this.schema = schema;
        this.user = user;
        this.pass = pass;
    }

    public DbParamDto(String url, String schema, String user){
        this.url = url;
        this.schema = schema;
        this.user = user;
    }

    public DbParamDto(String url, String schema){
        this.url = url;
        this.schema = schema;
    }
}
