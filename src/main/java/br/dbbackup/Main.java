package br.dbbackup;

import br.dbbackup.core.DbbackupException;
import br.dbbackup.sgbd.Mysql;
import br.dbbackup.sgbd.Oracle;
import br.dbbackup.sgbd.Sgbd;
import org.apache.commons.cli.*;
import org.apache.log4j.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Main {
    private static Logger logger = LoggerFactory.getLogger(Oracle.class);

    private Main(){}

    public static void main(String[] args) throws DbbackupException {
        // LOGGER
        PatternLayout patternLayout = new PatternLayout("%d{yyyy-MM-dd HH:mm:ss} %-5p %c{1}:%L - %m%n");
        ConsoleAppender consoleAppender = new ConsoleAppender(patternLayout);
        BasicConfigurator.configure(consoleAppender);
        LogManager.getRootLogger().setLevel(Level.INFO);

        // OPTIONS
        Options options = options();
        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd = null;

        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            logger.warn(Main.class.getName(), e);
            formatter.printHelp("dbbackup", options);
            System.exit(1);
        }

        // ### INICIANDO ###
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            logger.warn(Main.class.getName(), e);
            System.exit(1);
        }

        Connection conexao = null;

        try {
            conexao = DriverManager.getConnection(
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
                    throw new DbbackupException(String.format("\"%s\" não implementado!", cmd.getOptionValue("db")));
            }

            if ("get".equals(cmd.getOptionValue("ope"))) {
                sgbd.startDump();
            } else if ("put".equals(cmd.getOptionValue("ope"))) {
                sgbd.startPump();
            } else {
                throw new DbbackupException(String.format("\"%s\" não implementado!", cmd.getOptionValue("ope")));
            }
        } catch (SQLException e) {
            logger.warn(Sgbd.class.getName(), e);
            throw new DbbackupException(e.getMessage());
        } finally {
            if(conexao != null) {
                try {
                    conexao.close();
                } catch (SQLException e) {
                    logger.warn(Sgbd.class.getName(), e);
                }
            }
        }
    }

    private static Options options (){
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

        return options;
    }
}
