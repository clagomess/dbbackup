package br.dbbackup;

import java.sql.*;

public class Main {
    public static void main(String[] argv) throws SQLException {
        Connection conexao = DriverManager.getConnection(
                "jdbc:oracle:thin:@//bomba.fnde.gov.br:1521/rubi.fnde.gov.br",
                "homolog_consulta",
                "homolog"
        );

        Oracle oracle = new Oracle(conexao, "SIGARP_FNDE");
        oracle.startDump();

        conexao.close();
    }
}
