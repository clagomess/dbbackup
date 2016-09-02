package br.dbbackup;

import java.sql.*;

public class Main {
    public static void main(String[] argv) throws SQLException {
        if(argv.length != 6) {
            System.out.println("É necessário preencher os parâmetros!");
            System.out.println("\t# dbbackup operacao db url user pass database");
            System.out.println("\t\toperacao: {get, put}");
            System.out.println("\t\tdb: {oracle}");
            System.out.println("\t\turl: jdbc:oracle:thin:@0.0.0.0:1521/0.0.0.0");
            System.out.println("\t\tuser: user");
            System.out.println("\t\tpass: pass");
            System.out.println("\t\tdatabase: database");

            System.exit(0);
        }

        Connection conexao = DriverManager.getConnection(argv[2], argv[3], argv[4]);

        if (argv[0].equals("get")) {
            switch (argv[1]){
                case "oracle":
                    Oracle oracle = new Oracle(conexao, argv[5]);
                    oracle.startDump();
                    break;
                default:
                    System.out.println(String.format("\"%s\" não implementado!", argv[1]));
            }
        }else{
            System.out.println(String.format("\"%s\" não implementado!", argv[0]));
        }

        conexao.close();
    }
}
