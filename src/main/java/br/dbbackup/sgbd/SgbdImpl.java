package br.dbbackup.sgbd;

import br.dbbackup.dto.OptionsDto;

import java.sql.ResultSet;
import java.util.Map;

public interface SgbdImpl {
    String getSqlTabColumns();
    String getTabColumnTableName();
    String getTabColumnColumnName();
    String getTabColumnDataType();

    String formatColumn(OptionsDto options, Map<String, Map<String, String>> tabcolumns, ResultSet rs, String table, String column) throws Throwable;
}
