package br.com.projeto.persistencia;

import br.com.projeto.modelo.CorpoCeleste;
import br.com.projeto.modelo.Planeta;
import br.com.projeto.modelo.Estrela;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CorpoCelesteDAO {

    // Método auxiliar para garantir que o ambiente esteja pronto
    private void criarTabelaSeNaoExistir(Connection conexao) throws SQLException {
        String sql = """
            CREATE TABLE IF NOT EXISTS corpos_celestes (
                id SERIAL PRIMARY KEY,
                tipo TEXT NOT NULL,
                nome TEXT NOT NULL,
                massa_kg DOUBLE PRECISION NOT NULL,
                distancia_anos_luz BIGINT NOT NULL,
                atributo_especial TEXT
            )
        """;
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.execute();
        }
    }

    public void inserir(CorpoCeleste corpo) throws SQLException {
        String sql = "INSERT INTO corpos_celestes (tipo, nome, massa_kg, distancia_anos_luz, atributo_especial) VALUES (?, ?, ?, ?, ?)";

        try (Connection conexao = ConexaoBanco.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            String tipo = "DESCONHECIDO";
            String atributoEspecial = "";

            // Pattern Matching moderno (Java 14+)
            switch (corpo) {
                case Planeta p -> {
                    tipo = "PLANETA";
                    atributoEspecial = String.valueOf(p.isHabitavel());
                }
                case Estrela e -> {
                    tipo = "ESTRELA";
                    atributoEspecial = e.getTipoEspectral();
                }
                default -> {
                }
            }

            stmt.setString(1, tipo);
            stmt.setString(2, corpo.getNome());
            stmt.setDouble(3, corpo.getMassaKg());
            stmt.setLong(4, corpo.getDistanciaEmAnosLuz());
            stmt.setString(5, atributoEspecial);

            stmt.executeUpdate();
        }
    }

    public void removerPorTipoENome(String tipo, String nome) throws SQLException {
        String sql = "DELETE FROM corpos_celestes WHERE tipo = ? AND nome = ?";

        try (Connection conexao = ConexaoBanco.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {
            
            stmt.setString(1, tipo);
            stmt.setString(2, nome);
            stmt.executeUpdate();
        }
    }

    public List<CorpoCeleste> listarTodos() throws SQLException {
        List<CorpoCeleste> lista = new ArrayList<>();
        String sql = "SELECT tipo, nome, massa_kg, distancia_anos_luz, atributo_especial FROM corpos_celestes";

        try (Connection conexao = ConexaoBanco.getConnection()) {
            
            // Verifica a tabela apenas na carga inicial para garantir robustez
            criarTabelaSeNaoExistir(conexao);

            try (PreparedStatement stmt = conexao.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    String tipo = rs.getString("tipo");
                    String nome = rs.getString("nome");
                    double massa = rs.getDouble("massa_kg");
                    long distancia = rs.getLong("distancia_anos_luz");
                    String atributoEspecial = rs.getString("atributo_especial");

                    CorpoCeleste corpo = null;

                    if ("PLANETA".equalsIgnoreCase(tipo)) {
                        boolean habitavel = Boolean.parseBoolean(atributoEspecial);
                        corpo = new Planeta(nome, massa, distancia, habitavel);
                    } else if ("ESTRELA".equalsIgnoreCase(tipo)) {
                        corpo = new Estrela(nome, massa, distancia, atributoEspecial);
                    }

                    if (corpo != null) {
                        lista.add(corpo);
                    }
                }
            }
        }
        return lista;
    }
}