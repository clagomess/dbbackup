package br.dbbackup.sgbd;

import br.dbbackup.constant.DataType;
import br.dbbackup.core.DbbackupException;
import br.dbbackup.core.LobWriter;
import br.dbbackup.dto.OptionsDto;
import lombok.extern.slf4j.Slf4j;

import java.io.UnsupportedEncodingException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Map;

@Slf4j
public class Postgresql implements SgbdImpl {
    @Override
    public String getSqlTabColumns(OptionsDto options) {
        String sql = "select c.table_name, c.column_name, c.udt_name as data_type\n" +
                "from information_schema.columns c\n" +
                "JOIN information_schema.tables t\n" +
                "  on t.table_catalog = c.table_catalog\n" +
                "  AND t.table_schema = c.table_schema\n" +
                "  AND t.table_name = c.table_name\n" +
                "where c.table_schema = '%s' AND t.table_type <> 'VIEW'\n";

        sql = String.format(sql, options.getSchema());

        if(options.getTable() != null){
            sql += String.format("and c.table_name in ('%s')", String.join("','", options.getTable()));
        }

        return sql;
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
            default:
                return DataType.DEFAULT;
        }
    }

    @Override
    public String formatColumn(OptionsDto options, Map<String, Map<String, String>> tabcolumns, ResultSet rs, String table, String column) throws Throwable {
        String toReturn = "null";

        try {
            if(rs.getObject(column) != null){
                SimpleDateFormat sdf;

                switch (options.getSgbdFromInstance().getDataType(tabcolumns.get(table).get(column))){
                    case NUMBER:
                        toReturn = rs.getString(column);
                        break;
                    case DATETIME:
                        toReturn = "to_timestamp('%s', 'YYYY-MM-DD HH24:MI:SS')";

                        sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                        toReturn = String.format(toReturn, sdf.format(rs.getTimestamp(column)));
                        break;
                    case DATE:
                        toReturn = "to_date('%s', 'YYYY-MM-DD')";

                        sdf = new SimpleDateFormat("yyyy-MM-dd");

                        toReturn = String.format(toReturn, sdf.format(rs.getTimestamp(column)));
                        break;
                    case TIME:
                        toReturn = "to_timestamp('%s', 'HH24:MI:SS')";

                        sdf = new SimpleDateFormat("HH:mm:ss");

                        toReturn = String.format(toReturn, sdf.format(rs.getTimestamp(column)));
                        break;
                    case BLOB:
                        if(rs.getBytes(column).length >= 0 && options.getExportLob()){
                            toReturn = LobWriter.write(options, rs.getBytes(column));
                        }
                        break;
                    case CLOB:
                        toReturn = LobWriter.write(options, rs.getString(column).getBytes("UTF-8"));
                        toReturn = String.format("encode(%s, 'escape')", toReturn);
                        break;
                    case VARCHAR:
                        toReturn = "CONVERT_FROM(DECODE('%s', 'BASE64'), 'UTF-8')";
                        toReturn = String.format(toReturn, Base64.getEncoder().encodeToString(rs.getString(column).getBytes("UTF-8")));
                        break;
                    default:
                        toReturn = "'%s'";
                        toReturn = String.format(toReturn, rs.getString(column));
                }
            }
        }catch (SQLException | UnsupportedEncodingException e){
            log.warn(Postgresql.class.getName(), e);
            throw new DbbackupException(e.getMessage());
        }

        return toReturn;
    }
}
