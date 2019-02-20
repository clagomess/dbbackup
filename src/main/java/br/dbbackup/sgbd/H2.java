package br.dbbackup.sgbd;

import br.dbbackup.constant.DataType;
import br.dbbackup.core.Format;
import br.dbbackup.core.LobWriter;
import br.dbbackup.core.Resource;
import br.dbbackup.dto.OptionsDto;
import br.dbbackup.dto.TabColumnsDto;
import lombok.extern.slf4j.Slf4j;

import java.sql.ResultSet;

@Slf4j
public class H2 implements SgbdImpl {
    @Override
    public String getSqlTabColumns() throws Throwable {
        return Resource.getString("sql/h2_tabcolumns.sql");
    }

    @Override
    public String getSqlInfo() throws Throwable {
        return Resource.getString("sql/h2_info.sql");
    }

    @Override
    public DataType getDataType(String dataType) {
        switch (dataType){
            case "BIGINT":
            case "NUMERIC":
            case "DECIMAL":
            case "INTEGER":
            case "FLOAT":
            case "REAL":
            case "DOUBLE":
            case "SMALLINT":
                return DataType.NUMBER;
            case "TIMESTAMP":
                return DataType.DATETIME;
            case "TIME":
                return DataType.TIME;
            case "DATE":
                return DataType.DATE;
            case "BLOB":
                return DataType.BLOB;
            case "CLOB":
                return DataType.CLOB;
            case "VARCHAR":
            case "VARCHAR_IGNORECASE":
                return DataType.VARCHAR;
            case "BOOLEAN":
                return DataType.BOOL;
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
                toReturn = "PARSEDATETIME('%s', 'yyyy-MM-dd HH:mm:ss')";
                toReturn = String.format(toReturn, Format.dateTime(rs.getTimestamp(column)));
                break;
            case DATE:
                toReturn = "PARSEDATETIME('%s', 'yyyy-MM-dd')";
                toReturn = String.format(toReturn, Format.date(rs.getTimestamp(column)));
                break;
            case TIME:
                toReturn = "PARSEDATETIME('%s', 'HH:mm:ss')";
                toReturn = String.format(toReturn, Format.time(rs.getTimestamp(column)));
                break;
            case CLOB:
                toReturn = LobWriter.write(options, rs.getString(column).getBytes(options.getCharset()));
                break;
            case BLOB:
                toReturn = LobWriter.write(options, rs.getBytes(column));
                break;
            case VARCHAR:
                toReturn = String.format("hextoraw('%s')", encodeHex(rs.getString(column).getBytes()));
                break;
            case BOOL:
                toReturn = rs.getBoolean(column) ? "1" : "0";
                break;
            default:
                toReturn = "'%s'";
                toReturn = String.format(toReturn, rs.getString(column));
        }

        return toReturn;
    }

    String encodeHex(byte[] bytes){
        StringBuilder sb = new StringBuilder();

        for (byte aByte : bytes) {
            sb.append(String.format("%04X", aByte));
        }

        return sb.toString().toLowerCase();
    }
}
