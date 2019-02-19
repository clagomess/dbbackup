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
public class Oracle implements SgbdImpl {
    @Override
    public String getSqlTabColumns(OptionsDto options) {
        String sql = "SELECT SATC.TABLE_NAME as \"table_name\", SATC.COLUMN_NAME as \"column_name\", SATC.DATA_TYPE as \"data_type\"\n" +
                "FROM SYS.ALL_TAB_COLUMNS SATC\n" +
                "JOIN SYS.ALL_TABLES SAT ON SAT.OWNER = SATC.OWNER AND SAT.TABLE_NAME = SATC.TABLE_NAME\n" +
                "LEFT JOIN SYS.ALL_MVIEWS SAM ON SAM.OWNER = SATC.OWNER AND SAM.MVIEW_NAME = SATC.TABLE_NAME\n" +
                "WHERE SAM.MVIEW_NAME IS NULL AND SATC.OWNER = '%s'\n";

        sql = String.format(sql, options.getSchema());

        if(options.getTable() != null){
            sql += String.format("AND SATC.TABLE_NAME IN ('%s')\n", String.join("','", options.getTable()));
        }

        sql += "ORDER BY SATC.TABLE_NAME, SATC.COLUMN_ID";

        return sql;
    }

    @Override
    public String getSqlInfo(OptionsDto options){
        String sql = "SELECT\n" +
                "   SATC.TABLE_NAME as \"table_name\",\n" +
                "   count(*) \"qtd_columns\",\n" +
                "   MAX(PK.COLUMN_NAME) \"pk_column\",\n" +
                "   MAX(CASE WHEN SATC.DATA_TYPE IN ('BLOB', 'CLOB') THEN 1 ELSE 0 END) \"lob\"\n" +
                "FROM SYS.ALL_TAB_COLUMNS SATC\n" +
                "JOIN SYS.ALL_TABLES SAT ON SAT.OWNER = SATC.OWNER AND SAT.TABLE_NAME = SATC.TABLE_NAME\n" +
                "LEFT JOIN SYS.ALL_MVIEWS SAM ON SAM.OWNER = SATC.OWNER AND SAM.MVIEW_NAME = SATC.TABLE_NAME\n" +
                "LEFT JOIN (\n" +
                "  SELECT SACC.TABLE_NAME, LISTAGG(SACC.COLUMN_NAME, ', ') WITHIN GROUP (ORDER BY SACC.COLUMN_NAME) COLUMN_NAME\n" +
                "  FROM ALL_CONS_COLUMNS SACC\n" +
                "  JOIN ALL_CONSTRAINTS SAC\n" +
                "  ON SAC.CONSTRAINT_NAME = SACC.CONSTRAINT_NAME\n" +
                "  AND SAC.OWNER = SACC.OWNER\n" +
                "  AND SAC.TABLE_NAME = SACC.TABLE_NAME\n" +
                "  WHERE SACC.OWNER = '%s'\n" +
                "    AND SAC.CONSTRAINT_TYPE = 'P'\n" +
                "  GROUP BY SACC.TABLE_NAME\n" +
                ") PK ON PK.TABLE_NAME = SATC.TABLE_NAME AND PK.COLUMN_NAME = SATC.COLUMN_NAME\n" +
                "WHERE SAM.MVIEW_NAME IS NULL\n" +
                "AND SATC.OWNER = '%s'\n" +
                "GROUP BY SATC.TABLE_NAME\n" +
                "ORDER BY SATC.TABLE_NAME";

        return String.format(sql, options.getSchema(), options.getSchema());
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
