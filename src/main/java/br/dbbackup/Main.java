package br.dbbackup;

import org.apache.commons.cli.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) throws SQLException {
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

        if(cmd.getOptionValue("ope").equals("get")) {
            switch (cmd.getOptionValue("db")) {
                case "oracle":
                    Oracle oracle = new Oracle(
                            conexao,
                            cmd.getOptionValue("schema"),
                            (cmd.getOptionValue("lob") != null),
                            cmd.getOptionValue("schema_exp")
                    );
                    oracle.startDump();
                    break;
                case "mysql":
                    Mysql mysql = new Mysql(
                            conexao,
                            cmd.getOptionValue("schema"),
                            (cmd.getOptionValue("lob") != null),
                            cmd.getOptionValue("schema_exp")
                    );
                    mysql.startDump();
                    break;
                default:
                    System.out.println(String.format("\"%s\" não implementado!", cmd.getOptionValue("db")));
            }
        }else{
            System.out.println(String.format("\"%s\" não implementado!", cmd.getOptionValue("ope")));
        }

        conexao.close();
    }
}
