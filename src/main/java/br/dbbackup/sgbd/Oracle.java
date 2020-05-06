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
public class Oracle implements SgbdImpl {
    @Override
    public String getQuote(){
        return "\"";
    }

    @Override
    public String getSqlTabColumns() throws Throwable {
        return Resource.getString("sql/oracle_tabcolumns.sql");
    }

    @Override
    public String getSqlInfo() throws Throwable {
        return Resource.getString("sql/oracle_info.sql");
    }

    @Override
    public DataType getDataType(String dataType) {
        switch (dataType){
            case "NUMBER":
                return DataType.NUMBER;
            case "DATE":
                return DataType.DATETIME;
            case "BLOB":
                return DataType.BLOB;
            case "CLOB":
                return DataType.CLOB;
            case "VARCHAR2":
                return DataType.VARCHAR;
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
            case DATETIME:
            case DATE:
            case TIME:
                toReturn = "TO_DATE('%s', 'YYYY-MM-DD HH24:MI:SS')";
                toReturn = String.format(toReturn, Format.dateTime(rs.getTimestamp(column)));
                break;
            case BLOB:
                toReturn = rs.getBytes(column).length == 0 ? "EMPTY_BLOB()" : LobWriter.write(options, rs.getBytes(column));
                break;
            case CLOB:
                toReturn = LobWriter.write(options, rs.getString(column).getBytes(options.getCharset()));
                break;
            case VARCHAR:
                toReturn = "UTL_RAW.CAST_TO_VARCHAR2(UTL_ENCODE.BASE64_DECODE(UTL_RAW.CAST_TO_RAW('%s')))";
                toReturn = String.format(toReturn, Base64.getEncoder().encodeToString(rs.getString(column).getBytes(options.getCharset())));
                break;
            case BOOL:
                toReturn = rs.getBoolean(column) ? "1" : "0";
                break;
            default:
                toReturn = String.format("'%s'", rs.getString(column));
        }

        return toReturn;
    }
}
