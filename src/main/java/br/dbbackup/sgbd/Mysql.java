package br.dbbackup.sgbd;

import br.dbbackup.constant.DataType;
import br.dbbackup.core.Format;
import br.dbbackup.core.LobWriter;
import br.dbbackup.dto.OptionsDto;
import br.dbbackup.dto.TabColumnsDto;
import lombok.extern.slf4j.Slf4j;

import java.sql.ResultSet;
import java.util.Base64;

@Slf4j
public class Mysql implements SgbdImpl {
    @Override
    public String getSqlTabColumns(OptionsDto options) {
        String sql = "select table_name, column_name, data_type\n" +
                "from information_schema.columns where table_schema = '%s'\n";

        sql = String.format(sql, options.getSchema());

        if(options.getTable() != null){
            sql += String.format("and table_name in ('%s')\n", String.join("','", options.getTable()));
        }

        sql += "order by table_name, ordinal_position";

        return sql;
    }

    @Override
    public DataType getDataType(String dataType) {
        switch (dataType){
            case "int":
            case "bigint":
            case "decimal":
            case "tinyint":
                return DataType.NUMBER;
            case "datetime":
                return DataType.DATETIME;
            case "date":
                return DataType.DATE;
            case "time":
                return DataType.TIME;
            case "blob":
            case "longblob":
                return DataType.BLOB;
            case "longtext":
                return DataType.CLOB;
            case "varchar":
            case "text":
                return DataType.VARCHAR;
            default:
                return DataType.DEFAULT;
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
                toReturn = "str_to_date('%s', '%%Y-%%m-%%d %%H:%%i:%%s')";
                toReturn = String.format(toReturn, Format.dateTime(rs.getTimestamp(column)));
                break;
            case DATE:
                toReturn = "str_to_date('%s', '%%Y-%%m-%%d')";
                toReturn = String.format(toReturn, Format.date(rs.getTimestamp(column)));
                break;
            case TIME:
                toReturn = "str_to_date('%s', '%%H:%%i:%%s')";
                toReturn = String.format(toReturn, Format.time(rs.getTimestamp(column)));
                break;
            case CLOB:
                toReturn = LobWriter.write(options, rs.getString(column).getBytes(options.getCharset()));
                break;
            case BLOB:
                toReturn = rs.getBytes(column).length == 0 ? "''" : LobWriter.write(options, rs.getBytes(column));
                break;
            case VARCHAR:
                toReturn = "from_base64('%s')";
                toReturn = String.format(toReturn, Base64.getEncoder().encodeToString(rs.getString(column).getBytes(options.getCharset())));
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
}
