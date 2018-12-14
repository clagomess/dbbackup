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
public class Postgresql implements SgbdImpl {
    private final String SQL_TAB_COLUMNS = "select c.table_name, c.column_name, c.udt_name\n" +
            "from information_schema.columns c\n" +
            "JOIN information_schema.tables t\n" +
            "  on t.table_catalog = c.table_catalog\n" +
            "  AND t.table_schema = c.table_schema\n" +
            "  AND t.table_name = c.table_name\n" +
            "where c.table_schema = '%s' AND t.table_type <> 'VIEW'";

    String TAB_COLUMN_TABLE_NAME = "table_name";
    String TAB_COLUMN_COLUMN_NAME = "column_name";
    String TAB_COLUMN_DATA_TYPE = "udt_name";

    public String formatColumn(OptionsDto options, Map<String, Map<String, String>> tabcolumns, ResultSet rs, String table, String column) throws DbbackupException {
        String toReturn = "null";

        try {
            if(rs.getObject(column) != null){
                SimpleDateFormat sdf;

                switch (tabcolumns.get(table).get(column)){
                    case "float8":
                    case "numeric":
                    case "int4":
                    case "int8":
                    case "int2":
                        toReturn = rs.getString(column);
                        break;
                    case "timestamptz":
                    case "timestamp":
                        toReturn = "to_date('%s', 'YYYY-MM-DD HH24:MI:SS')";

                        sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                        toReturn = String.format(toReturn, sdf.format(rs.getTimestamp(column)));
                        break;
                    case "date":
                        toReturn = "to_date('%s', 'YYYY-MM-DD')";

                        sdf = new SimpleDateFormat("yyyy-MM-dd");

                        toReturn = String.format(toReturn, sdf.format(rs.getTimestamp(column)));
                        break;
                    case "text":
                        if(rs.getBytes(column).length == 0 || !options.getExportLob()){
                            toReturn = "null";
                        }else{
                            toReturn = LobWriter.write(rs.getBytes(column));
                            toReturn = ":lob_" + toReturn;
                        }
                        break;
                    case "varchar":
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
