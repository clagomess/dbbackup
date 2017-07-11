package br.dbbackup.sgbd;

import br.dbbackup.core.Database;
import br.dbbackup.core.DbbackupException;
import br.dbbackup.core.Msg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Base64;

public class Postgresql extends Database implements Sgbd {
    private Logger logger = LoggerFactory.getLogger(Postgresql.class);
    private Connection conn = null;
    private String owner = null;
    private Boolean lob = false;
    private String owner_exp = null;

    private final String SQL_TAB_COLUMNS = "select c.table_name, c.column_name, c.udt_name\n" +
            "from information_schema.columns c\n" +
            "JOIN information_schema.tables t\n" +
            "  on t.table_catalog = c.table_catalog\n" +
            "  AND t.table_schema = c.table_schema\n" +
            "  AND t.table_name = c.table_name\n" +
            "where c.table_schema = '%s' AND t.table_type <> 'VIEW'";

    public Postgresql(Connection conn, String owner, Boolean lob, String owner_exp){
        this.conn = conn;
        this.owner = owner;
        this.lob = lob;

        if(owner_exp != null){
            this.owner_exp = owner_exp;
        }else{
            this.owner_exp = owner;
        }
    }

    public void startDump() throws DbbackupException {
        ResultSet rs = null;

        try(Statement stmt = conn.createStatement()) {
            logger.info(Msg.MSG_CONECTADO);
            logger.info(Msg.MSG_TBL_EXPORTACAO);

            rs = stmt.executeQuery(String.format(SQL_TAB_COLUMNS, owner));

            while (rs.next()) {
                setTabColumn(
                        rs.getString("table_name"),
                        rs.getString("column_name"),
                        rs.getString("udt_name")
                );
            }

            this.startDumpProcess(stmt, owner, owner_exp);
        } catch (SQLException e) {
            logger.warn(Postgresql.class.getName(), e);
            throw new DbbackupException(e.getMessage());
        } finally {
            if(rs != null){
                try {
                    rs.close();
                } catch (SQLException e) {
                    logger.warn(Postgresql.class.getName(), e);
                }
            }
        }
    }

    public void startPump() throws DbbackupException {
        startPumpProcess(conn);
    }

    public String formatColumn(ResultSet rs, String table, String column) throws DbbackupException {
        String toReturn = "null";

        try {
            if(rs.getObject(column) != null){
                SimpleDateFormat sdf;

                switch (getColumnType(table, column)){
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
                        if(rs.getBytes(column).length == 0 || !lob){
                            toReturn = "null";
                        }else{
                            toReturn = Database.lobWriter(rs.getBytes(column));
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
            logger.warn(Postgresql.class.getName(), e);
            throw new DbbackupException(e.getMessage());
        }

        return toReturn;
    }
}
