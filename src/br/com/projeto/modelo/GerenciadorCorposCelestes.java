package br.com.projeto.modelo;

import br.com.projeto.persistencia.CorpoCelesteDAO;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class GerenciadorCorposCelestes {

    private final List<CorpoCeleste> colecaoEmMemoria;
    private final CorpoCelesteDAO dao;

    public GerenciadorCorposCelestes() {
        this.colecaoEmMemoria = new ArrayList<>();
        this.dao = new CorpoCelesteDAO();
        carregarDadosIniciais();
    }

    private void carregarDadosIniciais() {
        // 1. Tenta Banco de Dados
        if (carregarDoBanco()) {
            return;
        }

        // 2. Tenta Arquivo TXT
        if (carregarDoArquivoTxt()) {
            return;
        }

        // 3. Fallback: Dados em Memória RAM
        inicializarDadosFake();
    }

    private boolean carregarDoBanco() {
        try {
            List<CorpoCeleste> doBanco = dao.listarTodos();
            if (!doBanco.isEmpty()) {
                colecaoEmMemoria.addAll(doBanco);
                return true;
            }
        } catch (SQLException e) {
            // Captura específica de SQL para satisfazer o NetBeans
            System.err.println("Aviso: Banco indisponível (" + e.getMessage() + ")");
        } catch (Exception e) {
            // Captura genérica para outros erros (ex: Driver não encontrado)
            System.err.println("Aviso: Erro de conexão (" + e.getMessage() + ")");
        }
        return false;
    }

    private boolean carregarDoArquivoTxt() {
        File arquivo = new File("dados/dados.txt");
        if (!arquivo.exists()) {
            arquivo = new File("dados.txt");
        }

        if (!arquivo.exists()) {
            return false;
        }

        // CORREÇÃO: Uso de 'var' para limpar o código (Sugestão NetBeans)
        try (var br = new BufferedReader(new FileReader(arquivo))) {
            String linha;
            boolean carregouAlgo = false;

            while ((linha = br.readLine()) != null) {
                if (linha.trim().isEmpty() || linha.startsWith("#")) {
                    continue;
                }

                try {
                    String[] partes = linha.split(",");
                    if (partes.length < 5) {
                        continue;
                    }

                    String tipo = partes[0].trim().toUpperCase();
                    String nome = partes[1].trim();
                    double massa = Double.parseDouble(partes[2].trim());
                    long distancia = Long.parseLong(partes[3].trim());
                    String extra = partes[4].trim();

                    if ("PLANETA".equals(tipo)) {
                        boolean habitavel = Boolean.parseBoolean(extra);
                        colecaoEmMemoria.add(new Planeta(nome, massa, distancia, habitavel));
                        carregouAlgo = true;
                    } else if ("ESTRELA".equals(tipo)) {
                        colecaoEmMemoria.add(new Estrela(nome, massa, distancia, extra));
                        carregouAlgo = true;
                    }
                } catch (NumberFormatException ignored) {
                    // Ignora linhas mal formatadas
                }
            }
            return carregouAlgo;
        } catch (IOException e) {
            System.err.println("Erro ao ler TXT: " + e.getMessage());
            return false;
        }
    }

    private void inicializarDadosFake() {
        System.out.println("Iniciando com dados de memória (RAM).");
        colecaoEmMemoria.add(new Estrela("Sol", 1.989e30, 0, "G2V"));
        colecaoEmMemoria.add(new Planeta("Terra", 5.972e24, 0, true));
        colecaoEmMemoria.add(new Planeta("Marte", 6.417e23, 0, false));
    }

    // --- Métodos CRUD ---
    
    public List<CorpoCeleste> listarTodos() {
        return new ArrayList<>(colecaoEmMemoria);
    }

    public void adicionar(CorpoCeleste corpo) {
        if (corpo == null) {
            return;
        }

        colecaoEmMemoria.add(corpo);
        try {
            dao.inserir(corpo);
        } catch (SQLException e) {
            System.err.println("Erro ao salvar no banco: " + e.getMessage());
        }
    }

    public void removerPorNome(String nome) {
        if (nome == null) {
            return;
        }

        String nomeTratado = nome.trim().toLowerCase();

        // CORREÇÃO: Null check adicionado dentro do filtro
        CorpoCeleste alvo = colecaoEmMemoria.stream()
                .filter(c -> c != null && c.getNome() != null)
                .filter(c -> c.getNome().toLowerCase().equals(nomeTratado))
                .findFirst()
                .orElse(null);

        if (alvo != null) {
            colecaoEmMemoria.remove(alvo);
            try {
                String tipo = (alvo instanceof Planeta) ? "PLANETA" : "ESTRELA";
                dao.removerPorTipoENome(tipo, alvo.getNome());
            } catch (SQLException e) {
                System.err.println("Erro ao remover do banco: " + e.getMessage());
            }
        }
    }

    // --- Estatísticas (Streams) ---
    
    public List<CorpoCeleste> getRankingPeso(int limite) {
        return colecaoEmMemoria.stream()
                .filter(c -> c != null) // Proteção contra nulos
                .sorted(Comparator.comparing(CorpoCeleste::getMassaKg).reversed())
                .limit(limite)
                .collect(Collectors.toList());
    }

    public List<CorpoCeleste> getRankingDistancia(int limite) {
        return colecaoEmMemoria.stream()
                .filter(c -> c != null)
                .sorted(Comparator.comparing(CorpoCeleste::getDistanciaEmAnosLuz).reversed())
                .limit(limite)
                .collect(Collectors.toList());
    }

    public Planeta getPlanetaMaisPesado() {
        return colecaoEmMemoria.stream()
                .filter(c -> c instanceof Planeta)
                .map(c -> (Planeta) c)
                .max(Comparator.comparing(CorpoCeleste::getMassaKg))
                .orElse(null);
    }

    public Estrela getEstrelaMaisPesada() {
        return colecaoEmMemoria.stream()
                .filter(c -> c instanceof Estrela)
                .map(c -> (Estrela) c)
                .max(Comparator.comparing(CorpoCeleste::getMassaKg))
                .orElse(null);
    }

    // --- Exportação ---
    
    public void exportarParaCsv(String caminhoArquivo) throws IOException {
        Path path = Paths.get(caminhoArquivo);

        // CORREÇÃO: Uso de 'var' no try-with-resources
        try (var writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
            writer.write("TIPO;NOME;MASSA;DISTANCIA;EXTRA");
            writer.newLine();

            for (CorpoCeleste c : colecaoEmMemoria) {
                // CORREÇÃO: "Dereferencing possible null pointer" resolvido
                if (c == null) {
                    continue;
                }

                String tipo = "DESCONHECIDO";
                String extra = "";

                // CORREÇÃO: Pattern Matching (Java 14+) elimina casts manuais
                switch (c) {
                    case Planeta p -> {
                        tipo = "PLANETA";
                        extra = String.valueOf(p.isHabitavel());
                    }
                    case Estrela e -> {
                        tipo = "ESTRELA";
                        extra = e.getTipoEspectral();
                    }
                    default -> {
                    }
                }

                // CORREÇÃO: Null check antes de getNome()
                String nomeSeguro = (c.getNome() != null) ? c.getNome() : "Sem Nome";

                String linha = String.format("%s;%s;%s;%d;%s",
                        tipo,
                        nomeSeguro,
                        c.getMassaKg(),
                        c.getDistanciaEmAnosLuz(),
                        extra);

                writer.write(linha);
                writer.newLine();
            }
        }
    }
}