package br.dbbackup.dto;

import br.dbbackup.constant.Database;
import br.dbbackup.constant.Operation;
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

    public OptionsDto(CommandLine cmd){
        this.operation = Operation.valueOf(cmd.getOptionValue("ope"));
        this.database = Database.valueOf(cmd.getOptionValue("db"));
        this.url = cmd.getOptionValue("url");
        this.user = cmd.getOptionValue("user");
        this.pass = cmd.getOptionValue("pass");
        this.schema = cmd.getOptionValue("schema");
        this.exportLob = cmd.getOptionValue("lob") != null;
        this.schemaNewName = cmd.getOptionValue("schema_exp");
    }
}
