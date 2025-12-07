package br.com.projeto.gui;

import br.com.projeto.modelo.GerenciadorCorposCelestes;
import br.com.projeto.modelo.CorpoCeleste;
import br.com.projeto.modelo.Planeta;
import br.com.projeto.modelo.Estrela;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class MainFrame extends JFrame {

    private static final long serialVersionUID = 1L;

    private final GerenciadorCorposCelestes gerenciador;

    // Componentes da Interface
    private final JTextField searchField;
    private final JCheckBox chkPlaneta;
    private final JCheckBox chkEstrela;
    private final JButton btnCadastrar;
    private final JButton btnRemover;
    private final JButton btnEstatisticas;
    private final JButton btnRankings;
    private final JButton btnExportarCsv;
    private final JTable table;
    private final DefaultTableModel tableModel;

    public MainFrame(GerenciadorCorposCelestes gerenciador) {
        super("AstroData - Gerenciador de Corpos Celestes");
        this.gerenciador = gerenciador;

        // Configuração do Ícone
        ImageIcon icon = new ImageIcon("imagens/icone.png");
        if (icon.getImageLoadStatus() != MediaTracker.ERRORED) {
            setIconImage(icon.getImage());
        }

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLayout(new BorderLayout());

        // --- Painel Superior (Filtros e Ações) ---
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JLabel lblBuscar = new JLabel("Buscar:");
        searchField = new JTextField(20);
        chkPlaneta = new JCheckBox("Planeta");
        chkEstrela = new JCheckBox("Estrela");
        btnCadastrar = new JButton("Cadastrar");
        btnRemover = new JButton("Remover");

        topPanel.add(lblBuscar);
        topPanel.add(searchField);
        topPanel.add(chkPlaneta);
        topPanel.add(chkEstrela);
        topPanel.add(btnCadastrar);
        topPanel.add(btnRemover);

        add(topPanel, BorderLayout.NORTH);

        // --- Tabela Central ---
        String[] colunas = {"Tipo", "Nome", "Massa (Kg)", "Distância (AL)", "Atributo Especial"};
        
        tableModel = new DefaultTableModel(colunas, 0) {
            private static final long serialVersionUID = 1L;
            
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                // CORREÇÃO 2: Switch Expression (Moderno)
                return switch (columnIndex) {
                    case 2 -> Double.class;
                    case 3 -> Long.class;
                    default -> String.class;
                };
            }
        };

        table = new JTable(tableModel);
        table.setFillsViewportHeight(true);
        table.setShowHorizontalLines(false);
        table.setShowVerticalLines(false);
        table.setRowSelectionAllowed(false);
        table.setAutoCreateRowSorter(true);

        // Centralizar conteúdo das colunas
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i : new int[]{0, 2, 3, 4}) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        add(new JScrollPane(table), BorderLayout.CENTER);

        // --- Painel Inferior (Botões Extras) ---
        JPanel bottomPanel = new JPanel(new BorderLayout());
        JPanel leftButtonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPanel rightButtonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        btnEstatisticas = new JButton("Estatísticas");
        btnRankings = new JButton("Rankings");
        btnExportarCsv = new JButton("Exportar CSV");

        leftButtonsPanel.add(btnEstatisticas);
        leftButtonsPanel.add(btnRankings);
        rightButtonsPanel.add(btnExportarCsv);

        bottomPanel.add(leftButtonsPanel, BorderLayout.WEST);
        bottomPanel.add(rightButtonsPanel, BorderLayout.EAST);

        add(bottomPanel, BorderLayout.SOUTH);

        // --- Configuração de Listeners ---

        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { atualizarListaFiltrada(); }
            @Override
            public void removeUpdate(DocumentEvent e) { atualizarListaFiltrada(); }
            @Override
            public void changedUpdate(DocumentEvent e) { atualizarListaFiltrada(); }
        });

        chkPlaneta.addItemListener(e -> atualizarListaFiltrada());
        chkEstrela.addItemListener(e -> atualizarListaFiltrada());

        btnCadastrar.addActionListener(e -> abrirCadastro());
        btnRemover.addActionListener(e -> abrirRemocao());
        btnEstatisticas.addActionListener(e -> mostrarEstatisticas());
        btnRankings.addActionListener(e -> abrirRankings());
        btnExportarCsv.addActionListener(e -> exportarCsv());

        // Carga inicial
        listarTodosCorpos();

        setLocationRelativeTo(null);
        setVisible(true);
    }

    // --- Métodos de Lógica ---

    private void atualizarTabela(List<CorpoCeleste> dados) {
        tableModel.setRowCount(0);

        for (CorpoCeleste corpo : dados) {
            // CORREÇÃO 4: Null Check
            if (corpo == null) continue;

            String tipo = "OUTRO";
            String atributoEspecial = "";

            // CORREÇÃO 1: Instanceof Pattern Matching (Java 14+)
            switch (corpo) {
                case Planeta p -> {
                    tipo = "PLANETA";
                    atributoEspecial = p.isHabitavel() ? "Sim" : "Não";
                }
                case Estrela e -> {
                    tipo = "ESTRELA";
                    atributoEspecial = e.getTipoEspectral();
                }
                default -> {
                }
            }

            tableModel.addRow(new Object[]{
                tipo,
                corpo.getNome(),
                corpo.getMassaKg(),
                corpo.getDistanciaEmAnosLuz(),
                atributoEspecial
            });
        }
    }

    // CORREÇÃO 5: Método final ou privado para ser chamado no construtor com segurança
    private void listarTodosCorpos() {
        List<CorpoCeleste> todos = gerenciador.listarTodos();
        atualizarTabela(todos);
    }

    private void atualizarListaFiltrada() {
        String termo = searchField.getText();
        String termoLower = (termo == null) ? "" : termo.trim().toLowerCase();

        boolean filtrarPlaneta = chkPlaneta.isSelected();
        boolean filtrarEstrela = chkEstrela.isSelected();

        List<CorpoCeleste> base = gerenciador.listarTodos();

        List<CorpoCeleste> filtrados = base.stream()
                .filter(c -> {
                    if (termoLower.isEmpty()) return true;
                    String nome = c.getNome();
                    return nome != null && nome.toLowerCase().contains(termoLower);
                })
                .filter(c -> {
                    // CORREÇÃO 3: Lógica booleana simplificada
                    if (!filtrarPlaneta && !filtrarEstrela) return true;
                    
                    boolean ehPlaneta = c instanceof Planeta;
                    boolean ehEstrela = c instanceof Estrela;
                    
                    return (filtrarPlaneta && ehPlaneta) || (filtrarEstrela && ehEstrela);
                })
                .collect(Collectors.toList());

        atualizarTabela(filtrados);
    }

    private void abrirCadastro() {
        CadastroDialog dialog = new CadastroDialog(this, gerenciador);
        dialog.setVisible(true);
        // Atualiza a lista após fechar o diálogo de cadastro
        listarTodosCorpos();
    }

    private void abrirRemocao() {
        RemoverDialog dialog = new RemoverDialog(this, gerenciador);
        dialog.setVisible(true);
        // Atualiza a lista após fechar o diálogo de remoção
        listarTodosCorpos();
    }

    private void mostrarEstatisticas() {
        new EstatisticasDialog(this, gerenciador).setVisible(true);
    }

    private void abrirRankings() {
        new RankingDialog(this, gerenciador).setVisible(true);
    }

    private void exportarCsv() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Salvar arquivo CSV");
        chooser.setSelectedFile(new File("corpos_celestes.csv"));

        int resultado = chooser.showSaveDialog(this);
        if (resultado == JFileChooser.APPROVE_OPTION) {
            File arquivo = chooser.getSelectedFile();
            try {
                gerenciador.exportarParaCsv(arquivo.getAbsolutePath());
                JOptionPane.showMessageDialog(this, "Exportação concluída com sucesso.", "Exportar CSV", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Erro ao exportar CSV: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}