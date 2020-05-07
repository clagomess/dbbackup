package br.dbbackup.sgbd;

import br.dbbackup.constant.DataType;
import br.dbbackup.core.Format;
import br.dbbackup.core.LobWriter;
import br.dbbackup.core.Resource;
import br.dbbackup.dto.OptionsDto;
import br.dbbackup.dto.TabColumnsDto;
import lombok.extern.slf4j.Slf4j;

import java.sql.ResultSet;
import java.util.Base64;

@Slf4j
public class Postgresql implements SgbdImpl {
    @Override
    public String getQuote(){
        return "\"";
    }

    @Override
    public String getSqlTabColumns() throws Throwable {
        return Resource.getString("sql/postgresql_tabcolumns.sql");
    }

    @Override
    public String getSqlInfo() throws Throwable {
        return Resource.getString("sql/postgresql_info.sql");
    }

    @Override
    public DataType getDataType(String dataType) {
        switch (dataType){
            case "float8":
            case "numeric":
            case "int4":
            case "int8":
            case "int2":
                return DataType.NUMBER;
            case "timestamptz":
            case "timestamp":
                return DataType.DATETIME;
            case "time":
                return DataType.TIME;
            case "date":
                return DataType.DATE;
            case "bytea":
                return DataType.BLOB;
            case "text":
                return DataType.CLOB;
            case "varchar":
                return DataType.VARCHAR;
            case "bool":
                return DataType.BOOL;
            default:
                return DataType.DEFAULT;
        }
    }

    @Override
    public int getDataTypePrecision(String dataType) {
        switch (dataType){
            case "numeric":
                return 2;
            case "varchar":
                return 1;
            default:
                return 0;
        }
    }

    @Override
    public String formatColumn(OptionsDto options, TabColumnsDto tabcolumns, ResultSet rs, String table, String column) throws Throwable {
        String toReturn = "null";

        if(rs.getObject(column) == null){
            return toReturn;
        }

        switch (options.getSgbdFromInstance().getDataType(tabcolumns.getDataType(table, column))){
            case NUMBER:
                toReturn = rs.getString(column);
                break;
            case DATETIME:
                toReturn = "to_timestamp('%s', 'YYYY-MM-DD HH24:MI:SS')";
                toReturn = String.format(toReturn, Format.dateTime(rs.getTimestamp(column)));
                break;
            case DATE:
                toReturn = "to_date('%s', 'YYYY-MM-DD')";
                toReturn = String.format(toReturn, Format.date(rs.getTimestamp(column)));
                break;
            case TIME:
                toReturn = "to_timestamp('%s', 'HH24:MI:SS')";
                toReturn = String.format(toReturn, Format.time(rs.getTimestamp(column)));
                break;
            case BLOB:
                toReturn = rs.getBytes(column).length == 0 ? "''" : LobWriter.write(options, rs.getBytes(column));
                break;
            case CLOB:
                toReturn = LobWriter.write(options, rs.getString(column).getBytes(options.getCharset()));
                toReturn = String.format("encode(%s, 'escape')", toReturn);
                break;
            case VARCHAR:
                toReturn = "CONVERT_FROM(DECODE('%s', 'BASE64'), 'UTF-8')";
                toReturn = String.format(toReturn, Base64.getEncoder().encodeToString(rs.getString(column).getBytes(options.getCharset())));
                break;
            case BOOL:
                toReturn = rs.getBoolean(column) ? "true" : "false";
                break;
            default:
                toReturn = "'%s'";
                toReturn = String.format(toReturn, rs.getString(column));
        }

        return toReturn;
    }
}
