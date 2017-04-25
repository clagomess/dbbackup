package br.dbbackup.core;


import java.util.List;

public interface DatabaseInterface {
    List<String> getTables();
    List<String> getColumns(String table);
}
