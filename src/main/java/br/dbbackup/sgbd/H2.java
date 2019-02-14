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

@Slf4j
public class H2 implements SgbdImpl {
    @Override
    public String getSqlTabColumns(OptionsDto options) {
        String sql = "SELECT TABLE_NAME AS \"table_name\", COLUMN_NAME AS \"column_name\", TYPE_NAME AS \"data_type\"\n" +
                "FROM INFORMATION_SCHEMA.COLUMNS\n" +
                "WHERE TABLE_SCHEMA = '%s'\n";

        sql = String.format(sql, options.getSchema());

        if(options.getTable() != null){
            sql += String.format("AND TABLE_NAME IN ('%s')", String.join("','", options.getTable()));
        }

        return sql;
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

        try {
            if(rs.getObject(column) != null){
                SimpleDateFormat sdf;

                switch (options.getSgbdFromInstance().getDataType(tabcolumns.getDataType(table, column))){
                    case NUMBER:
                        toReturn = rs.getString(column);
                        break;
                    case DATETIME:
                        toReturn = "PARSEDATETIME('%s', 'yyyy-MM-dd HH:mm:ss')";

                        sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                        toReturn = String.format(toReturn, sdf.format(rs.getTimestamp(column)));
                        break;
                    case DATE:
                        toReturn = "PARSEDATETIME('%s', 'yyyy-MM-dd')";

                        sdf = new SimpleDateFormat("yyyy-MM-dd");

                        toReturn = String.format(toReturn, sdf.format(rs.getTimestamp(column)));
                        break;
                    case TIME:
                        toReturn = "PARSEDATETIME('%s', 'HH:mm:ss')";

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
                        toReturn = String.format("hextoraw('%s')", encodeHex(rs.getString(column).getBytes()));
                        break;
                    case BOOL:
                        toReturn = rs.getBoolean(column) ? "1" : "0";
                        break;
                    default:
                        toReturn = "'%s'";
                        toReturn = String.format(toReturn, rs.getString(column));
                }
            }
        } catch (SQLException | UnsupportedEncodingException e){
            log.warn(Sgbd.class.getName(), e);
            throw new DbbackupException(e.getMessage());
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
