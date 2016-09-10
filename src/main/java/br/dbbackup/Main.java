package br.dbbackup;

import br.dbbackup.sgbd.Mysql;
import br.dbbackup.sgbd.Oracle;
import br.dbbackup.sgbd.Sgbd;
import org.apache.commons.cli.*;

import java.sql.Connection;
import java.sql.DriverManager;

public class Main {
    public static void main(String[] args) throws Exception {
        Options options = new Options();
        Option option;

        option = new Option("ope", true, "{get, put}");
        option.setRequired(true);
        options.addOption(option);

        option = new Option("db", true, "{oracle, mysql}");
        option.setRequired(true);
        options.addOption(option);

        option = new Option("url", true, "jdbc:oracle:thin:@localhost:1521/XE - jdbc:mysql://localhost/database");
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

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd = null;

        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("dbbackup", options);

            System.exit(1);
        }

        // ### INICIANDO ###
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        Connection conexao = DriverManager.getConnection(
                cmd.getOptionValue("url"),
                cmd.getOptionValue("user"),
                cmd.getOptionValue("pass")
        );

        // ### Instanciando SGBD ###
        Sgbd sgbd;

        switch (cmd.getOptionValue("db")) {
            case "oracle":
                sgbd = new Oracle(
                        conexao,
                        cmd.getOptionValue("schema"),
                        (cmd.getOptionValue("lob") != null),
                        cmd.getOptionValue("schema_exp")
                );
                break;
            case "mysql":
                sgbd = new Mysql(
                        conexao,
                        cmd.getOptionValue("schema"),
                        (cmd.getOptionValue("lob") != null),
                        cmd.getOptionValue("schema_exp")
                );
                break;
            default:
                throw new Exception(String.format("\"%s\" não implementado!", cmd.getOptionValue("db")));
        }

        if (cmd.getOptionValue("ope").equals("get")) {
            sgbd.startDump();
        } else if (cmd.getOptionValue("ope").equals("put")) {
            sgbd.startPump();
        } else {
            System.out.println(String.format("\"%s\" não implementado!", cmd.getOptionValue("ope")));
        }

        conexao.close();
    }
}
