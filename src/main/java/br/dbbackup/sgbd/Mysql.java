package br.dbbackup.sgbd;

import br.dbbackup.core.Database;
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

public class Mysql extends Database implements Sgbd {
    private static final Logger logger = LoggerFactory.getLogger(Mysql.class);
    private static final String EXCEPTION_LABEL = "ERRO NO Mysql";

    private Connection conn = null;
    private String owner = null;
    private Boolean lob = false;
    private String owner_exp = null;

    private final String SQL_TAB_COLUMNS = "select table_name, column_name, data_type " +
            "from information_schema.columns where table_schema = '%s'";

    public Mysql(Connection conn, String owner, Boolean lob, String owner_exp){
        this.conn = conn;
        this.owner = owner;
        this.lob = lob;

        if(owner_exp != null){
            this.owner_exp = owner_exp;
        }else{
            this.owner_exp = owner;
        }
    }

    public void startDump() throws SQLException {
        ResultSet rs;
        Statement stmt = conn.createStatement();

        logger.info(Msg.MSG_CONECTADO);
        logger.info(Msg.MSG_TBL_EXPORTACAO);

        rs = stmt.executeQuery(String.format(SQL_TAB_COLUMNS, owner));

        while(rs.next()) {
            setTabColumn(
                    rs.getString("table_name"),
                    rs.getString("column_name"),
                    rs.getString("data_type")
            );
        }

        startDumpProcess(stmt, owner, owner_exp);
    }

    public void startPump() throws Exception {
        startPumpProcess(conn);
    }

    public String formatColumn(ResultSet rs, String table, String column){
        String toReturn = "null";

        try {
            if(rs.getObject(column) != null){
                SimpleDateFormat sdf;

                switch (getColumnType(table, column)){
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
                        if(rs.getBytes(column).length == 0 || !lob){
                            toReturn = "null";
                        }else{
                            toReturn = Database.lobWriter(rs.getBytes(column));
                            toReturn = ":lob_" + toReturn;
                        }
                        break;
                    case "varchar":
                    case "text":
                        toReturn = "from_base64('%s')";
                        toReturn = String.format(toReturn, Base64.getEncoder().encodeToString(rs.getString(column).getBytes("UTF-8")));
                        break;
                }
            }
        }catch (SQLException|UnsupportedEncodingException e){
            logger.warn(EXCEPTION_LABEL, e);
            System.exit(0);
        }

        return toReturn;
    }
}
