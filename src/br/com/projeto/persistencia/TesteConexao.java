package br.com.projeto.persistencia;

import java.sql.Connection;
import java.sql.SQLException;

public class TesteConexao {
    public static void main(String[] args) {
        try (Connection conn = ConexaoBanco.getConnection()) {
            // Corrige aviso: Usa a variável 'conn' para algo útil
            String dbName = conn.getCatalog();
            System.out.println("SUCESSO! Conectado ao banco: " + dbName);
        } catch (SQLException e) {
            System.err.println("ERRO: " + e.getMessage());
        }
    }
}