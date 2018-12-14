package br.dbbackup.sgbd;

import br.dbbackup.dto.OptionsDto;

import java.sql.ResultSet;
import java.util.Map;

public interface SgbdImpl {
    String SQL_TAB_COLUMNS = "";
    String TAB_COLUMN_TABLE_NAME = "";
    String TAB_COLUMN_COLUMN_NAME = "";
    String TAB_COLUMN_DATA_TYPE = "";

    static String formatColumn(OptionsDto options, Map<String, Map<String, String>> tabcolumns, ResultSet rs, String table, String column) throws Throwable {
        throw new IllegalAccessError("Implements!");
    }

    static Map<String, Map<String, String>> setTabColumn(String table, String column, String type) {
        throw new IllegalAccessError("Implements!");
    }
}
