package br.dbbackup.core;


import java.io.Writer;
import java.util.List;

public interface DatabaseInterface {
    List<String> getTables();
    List<String> getColumns(String table);
    Writer getSqlWriter(String owner, String table);
}
