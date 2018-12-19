package br.dbbackup.dto;

import br.dbbackup.constant.Database;
import br.dbbackup.constant.Operation;
import br.dbbackup.sgbd.Mysql;
import br.dbbackup.sgbd.Oracle;
import br.dbbackup.sgbd.Postgresql;
import br.dbbackup.sgbd.SgbdImpl;
import lombok.Data;
import org.apache.commons.cli.CommandLine;

import java.util.Arrays;
import java.util.List;

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

    /*
    @TODO: implementar limit de linhas
    @TODO: implementar CHARSET
    */

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

        if(this.schemaNewName == null){
            this.schemaNewName = this.schema;
        }

        if(this.dumpFormat == null){
            this.sgbdToInstance = getInstance(this.database);
        }else{
            this.sgbdToInstance = getInstance(this.dumpFormat);
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
