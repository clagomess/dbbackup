package br.dbbackup.sgbd;

import br.dbbackup.constant.DataType;
import br.dbbackup.core.DbbackupException;
import br.dbbackup.core.LobWriter;
import br.dbbackup.dto.OptionsDto;
import br.dbbackup.dto.TabColumnsDto;
import lombok.extern.slf4j.Slf4j;

import java.io.UnsupportedEncodingException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Base64;

@Slf4j
public class Mysql implements SgbdImpl {
    @Override
    public String getSqlTabColumns(OptionsDto options) {
        String sql = "select table_name, column_name, data_type\n" +
                "from information_schema.columns where table_schema = '%s'\n";

        sql = String.format(sql, options.getSchema());

        if(options.getTable() != null){
            sql += String.format("and table_name in ('%s')", String.join("','", options.getTable()));
        }

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

        try {
            if(rs.getObject(column) != null){
                SimpleDateFormat sdf;

                switch (options.getSgbdFromInstance().getDataType(tabcolumns.getDataType(table, column))){
                    case NUMBER:
                        toReturn = rs.getString(column);
                        break;
                    case DATETIME:
                        toReturn = "str_to_date('%s', '%%Y-%%m-%%d %%H:%%i:%%s')";

                        sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                        toReturn = String.format(toReturn, sdf.format(rs.getTimestamp(column)));
                        break;
                    case DATE:
                        toReturn = "str_to_date('%s', '%%Y-%%m-%%d')";

                        sdf = new SimpleDateFormat("yyyy-MM-dd");

                        toReturn = String.format(toReturn, sdf.format(rs.getTimestamp(column)));
                        break;
                    case TIME:
                        toReturn = "str_to_date('%s', '%%H:%%i:%%s')";

                        sdf = new SimpleDateFormat("HH:mm:ss");

                        toReturn = String.format(toReturn, sdf.format(rs.getTimestamp(column)));
                        break;
                    case CLOB:
                        toReturn = LobWriter.write(options, rs.getString(column).getBytes("UTF-8"));
                        break;
                    case BLOB:
                        if(rs.getBytes(column).length >= 0 && options.getExportLob()){
                            toReturn = LobWriter.write(options, rs.getBytes(column));
                        }
                        break;
                    case VARCHAR:
                        toReturn = "from_base64('%s')";
                        toReturn = String.format(toReturn, Base64.getEncoder().encodeToString(rs.getString(column).getBytes("UTF-8")));
                        break;
                    case BOOL:
                        toReturn = rs.getBoolean(column) ? "1" : "0";
                        break;
                    default:
                        toReturn = "'%s'";
                        toReturn = String.format(toReturn, rs.getString(column));
                }
            }
        }catch (SQLException | UnsupportedEncodingException e){
            log.warn(Mysql.class.getName(), e);
            throw new DbbackupException(e.getMessage());
        }

        return toReturn;
    }
}
