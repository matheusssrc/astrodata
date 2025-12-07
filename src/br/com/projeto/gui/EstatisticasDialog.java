package br.com.projeto.gui;

import br.com.projeto.modelo.GerenciadorCorposCelestes;
import br.com.projeto.modelo.CorpoCeleste;
import br.com.projeto.modelo.Planeta;
import br.com.projeto.modelo.Estrela;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class EstatisticasDialog extends JDialog {

    private static final long serialVersionUID = 1L;

    // Formatador para notação científica (ex: 1.989E30)
    private static final DecimalFormat FORMAT_SCI = new DecimalFormat("0.###E0");

    private final List<CorpoCeleste> todos;

    // Componentes de Interface
    private final JList<CorpoCeleste> listaCorpos;
    private final JLabel lblTotalCorpos;
    private final JLabel lblTotalEstrelas;
    private final JLabel lblTotalPlanetas;
    private final JLabel lblSomaMassa;
    private final JLabel lblMediaMassa;
    private final JLabel lblQtdHabitaveis;
    private final DefaultListModel<String> modeloHabitaveis;

    public EstatisticasDialog(JFrame parent, GerenciadorCorposCelestes gerenciador) {
        super(parent, "Estatísticas da Coleção", true);
        
        // Cria um snapshot (cópia) dos dados atuais para análise
        this.todos = new ArrayList<>(gerenciador.listarTodos());

        setLayout(new BorderLayout());

        // --- 1. PAINEL ESQUERDO: Lista de Seleção ---
        JPanel painelEsquerdo = new JPanel(new BorderLayout());
        painelEsquerdo.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        painelEsquerdo.add(new JLabel("Filtrar seleção (CTRL+Click):"), BorderLayout.NORTH);

        DefaultListModel<CorpoCeleste> modelLista = new DefaultListModel<>();
        todos.forEach(modelLista::addElement);

        listaCorpos = new JList<>(modelLista);
        listaCorpos.setCellRenderer(new CorpoCelesteListRenderer());
        // Permite selecionar vários itens para recalcular a média apenas deles
        listaCorpos.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        
        JScrollPane scrollLista = new JScrollPane(listaCorpos);
        scrollLista.setPreferredSize(new Dimension(250, 0)); // Largura fixa para a lista
        painelEsquerdo.add(scrollLista, BorderLayout.CENTER);

        add(painelEsquerdo, BorderLayout.WEST);

        // --- 2. PAINEL CENTRAL: Resumo + Habitáveis ---
        JPanel painelCentral = new JPanel(new GridLayout(1, 2, 10, 0)); // Divide em 2 colunas
        painelCentral.setBorder(new EmptyBorder(10, 0, 10, 10));

        // Coluna 1: Grid de Valores (Resumo)
        JPanel painelResumo = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Inicializa labels de valores
        lblTotalCorpos = new JLabel("-");
        lblTotalEstrelas = new JLabel("-");
        lblTotalPlanetas = new JLabel("-");
        lblSomaMassa = new JLabel("-");
        lblMediaMassa = new JLabel("-");
        lblQtdHabitaveis = new JLabel("-");

        // Adiciona linhas ao GridBag (Rótulo à Dir, Valor à Esq)
        adicionarLinhaResumo(painelResumo, gbc, 0, "Total de Corpos:", lblTotalCorpos);
        adicionarLinhaResumo(painelResumo, gbc, 1, "Total de Estrelas:", lblTotalEstrelas);
        adicionarLinhaResumo(painelResumo, gbc, 2, "Total de Planetas:", lblTotalPlanetas);
        
        // Separador visual
        gbc.gridy = 3; 
        painelResumo.add(Box.createVerticalStrut(10), gbc);

        adicionarLinhaResumo(painelResumo, gbc, 4, "Soma Massa (Kg):", lblSomaMassa);
        adicionarLinhaResumo(painelResumo, gbc, 5, "Média Massa (Kg):", lblMediaMassa);
        adicionarLinhaResumo(painelResumo, gbc, 6, "Planetas Habitáveis:", lblQtdHabitaveis);

        // Coluna 2: Lista de Habitáveis
        JPanel painelHabitaveis = new JPanel(new BorderLayout());
        painelHabitaveis.add(new JLabel("Lista de Habitáveis:"), BorderLayout.NORTH);
        
        modeloHabitaveis = new DefaultListModel<>();
        JList<String> listaHabitaveis = new JList<>(modeloHabitaveis);
        listaHabitaveis.setEnabled(false); // Apenas visualização
        listaHabitaveis.setBackground(new Color(240, 240, 240)); // Cinza claro
        
        painelHabitaveis.add(new JScrollPane(listaHabitaveis), BorderLayout.CENTER);

        // Adiciona as duas colunas ao centro
        painelCentral.add(painelResumo);
        painelCentral.add(painelHabitaveis);

        add(painelCentral, BorderLayout.CENTER);

        // --- 3. PAINEL INFERIOR: Botão ---
        JPanel painelBotao = new JPanel();
        painelBotao.setBorder(new EmptyBorder(5, 0, 10, 0));
        
        JButton btnFechar = new JButton("Fechar");
        btnFechar.setPreferredSize(new Dimension(100, 30)); // Tamanho padronizado
        btnFechar.addActionListener(e -> dispose());
        
        painelBotao.add(btnFechar);
        add(painelBotao, BorderLayout.SOUTH);

        // --- LISTENERS ---
        // Recalcula tudo quando a seleção da lista muda
        listaCorpos.addListSelectionListener((ListSelectionEvent e) -> {
            if (!e.getValueIsAdjusting()) {
                atualizarEstatisticas();
            }
        });

        // Atalho ESC para limpar seleção
        getRootPane().registerKeyboardAction(
                e -> listaCorpos.clearSelection(),
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_IN_FOCUSED_WINDOW
        );

        // Cálculo inicial (com todos selecionados implicitamente)
        atualizarEstatisticas();

        // Tamanho ajustado (Menor e mais limpo)
        setSize(780, 420);
        setLocationRelativeTo(parent);
        setResizable(false);
    }

    // Método auxiliar para limpar a adição de linhas no GridBagLayout
    private void adicionarLinhaResumo(JPanel panel, GridBagConstraints gbc, int row, String label, JLabel valor) {
        gbc.gridy = row;
        
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.weightx = 0.0;
        JLabel lblTitulo = new JLabel(label);
        lblTitulo.setFont(lblTitulo.getFont().deriveFont(Font.BOLD)); // Negrito no rótulo
        panel.add(lblTitulo, gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.weightx = 1.0;
        panel.add(valor, gbc);
    }

    private void atualizarEstatisticas() {
        // Se nada selecionado, usa TODOS. Se algo selecionado, usa apenas a seleção.
        List<CorpoCeleste> base = listaCorpos.getSelectedValuesList();
        if (base.isEmpty()) {
            base = todos;
        }

        // Cálculos usando Java Streams (Programação Funcional)
        long totalEstrelas = base.stream().filter(c -> c instanceof Estrela).count();
        long totalPlanetas = base.stream().filter(c -> c instanceof Planeta).count();
        
        double somaMassa = base.stream().mapToDouble(CorpoCeleste::getMassaKg).sum();
        double mediaMassa = base.isEmpty() ? 0.0 : somaMassa / base.size();

        List<String> nomesHabitaveis = base.stream()
                .filter(c -> c instanceof Planeta)
                .map(c -> (Planeta) c)
                .filter(Planeta::isHabitavel)
                .map(CorpoCeleste::getNome)
                .collect(Collectors.toList());

        // Atualização da Interface
        lblTotalCorpos.setText(String.valueOf(base.size()));
        lblTotalEstrelas.setText(String.valueOf(totalEstrelas));
        lblTotalPlanetas.setText(String.valueOf(totalPlanetas));
        
        lblSomaMassa.setText(FORMAT_SCI.format(somaMassa));
        lblMediaMassa.setText(FORMAT_SCI.format(mediaMassa));
        lblQtdHabitaveis.setText(String.valueOf(nomesHabitaveis.size()));

        // Atualiza lista lateral
        modeloHabitaveis.clear();
        if (nomesHabitaveis.isEmpty()) {
            modeloHabitaveis.addElement("(Nenhum)");
        } else {
            nomesHabitaveis.forEach(modeloHabitaveis::addElement);
        }
    }

    // Renderizador customizado para mostrar "TIPO - Nome" na lista esquerda
    private static class CorpoCelesteListRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            
            if (value instanceof CorpoCeleste c) {
                String tipo = (c instanceof Estrela) ? "[ESTRELA]" : "[PLANETA]";
                setText(tipo + " " + c.getNome());
            }
            return this;
        }
    }
}