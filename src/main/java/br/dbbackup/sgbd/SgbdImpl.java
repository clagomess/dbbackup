package br.dbbackup.sgbd;

import br.dbbackup.constant.DataType;
import br.dbbackup.dto.OptionsDto;
import br.dbbackup.dto.TabColumnsDto;

import java.sql.ResultSet;

public interface SgbdImpl {
    // CRIAR SELECT COM OS CAMPOS: table_name, column_name, data_type
    String getSqlTabColumns() throws Throwable;
    String getSqlInfo() throws Throwable;
    String formatColumn(OptionsDto options, TabColumnsDto tabcolumns, ResultSet rs, String table, String column) throws Throwable;
    DataType getDataType(String dataType);
}
