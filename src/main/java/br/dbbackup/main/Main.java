package br.dbbackup.main;

import br.dbbackup.core.DbbackupException;
import br.dbbackup.core.MainOptions;
import br.dbbackup.dto.OptionsDto;
import br.dbbackup.sgbd.Mysql;
import br.dbbackup.sgbd.Oracle;
import br.dbbackup.sgbd.Postgresql;
import br.dbbackup.sgbd.Sgbd;
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
                    sgbd = new Sgbd<Oracle>(conexao, options);
                    break;
                case MYSQL:
                    sgbd = new Sgbd<Mysql>(conexao, options);
                    break;
                case POSTGRESQL:
                    sgbd = new Sgbd<Postgresql>(conexao, options);
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
                default:
                    throw new DbbackupException(String.format("\"%s\" não implementado!", options.getOperation()));
            }
        } catch (Throwable e) {
            log.warn(Sgbd.class.getName(), e);
            throw new DbbackupException(e.getMessage());
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
