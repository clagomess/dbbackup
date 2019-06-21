package br.dbbackup.sgbd;

import br.dbbackup.constant.DataType;
import br.dbbackup.core.LobWriter;
import br.dbbackup.core.Resource;
import br.dbbackup.dto.OptionsDto;
import br.dbbackup.dto.TabColumnsDto;
import lombok.extern.slf4j.Slf4j;

import java.sql.ResultSet;

@Slf4j
public class Sqlite implements SgbdImpl {
    @Override
    public String getSqlTabColumns() throws Throwable {
        return Resource.getString("sql/sqlite_tabcolumns.sql");
    }

    @Override
    public String getSqlInfo() throws Throwable {
        return Resource.getString("sql/sqlite_info.sql");
    }

    @Override
    public DataType getDataType(String dataType) {
        switch (dataType){
            case "integer":
            case "numeric":
            case "real":
                return DataType.NUMBER;
            case "blob":
                return DataType.BLOB;
            default:
                return DataType.DEFAULT;
        }
    }

    @Override
    public String formatColumn(OptionsDto options, TabColumnsDto tabcolumns, ResultSet rs, String table, String column) throws Throwable {
        String toReturn = "NULL";

        if(rs.getObject(column) == null){
            return toReturn;
        }

        switch (options.getSgbdFromInstance().getDataType(tabcolumns.getDataType(table, column))){
            case NUMBER:
                toReturn = rs.getString(column);
                break;
            case BLOB:
                toReturn = rs.getBytes(column).length == 0 ? "''" : LobWriter.write(options, rs.getBytes(column));
                break;
            case CLOB:
                toReturn = LobWriter.write(options, rs.getString(column).getBytes(options.getCharset()));
                break;
            default:
                toReturn = "'%s'";
                toReturn = String.format(toReturn, rs.getString(column));
        }

        return toReturn;
    }
}
