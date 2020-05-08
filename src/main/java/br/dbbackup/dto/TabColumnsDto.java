package br.dbbackup.dto;

import br.dbbackup.constant.DataType;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class TabColumnsDto {
    // table_name -> column_name -> type
    private final Map<String, Map<String, TabColumnInfoDto>> tabcolumns = new TreeMap<>();
    private final OptionsDto options;

    public TabColumnsDto(OptionsDto options){
        this.options = options;
    }

    public List<String> getColumns(String table) {
        List<String> columns = new LinkedList<>();

        for(String column : tabcolumns.get(table).keySet()){
            DataType dataType = options.getSgbdFromInstance().getDataType(getDataType(table, column));

            if((dataType == DataType.BLOB || dataType == DataType.CLOB) && !options.getExportLob()){
                continue;
            }

            columns.add(column);
        }

        return columns;
    }

    public void setTabColumn(String table, String column, String type, boolean nullable, Long precision, Long scale) {
        if(!tabcolumns.containsKey(table)){
            tabcolumns.put(table, new TreeMap<>());
        }

        tabcolumns.get(table).put(column, new TabColumnInfoDto(type, nullable, precision, scale));
    }

    public List<String> getTables() {
        return new LinkedList<>(tabcolumns.keySet());
    }

    public String getDataType(String table, String column){
        return tabcolumns.get(table).get(column).getType();
    }

    public TabColumnInfoDto getColInfo(String table, String column){
        return tabcolumns.get(table).get(column);
    }
}
