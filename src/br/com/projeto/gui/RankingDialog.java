package br.com.projeto.gui;

import br.com.projeto.modelo.GerenciadorCorposCelestes;
import br.com.projeto.modelo.CorpoCeleste;
import br.com.projeto.modelo.Planeta;
import br.com.projeto.modelo.Estrela;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class RankingDialog extends JDialog {

    private static final long serialVersionUID = 1L;

    private final GerenciadorCorposCelestes gerenciador;

    // Componentes da Interface (Imutáveis)
    private final JComboBox<String> comboRanking;
    private final JTable tabela;
    private final DefaultTableModel tableModel;
    private final JButton btnFechar;

    public RankingDialog(JFrame parent, GerenciadorCorposCelestes gerenciador) {
        super(parent, "Rankings da Coleção", true);
        this.gerenciador = gerenciador;

        setLayout(new BorderLayout());

        // --- 1. Painel Superior (Seleção) ---
        JPanel painelTopo = new JPanel(new FlowLayout(FlowLayout.LEFT));
        painelTopo.setBorder(new EmptyBorder(5, 5, 5, 5));

        painelTopo.add(new JLabel("Escolha o ranking:"));

        comboRanking = new JComboBox<>(new String[]{
            "Ranking Peso (Top 3)",
            "Ranking Distância (Top 3)",
            "Planeta Mais Pesado",
            "Estrela Mais Pesada"
        });
        painelTopo.add(comboRanking);

        add(painelTopo, BorderLayout.NORTH);

        // --- 2. Tabela Central ---
        tableModel = new DefaultTableModel(new Object[]{
            "Posição", "Tipo", "Nome", "Massa (Kg)", "Distância (AL)"
        }, 0) {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                // Switch moderno (Java 14+) - Elimina avisos e fica mais limpo
                return switch (columnIndex) {
                    case 0 -> Integer.class;
                    case 3 -> Double.class;
                    case 4 -> Long.class;
                    default -> String.class;
                };
            }
        };

        tabela = new JTable(tableModel);
        tabela.setFillsViewportHeight(true);
        tabela.setShowGrid(false); // Visual clean
        tabela.setIntercellSpacing(new Dimension(0, 0));
        tabela.setRowHeight(25); // Altura da linha mais confortável

        // Centraliza as colunas de dados (0=Pos, 1=Tipo, 3=Massa, 4=Distância)
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        
        for (int i : new int[]{0, 1, 3, 4}) {
            tabela.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        JScrollPane scroll = new JScrollPane(tabela);
        add(scroll, BorderLayout.CENTER);

        // --- 3. Painel Inferior (Botões) ---
        JPanel painelSul = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        painelSul.setBorder(new EmptyBorder(5, 5, 5, 5));

        btnFechar = new JButton("Fechar");
        btnFechar.setPreferredSize(new Dimension(100, 30)); // Tamanho padronizado
        painelSul.add(btnFechar);

        add(painelSul, BorderLayout.SOUTH);

        // --- Listeners ---
        comboRanking.addActionListener(e -> atualizarRanking());
        btnFechar.addActionListener(e -> dispose());

        // Carga inicial
        atualizarRanking();

        // Tamanho ajustado para ser mais compacto
        setSize(600, 350);
        setLocationRelativeTo(parent);
        setResizable(false);
    }

    private void atualizarRanking() {
        String opcao = (String) comboRanking.getSelectedItem();
        tableModel.setRowCount(0);

        if (opcao == null) return;

        if (opcao.startsWith("Ranking Peso")) {
            preencherRanking(gerenciador.getRankingPeso(3));
        } else if (opcao.startsWith("Ranking Distância")) {
            preencherRanking(gerenciador.getRankingDistancia(3));
        } else if (opcao.startsWith("Planeta Mais Pesado")) {
            Planeta p = gerenciador.getPlanetaMaisPesado();
            if (p != null) adicionarLinha(1, p);
        } else if (opcao.startsWith("Estrela Mais Pesada")) {
            Estrela e = gerenciador.getEstrelaMaisPesada();
            if (e != null) adicionarLinha(1, e);
        }
    }

    // Método auxiliar genérico para preencher listas
    private void preencherRanking(List<CorpoCeleste> lista) {
        int posicao = 1;
        for (CorpoCeleste c : lista) {
            adicionarLinha(posicao++, c);
        }
    }

    private void adicionarLinha(int posicao, CorpoCeleste corpo) {
        if (corpo == null) return;

        String tipo = "DESCONHECIDO";
        
        // Pattern Matching for instanceof (Java 14+) - Código mais limpo
        if (corpo instanceof Planeta) {
            tipo = "PLANETA";
        } else if (corpo instanceof Estrela) {
            tipo = "ESTRELA";
        }

        tableModel.addRow(new Object[]{
            posicao,
            tipo,
            corpo.getNome(),
            corpo.getMassaKg(),
            corpo.getDistanciaEmAnosLuz()
        });
    }
}