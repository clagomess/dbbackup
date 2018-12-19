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
public class Oracle implements SgbdImpl {
    @Override
    public String getSqlTabColumns(OptionsDto options) {
        String sql = "SELECT SATC.TABLE_NAME as \"table_name\", SATC.COLUMN_NAME as \"column_name\", SATC.DATA_TYPE as \"data_type\"\n" +
                "FROM SYS.ALL_TAB_COLUMNS SATC\n" +
                "JOIN SYS.ALL_TABLES SAT ON SAT.OWNER = SATC.OWNER AND SAT.TABLE_NAME = SATC.TABLE_NAME\n" +
                "WHERE SATC.OWNER = '%s'\n";

        sql = String.format(sql, options.getSchema());

        if(options.getTable() != null){
            sql += String.format("AND SATC.TABLE_NAME IN ('%s')", String.join("','", options.getTable()));
        }

        return sql;
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
    public String formatColumn(OptionsDto options, Map<String, Map<String, String>> tabcolumns, ResultSet rs, String table, String column) throws DbbackupException {
        String toReturn = "NULL";

        try {
            if(rs.getObject(column) != null){
                switch (getDataType(tabcolumns.get(table).get(column))){
                    case NUMBER:
                        toReturn = rs.getString(column);
                        break;
                    case DATETIME:
                    case DATE:
                    case TIME:
                        toReturn = "TO_DATE('%s', 'YYYY-MM-DD HH24:MI:SS')";

                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                        toReturn = String.format(toReturn, sdf.format(rs.getTimestamp(column)));
                        break;
                    case BLOB:
                        if(rs.getBytes(column).length == 0 || !options.getExportLob()){
                            toReturn = "EMPTY_BLOB()";
                        }else{
                            toReturn = LobWriter.write(options, rs.getBytes(column));
                            toReturn = ":lob_" + toReturn;
                        }
                        break;
                    case CLOB:
                        if(rs.getString(column) == null || !options.getExportLob()){
                            toReturn = "EMPTY_CLOB()";
                        }else{
                            toReturn = LobWriter.write(options, rs.getString(column).getBytes("UTF-8"));
                            toReturn = ":lob_" + toReturn;
                        }
                        break;
                    case VARCHAR:
                        toReturn = "UTL_RAW.CAST_TO_VARCHAR2(UTL_ENCODE.BASE64_DECODE(UTL_RAW.CAST_TO_RAW('%s')))";
                        toReturn = String.format(toReturn, Base64.getEncoder().encodeToString(rs.getString(column).getBytes("UTF-8")));
                        break;
                    default:
                        toReturn = String.format("'%s'", rs.getString(column));
                }
            }
        } catch (SQLException | UnsupportedEncodingException e){
            log.warn(Sgbd.class.getName(), e);
            throw new DbbackupException(e.getMessage());
        }

        return toReturn;
    }
}
