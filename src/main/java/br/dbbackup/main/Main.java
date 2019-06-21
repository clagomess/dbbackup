package br.dbbackup.main;

import br.dbbackup.core.DbbackupException;
import br.dbbackup.core.MainOptions;
import br.dbbackup.dto.OptionsDto;
import br.dbbackup.sgbd.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.cli.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Slf4j
public class Main {
    private Main(){}

    public static void main(String[] args) throws Throwable {
        // OPTIONS
        Options opt = MainOptions.get();
        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd = null;

        try {
            cmd = parser.parse(opt, args);
        } catch (ParseException e) {
            log.warn(Main.class.getName(), e);
            formatter.printHelp("dbbackup", opt);
            System.exit(1);
        }

        // ### INICIANDO ###
        Connection conexao = null;

        try {
            final OptionsDto options = new OptionsDto(cmd);

            conexao = DriverManager.getConnection(options.getUrl(), options.getUser(), options.getPass());

            // ### Instanciando SGBD ###
            Sgbd sgbd;

            switch (options.getDatabase()) {
                case ORACLE:
                    sgbd = new Sgbd<>(new Oracle(), conexao, options);
                    break;
                case MYSQL:
                case MARIADB:
                    sgbd = new Sgbd<>(new Mysql(), conexao, options);
                    break;
                case POSTGRESQL:
                    sgbd = new Sgbd<>(new Postgresql(), conexao, options);
                    break;
                case H2:
                    sgbd = new Sgbd<>(new H2(), conexao, options);
                    break;
                case SQLITE:
                    sgbd = new Sgbd<>(new Sqlite(), conexao, options);
                    break;
                default:
                    throw new DbbackupException(String.format("\"%s\" não implementado!", options.getDatabase()));
            }

            switch (options.getOperation()){
                case GET:
                    sgbd.startDump();
                    break;
                case PUT:
                    sgbd.startPump();
                    break;
                case INFO:
                    sgbd.buildInfo();
                    break;
                default:
                    throw new DbbackupException(String.format("\"%s\" não implementado!", options.getOperation()));
            }
        } catch (Throwable e) {
            log.warn(Sgbd.class.getName(), e);
            throw e;
        } finally {
            if(conexao != null) {
                try {
                    conexao.close();
                } catch (SQLException e) {
                    log.warn(Sgbd.class.getName(), e);
                }
            }
        }
    }
}
