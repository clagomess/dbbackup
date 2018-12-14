package br.dbbackup.dto;

import br.dbbackup.constant.Database;
import br.dbbackup.constant.Operation;
import br.dbbackup.sgbd.*;
import lombok.Data;
import org.apache.commons.cli.CommandLine;

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

    // dump format
    private Database dumpFormat;
    private SgbdImpl sgbdInstance;

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

        if(this.schemaNewName == null){
            this.schemaNewName = this.schema;
        }

        if(this.dumpFormat == null){
            sgbdInstance = getInstance(database);
        }else{
            sgbdInstance = getInstance(dumpFormat);
        }
    }

    private SgbdImpl getInstance(Database database){
        switch (database) {
            case ORACLE:
                return new Oracle();
            case MYSQL:
                return new Mysql();
            case POSTGRESQL:
                return new Postgresql();
            default:
                return null;
        }
    }
}
