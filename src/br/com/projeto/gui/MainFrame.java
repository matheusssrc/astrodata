package br.com.projeto.gui;

import br.com.projeto.modelo.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import java.awt.*;
import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

public class MainFrame extends JFrame {
    private static final long serialVersionUID = 1L;
    private final GerenciadorCorposCelestes gerenciador;
    private final JTextField searchField;
    private final JCheckBox chkPlaneta, chkEstrela;
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

        // Painel Superior
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchField = new JTextField(20);
        chkPlaneta = new JCheckBox("Planeta");
        chkEstrela = new JCheckBox("Estrela");
        JButton btnCadastrar = new JButton("Cadastrar"), btnRemover = new JButton("Remover");

        topPanel.add(new JLabel("Buscar:"));
        topPanel.add(searchField);
        topPanel.add(chkPlaneta);
        topPanel.add(chkEstrela);
        topPanel.add(btnCadastrar);
        topPanel.add(btnRemover);
        add(topPanel, BorderLayout.NORTH);

        // Tabela
        String[] colunas = {"Tipo", "Nome", "Massa (Kg)", "Distância (AL)", "Atributo Especial"};
        tableModel = new DefaultTableModel(colunas, 0) {
            private static final long serialVersionUID = 1L;
            @Override public boolean isCellEditable(int row, int col) { return false; }
            @Override public Class<?> getColumnClass(int col) {
                return switch (col) { case 2 -> Double.class; case 3 -> Long.class; default -> String.class; };
            }
        };

        table = new JTable(tableModel);
        table.setFillsViewportHeight(true);
        table.setAutoCreateRowSorter(true);
        
        // Centralizar Títulos e Células
        ((DefaultTableCellRenderer)table.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(SwingConstants.CENTER);
        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i : new int[]{0, 2, 3, 4}) table.getColumnModel().getColumn(i).setCellRenderer(center);
        
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Painel Inferior
        JPanel bottomPanel = new JPanel(new BorderLayout());
        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnStats = new JButton("Estatísticas"), btnRank = new JButton("Rankings"), btnExp = new JButton("Exportar CSV");
        
        left.add(btnStats); left.add(btnRank); right.add(btnExp);
        bottomPanel.add(left, BorderLayout.WEST); bottomPanel.add(right, BorderLayout.EAST);
        add(bottomPanel, BorderLayout.SOUTH);

        // Listeners
        DocumentListener dl = new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { atualizarLista(); }
            public void removeUpdate(DocumentEvent e) { atualizarLista(); }
            public void changedUpdate(DocumentEvent e) { atualizarLista(); }
        };
        searchField.getDocument().addDocumentListener(dl);
        chkPlaneta.addItemListener(e -> atualizarLista());
        chkEstrela.addItemListener(e -> atualizarLista());

        btnCadastrar.addActionListener(e -> { new CadastroDialog(this, gerenciador).setVisible(true); listarTodos(); });
        btnRemover.addActionListener(e -> { new RemoverDialog(this, gerenciador).setVisible(true); listarTodos(); });
        btnStats.addActionListener(e -> new EstatisticasDialog(this, gerenciador).setVisible(true));
        btnRank.addActionListener(e -> new RankingDialog(this, gerenciador).setVisible(true));
        btnExp.addActionListener(e -> exportarCsv());

        listarTodos();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void listarTodos() { atualizarTabela(gerenciador.listarTodos()); }

    private void atualizarTabela(List<CorpoCeleste> dados) {
        tableModel.setRowCount(0);
        for (CorpoCeleste c : dados) {
            if (c == null) continue;
            String tipo = "OUTRO", extra = "";
            if (c instanceof Planeta p) { tipo = "PLANETA"; extra = p.isHabitavel() ? "Sim" : "Não"; }
            else if (c instanceof Estrela e) { tipo = "ESTRELA"; extra = e.getTipoEspectral(); }
            tableModel.addRow(new Object[]{tipo, c.getNome(), c.getMassaKg(), c.getDistanciaEmAnosLuz(), extra});
        }
    }

    private void atualizarLista() {
        String termo = searchField.getText().toLowerCase();
        boolean fPlaneta = chkPlaneta.isSelected(), fEstrela = chkEstrela.isSelected();
        List<CorpoCeleste> filtrados = gerenciador.listarTodos().stream()
            .filter(c -> termo.isEmpty() || (c.getNome() != null && c.getNome().toLowerCase().contains(termo)))
            .filter(c -> (!fPlaneta && !fEstrela) || (fPlaneta && c instanceof Planeta) || (fEstrela && c instanceof Estrela))
            .collect(Collectors.toList());
        atualizarTabela(filtrados);
    }

    private void exportarCsv() {
        JFileChooser ch = new JFileChooser();
        ch.setSelectedFile(new File("corpos_celestes.csv"));
        if (ch.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                gerenciador.exportarParaCsv(ch.getSelectedFile().getAbsolutePath());
                JOptionPane.showMessageDialog(this, "Sucesso!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage());
            }
        }
    }
}