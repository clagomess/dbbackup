package br.dbbackup.dto;

import br.dbbackup.constant.DataType;

import java.util.*;

public class TabColumnsDto {
    // table_name -> column_name -> type
    private Map<String, Map<String, String>> tabcolumns = new HashMap<>();
    private OptionsDto options;

    public TabColumnsDto(OptionsDto options){
        this.options = options;
    }

    public List<String> getColumns(String table) {
        List<String> columns = new LinkedList<>();

        for(String column : tabcolumns.get(table).keySet()){
            DataType dataType = options.getSgbdFromInstance().getDataType(tabcolumns.get(table).get(column));

            if((dataType == DataType.BLOB || dataType == DataType.CLOB) && options.getExportLob()){
                continue;
            }

            columns.add(column);
        }

        return columns;
    }

    public void setTabColumn(String table, String column, String type) {
        if(!tabcolumns.containsKey(table)){
            tabcolumns.put(table, new HashMap<>());
        }

        tabcolumns.get(table).put(column, type);
    }

    public List<String> getTables() {
        return new LinkedList<>(tabcolumns.keySet());
    }

    public String getDataType(String table, String column){
        return tabcolumns.get(table).get(column);
    }
}
