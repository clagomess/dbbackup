package br.dbbackup.dto;

import br.dbbackup.constant.Database;
import br.dbbackup.constant.Operation;
import br.dbbackup.sgbd.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.cli.CommandLine;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@NoArgsConstructor
@Data
public class OptionsDto {
    private Operation operation;
    private Database database;
    private String url;
    private String user;
    private String pass;
    private String schema;
    private Boolean exportLob;
    private String schemaNewName;
    private String workdir;
    private List<String> table;
    private Map<String, String> tableQuery; // table => query
    private Charset charset;

    // dump format
    private Database dumpFormat;
    private SgbdImpl sgbdFromInstance;
    private SgbdImpl sgbdToInstance;

    public OptionsDto(CommandLine cmd){
        this.operation = Operation.valueOf(cmd.getOptionValue("ope"));
        this.database = Database.valueOf(cmd.getOptionValue("db"));
        this.url = cmd.getOptionValue("url");
        this.user = cmd.getOptionValue("user");
        this.pass = cmd.getOptionValue("pass");
        this.schema = cmd.getOptionValue("schema");
        this.exportLob = cmd.getOptionValue("lob") != null;
        this.schemaNewName = cmd.getOptionValue("schema_exp");
        this.dumpFormat = cmd.getOptionValue("dump_format") != null ? Database.valueOf(cmd.getOptionValue("dump_format")) : null;
        this.workdir = cmd.getOptionValue("workdir") != null ? cmd.getOptionValue("workdir") : "dump";
        this.table = cmd.getOptionValues("table") != null ? Arrays.asList(cmd.getOptionValues("table")) : null;
        this.sgbdFromInstance = getInstance(this.database);
        this.charset = cmd.getOptionValue("charset") != null ? Charset.forName(cmd.getOptionValue("charset")) : StandardCharsets.UTF_8;

        if(this.schemaNewName == null){
            this.schemaNewName = this.schema;
        }

        if(this.dumpFormat == null){
            this.sgbdToInstance = getInstance(this.database);
        }else{
            this.sgbdToInstance = getInstance(this.dumpFormat);
        }

        if(cmd.getOptionValues("table_query") != null){
            if(this.tableQuery == null){
                this.tableQuery = new HashMap<>();
            }

            for (String arg : cmd.getOptionValues("table_query")){
                String[] list = arg.split(";");

                if(list.length == 2) {
                    this.tableQuery.put(list[0], list[1]);
                }
            }
        }
    }

    public String getTableQuery(String table){
        if(this.tableQuery == null){
            return null;
        }

        return this.tableQuery.get(table);
    }

    private SgbdImpl getInstance(Database database){
        switch (database) {
            case ORACLE:
                return new Oracle();
            case MYSQL:
            case MARIADB:
                return new Mysql();
            case POSTGRESQL:
                return new Postgresql();
            case H2:
                return new H2();
            case SQLITE:
                return new Sqlite();
            default:
                return null;
        }
    }
}
