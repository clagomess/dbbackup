package br.dbbackup.core;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

public class MainOptions {
    public static Options get(){
        Options options = new Options();
        Option option;

        option = new Option("ope", true, "{GET, PUT}");
        option.setRequired(true);
        options.addOption(option);

        option = new Option("db", true, "{ORACLE, MYSQL, POSTGRESQL}");
        option.setRequired(true);
        options.addOption(option);

        option = new Option("url", true, "jdbc:oracle:thin:@localhost:1521/XE - jdbc:mysql://localhost/database - jdbc:postgresql://localhost:5432/postgres");
        option.setRequired(true);
        options.addOption(option);

        option = new Option("user", true, "user");
        option.setRequired(true);
        options.addOption(option);

        option = new Option("pass", true, "pass");
        option.setRequired(true);
        options.addOption(option);

        option = new Option("schema", true, "schema");
        option.setRequired(true);
        options.addOption(option);

        option = new Option("lob", true, "{1} - Importar/Exportar LOB");
        options.addOption(option);

        option = new Option("schema_exp", true, "Nome do schema para exportacao");
        options.addOption(option);

        option = new Option("dump_format", true, "Formato de saida do dump. Ideal para migracao. Ex.: {ORACLE, MYSQL, POSTGRESQL}");
        option.setRequired(false);
        options.addOption(option);

        return options;
    }
}
