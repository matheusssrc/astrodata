package br.com.projeto.persistencia;

import java.sql.Connection;
import java.sql.SQLException;

public class TesteConexao {

    public static void main(String[] args) {
        // Utiliza try-with-resources para garantir que a conexão seja fechada automaticamente
        try (Connection conn = ConexaoBanco.getConnection()) {

            // Acessa um dado da conexão (nome do banco) para validar se ela está ativa
            String nomeBanco = conn.getCatalog();
            System.out.println("SUCESSO: Conexão estabelecida com o banco '" + nomeBanco + "'!");

        } catch (SQLException e) {
            System.err.println("ERRO: Falha ao conectar. Verifique as credenciais ou se o serviço está rodando.");
            e.printStackTrace();
        }
    }
}