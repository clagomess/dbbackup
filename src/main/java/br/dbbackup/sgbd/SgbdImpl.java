package br.dbbackup.sgbd;

import br.dbbackup.constant.DataType;
import br.dbbackup.dto.OptionsDto;

import java.sql.ResultSet;
import java.util.Map;

public interface SgbdImpl {
    // CRIAR SELECT COM OS CAMPOS: table_name, column_name, data_type
    String getSqlTabColumns();
    String formatColumn(OptionsDto options, Map<String, Map<String, String>> tabcolumns, ResultSet rs, String table, String column) throws Throwable;
    DataType getDataType(String dataType);
}
