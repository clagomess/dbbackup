package br.dbbackup.sgbd;

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
public class Mysql implements SgbdImpl {
    protected static String SQL_TAB_COLUMNS = "select table_name, column_name, data_type " +
            "from information_schema.columns where table_schema = '%s'";
    String TAB_COLUMN_TABLE_NAME = "table_name";
    String TAB_COLUMN_COLUMN_NAME = "column_name";
    String TAB_COLUMN_DATA_TYPE = "data_type";

    public static String formatColumn(OptionsDto options, Map<String, Map<String, String>> tabcolumns, ResultSet rs, String table, String column) throws Throwable {
        String toReturn = "null";

        try {
            if(rs.getObject(column) != null){
                SimpleDateFormat sdf;

                switch (tabcolumns.get(table).get(column)){
                    case "int":
                    case "bigint":
                    case "decimal":
                    case "tinyint":
                        toReturn = rs.getString(column);
                        break;
                    case "datetime":
                        toReturn = "str_to_date('%s', '%%Y-%%m-%%d %%H:%%i:%%s')";

                        sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                        toReturn = String.format(toReturn, sdf.format(rs.getTimestamp(column)));
                        break;
                    case "date":
                        toReturn = "str_to_date('%s', '%%Y-%%m-%%d')";

                        sdf = new SimpleDateFormat("yyyy-MM-dd");

                        toReturn = String.format(toReturn, sdf.format(rs.getTimestamp(column)));
                        break;
                    case "blob":
                    case "longblob":
                    case "longtext":
                        if(rs.getBytes(column).length == 0 || !options.getExportLob()){
                            toReturn = "null";
                        }else{
                            toReturn = LobWriter.write(rs.getBytes(column));
                            toReturn = ":lob_" + toReturn;
                        }
                        break;
                    case "varchar":
                    case "text":
                        toReturn = "from_base64('%s')";
                        toReturn = String.format(toReturn, Base64.getEncoder().encodeToString(rs.getString(column).getBytes("UTF-8")));
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
