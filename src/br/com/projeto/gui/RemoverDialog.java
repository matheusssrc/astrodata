package br.com.projeto.gui;

import br.com.projeto.modelo.GerenciadorCorposCelestes;
import br.com.projeto.modelo.CorpoCeleste;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

public class RemoverDialog extends JDialog {

    private static final long serialVersionUID = 1L;

    private final GerenciadorCorposCelestes gerenciador;
    private final List<CorpoCeleste> todosCorpos;

    // Componentes da Interface (Finais)
    private final JTextField campoFiltro;
    private final JList<CorpoCeleste> listaCorpos;
    private final DefaultListModel<CorpoCeleste> listModel;
    private final JButton btnRemover;
    private final JButton btnCancelar;

    public RemoverDialog(JFrame parent, GerenciadorCorposCelestes gerenciador) {
        super(parent, "Remover corpos celestes", true);
        this.gerenciador = gerenciador;

        // Snapshot da lista atual para garantir estabilidade no filtro
        this.todosCorpos = new ArrayList<>(gerenciador.listarTodos());

        // Layout com espaçamento (hgap, vgap)
        setLayout(new BorderLayout(10, 10));
        ((JPanel) getContentPane()).setBorder(new EmptyBorder(10, 10, 10, 10));

        // --- Painel Superior (Filtro) ---
        JPanel painelFiltro = new JPanel(new BorderLayout(5, 0));
        painelFiltro.add(new JLabel("Filtrar por nome:"), BorderLayout.WEST);
        
        campoFiltro = new JTextField();
        painelFiltro.add(campoFiltro, BorderLayout.CENTER);

        add(painelFiltro, BorderLayout.NORTH);

        // --- Lista Central ---
        listModel = new DefaultListModel<>();
        todosCorpos.forEach(listModel::addElement);

        listaCorpos = new JList<>(listModel);
        listaCorpos.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        listaCorpos.setVisibleRowCount(8);

        JScrollPane scroll = new JScrollPane(listaCorpos);
        scroll.setPreferredSize(new Dimension(400, 200)); // Tamanho confortável

        add(scroll, BorderLayout.CENTER);

        // --- Painel Inferior (Botões) ---
        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        btnCancelar = new JButton("Cancelar");
        btnRemover = new JButton("Remover Selecionados");
        
        // Padronizar altura dos botões
        Dimension btnSize = new Dimension(btnRemover.getPreferredSize().width, 30);
        btnRemover.setPreferredSize(btnSize);
        btnCancelar.setPreferredSize(new Dimension(100, 30));

        painelBotoes.add(btnCancelar);
        painelBotoes.add(btnRemover);

        add(painelBotoes, BorderLayout.SOUTH);

        // --- Listeners ---

        // Ação dos botões (Lambda limpa)
        btnRemover.addActionListener(e -> confirmarRemocao());
        btnCancelar.addActionListener(e -> dispose());

        // Filtro em tempo real
        campoFiltro.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { aplicarFiltro(); }
            @Override public void removeUpdate(DocumentEvent e) { aplicarFiltro(); }
            @Override public void changedUpdate(DocumentEvent e) { aplicarFiltro(); }
        });

        // Atalho ESC para limpar seleção
        getRootPane().registerKeyboardAction(
                e -> listaCorpos.clearSelection(),
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_IN_FOCUSED_WINDOW
        );

        // Ajusta a janela ao conteúdo
        pack();
        setResizable(false);
        setLocationRelativeTo(parent);
    }

    private void aplicarFiltro() {
        String termo = campoFiltro.getText();
        String termoLower = (termo == null) ? "" : termo.toLowerCase();

        listModel.clear();

        for (CorpoCeleste c : todosCorpos) {
            String nome = c.getNome() == null ? "" : c.getNome().toLowerCase();
            if (termoLower.isEmpty() || nome.contains(termoLower)) {
                listModel.addElement(c);
            }
        }
    }

    private void confirmarRemocao() {
        List<CorpoCeleste> selecionados = listaCorpos.getSelectedValuesList();

        if (selecionados == null || selecionados.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Selecione ao menos um corpo para remover.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Tem certeza que deseja remover " + selecionados.size() + " corpo(s) celeste(s)?",
                "Confirmar remoção",
                JOptionPane.OK_CANCEL_OPTION);

        if (confirm != JOptionPane.OK_OPTION) {
            return;
        }

        // Remove um a um
        for (CorpoCeleste corpo : selecionados) {
            if (corpo.getNome() != null) {
                gerenciador.removerPorNome(corpo.getNome());
            }
        }

        // Feedback ao usuário
        mostrarMensagemSucesso(selecionados);
        dispose();
    }

    private void mostrarMensagemSucesso(List<CorpoCeleste> removidos) {
        if (removidos.size() == 1) {
            JOptionPane.showMessageDialog(this,
                    removidos.get(0).getNome() + " foi removido do sistema.",
                    "Remoção concluída",
                    JOptionPane.INFORMATION_MESSAGE);
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append(removidos.size()).append(" corpos foram removidos:\n");

            for (int i = 0; i < removidos.size(); i++) {
                String nome = removidos.get(i).getNome();
                sb.append("- ").append(nome == null ? "(sem nome)" : nome).append("\n");
                // Limita a lista visual se for muito grande para não estourar a tela
                if (i >= 9) {
                    sb.append("... e mais ").append(removidos.size() - 10).append(" itens.");
                    break;
                }
            }
            JOptionPane.showMessageDialog(this, sb.toString(), "Remoção concluída", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}