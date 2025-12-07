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
import java.util.List;
import java.util.stream.Collectors;

public class MainFrame extends JFrame {

    private static final long serialVersionUID = 1L;
    private final GerenciadorCorposCelestes gerenciador;
    private final JTextField searchField;
    private final JCheckBox chkPlaneta;
    private final JCheckBox chkEstrela;
    private final JTable table;
    private final DefaultTableModel tableModel;

    public MainFrame(GerenciadorCorposCelestes gerenciador) {
        super("AstroData - Gerenciador");
        this.gerenciador = gerenciador;

        ImageIcon icon = new ImageIcon("imagens/icone.png");
        if (icon.getImageLoadStatus() != MediaTracker.ERRORED) setIconImage(icon.getImage());

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchField = new JTextField(20);
        chkPlaneta = new JCheckBox("Planeta");
        chkEstrela = new JCheckBox("Estrela");
        
        JButton btnCadastrar = new JButton("Cadastrar");
        JButton btnRemover = new JButton("Remover");

        topPanel.add(new JLabel("Buscar:"));
        topPanel.add(searchField);
        topPanel.add(chkPlaneta);
        topPanel.add(chkEstrela);
        topPanel.add(btnCadastrar);
        topPanel.add(btnRemover);
        add(topPanel, BorderLayout.NORTH);

        String[] colunas = {"Tipo", "Nome", "Massa (Kg)", "Distância (AL)", "Atributo Especial"};
        tableModel = new DefaultTableModel(colunas, 0) {
            private static final long serialVersionUID = 1L;
            @Override public boolean isCellEditable(int row, int col) { return false; }
            
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                // Corrige aviso: Switch Expression (Java 14+)
                return switch (columnIndex) {
                    case 2 -> Double.class;
                    case 3 -> Long.class;
                    default -> String.class;
                };
            }
        };

        table = new JTable(tableModel);
        table.setAutoCreateRowSorter(true);
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i : new int[]{0, 2, 3, 4}) table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel();
        JButton btnEstatisticas = new JButton("Estatísticas");
        JButton btnRankings = new JButton("Rankings");
        JButton btnExportar = new JButton("Exportar CSV");
        
        bottomPanel.add(btnEstatisticas);
        bottomPanel.add(btnRankings);
        bottomPanel.add(btnExportar);
        add(bottomPanel, BorderLayout.SOUTH);

        // Listeners
        DocumentListener dl = new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { atualizarListaFiltrada(); }
            public void removeUpdate(DocumentEvent e) { atualizarListaFiltrada(); }
            public void changedUpdate(DocumentEvent e) { atualizarListaFiltrada(); }
        };
        searchField.getDocument().addDocumentListener(dl);
        chkPlaneta.addItemListener(e -> atualizarListaFiltrada());
        chkEstrela.addItemListener(e -> atualizarListaFiltrada());

        btnCadastrar.addActionListener(e -> { new CadastroDialog(this, gerenciador).setVisible(true); listarTodosCorpos(); });
        btnRemover.addActionListener(e -> { new RemoverDialog(this, gerenciador).setVisible(true); listarTodosCorpos(); });
        btnEstatisticas.addActionListener(e -> new EstatisticasDialog(this, gerenciador).setVisible(true));
        btnRankings.addActionListener(e -> new RankingDialog(this, gerenciador).setVisible(true));
        
        btnExportar.addActionListener(e -> {
            try {
                gerenciador.exportarParaCsv("corpos_celestes.csv");
                JOptionPane.showMessageDialog(this, "CSV Exportado!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage());
            }
        });

        listarTodosCorpos();
        setLocationRelativeTo(null);
    }

    // Corrige aviso: "Overridable method call". Método agora é private.
    private void listarTodosCorpos() {
        atualizarTabela(gerenciador.listarTodos());
    }

    private void atualizarTabela(List<CorpoCeleste> dados) {
        tableModel.setRowCount(0);
        for (CorpoCeleste c : dados) {
            if (c == null) continue; // Proteção contra nulos

            String tipo = "OUTRO";
            String extra = "";

            // Corrige aviso: Pattern Matching (instanceof moderno)
            if (c instanceof Planeta p) {
                tipo = "PLANETA";
                extra = p.isHabitavel() ? "Sim" : "Não";
            } else if (c instanceof Estrela e) {
                tipo = "ESTRELA";
                extra = e.getTipoEspectral();
            }
            tableModel.addRow(new Object[]{tipo, c.getNome(), c.getMassaKg(), c.getDistanciaEmAnosLuz(), extra});
        }
    }

    private void atualizarListaFiltrada() {
        String termo = searchField.getText().toLowerCase();
        boolean filtrarPlaneta = chkPlaneta.isSelected();
        boolean filtrarEstrela = chkEstrela.isSelected();

        List<CorpoCeleste> filtrados = gerenciador.listarTodos().stream()
            .filter(c -> termo.isEmpty() || (c.getNome() != null && c.getNome().toLowerCase().contains(termo)))
            .filter(c -> {
                // Corrige aviso: Lógica redundante simplificada
                if (!filtrarPlaneta && !filtrarEstrela) return true;
                return (filtrarPlaneta && c instanceof Planeta) || (filtrarEstrela && c instanceof Estrela);
            })
            .collect(Collectors.toList());
        atualizarTabela(filtrados);
    }
}