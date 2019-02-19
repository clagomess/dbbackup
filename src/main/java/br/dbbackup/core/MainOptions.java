package br.dbbackup.core;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

public class MainOptions {
    public static Options get(){
        Options options = new Options();
        Option option;

        option = new Option("ope", true, "{GET, PUT, INFO}");
        option.setRequired(true);
        options.addOption(option);

        option = new Option("db", true, "{ORACLE, MYSQL, MARIADB, POSTGRESQL, H2}");
        option.setRequired(true);
        options.addOption(option);

        option = new Option("url", true, "jdbc:oracle:thin:@localhost:1521/XE - " +
                "jdbc:mysql://localhost/database - " +
                "jdbc:mariadb://localhost/database - " +
                "jdbc:postgresql://localhost:5432/postgres - " +
                "jdbc:h2:./home/h2/dbbackup");
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

        option = new Option("schema_exp", true, "Nome do schema para exportação");
        options.addOption(option);

        option = new Option("dump_format", true, "Formato de saída do dump. Ideal para migração. Ex.: {ORACLE, MYSQL, POSTGRESQL, H2}");
        option.setRequired(false);
        options.addOption(option);

        option = new Option("workdir", true, "Pasta de localização do dump. default: ./dump");
        options.addOption(option);

        option = new Option("table", true, "Tabela(s) a ser exportada(s). Ex.: -table foo -table bar ...");
        options.addOption(option);

        option = new Option("table_query", true, "Query de consulta da(s) tabela(s) a ser exportada(s). Ex.: " +
                "-table_query \"tbl_foo;select * from tbl_foo\" " +
                "-table_query \"tbl_bar;select * from tbl_bar\" ...");
        options.addOption(option);

        option = new Option("charset", true, "Default: UTF-8. Ex.: UTF-8, ISO-8859-1 e US-ASCII");
        options.addOption(option);

        return options;
    }
}
