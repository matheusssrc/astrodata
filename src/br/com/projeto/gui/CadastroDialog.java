package br.com.projeto.gui;

import br.com.projeto.modelo.GerenciadorCorposCelestes;
import br.com.projeto.modelo.CorpoCeleste;
import br.com.projeto.modelo.Planeta;
import br.com.projeto.modelo.Estrela;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;

public class CadastroDialog extends JDialog {

    private static final long serialVersionUID = 1L;

    // Textos de apoio
    private static final String PLACEHOLDER_MASSA = "Somente números (use ponto para decimais)";
    private static final String PLACEHOLDER_DISTANCIA = "Somente números inteiros";

    // Dimensões Fixas para Layout Estável
    private static final Dimension TAMANHO_FIXO = new Dimension(380, 30);
    private static final Dimension DIMENSAO_LABEL = new Dimension(180, 30);

    private final GerenciadorCorposCelestes gerenciador;

    // Componentes da Interface
    private final JComboBox<String> comboTipo;
    private final JTextField campoNome;
    private final JTextField campoMassa;
    private final JTextField campoDistancia;
    private final JLabel labelExtra;
    private final JPanel panelExtraCard;
    private final CardLayout cardExtraLayout;
    private final JComboBox<String> comboHabitavel;
    private final JTextField campoTipoEspectral;
    private final JButton btnSalvar;

    public CadastroDialog(JFrame parent, GerenciadorCorposCelestes gerenciador) {
        super(parent, "Cadastrar Novo Corpo Celeste", true);
        this.gerenciador = gerenciador;

        setLayout(new BorderLayout());

        // --- Configuração do Painel de Formulário ---
        JPanel painelCampos = new JPanel(new GridBagLayout());
        painelCampos.setBorder(new EmptyBorder(10, 10, 10, 10));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0.0;
        gbc.weighty = 0.0;

        // 1. Tipo de Corpo
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.EAST;
        painelCampos.add(criarLabel("Tipo de Corpo:"), gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        comboTipo = new JComboBox<>(new String[]{"Planeta", "Estrela"});
        aplicarTamanhoFixo(comboTipo);
        painelCampos.add(comboTipo, gbc);

        // 2. Nome
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.anchor = GridBagConstraints.EAST;
        painelCampos.add(criarLabel("Nome:"), gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        campoNome = new JTextField();
        aplicarTamanhoFixo(campoNome);
        painelCampos.add(campoNome, gbc);

        // 3. Massa
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.anchor = GridBagConstraints.EAST;
        painelCampos.add(criarLabel("Massa (Kg):"), gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        campoMassa = new JTextField();
        aplicarTamanhoFixo(campoMassa);
        adicionarPlaceholder(campoMassa, PLACEHOLDER_MASSA);
        // Validação: Números e Ponto
        campoMassa.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (!Character.isDigit(c) && !Character.isISOControl(c) && c != '.') {
                    e.consume();
                }
            }
        });
        painelCampos.add(campoMassa, gbc);

        // 4. Distância
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.anchor = GridBagConstraints.EAST;
        painelCampos.add(criarLabel("Distância (Anos Luz):"), gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        campoDistancia = new JTextField();
        aplicarTamanhoFixo(campoDistancia);
        adicionarPlaceholder(campoDistancia, PLACEHOLDER_DISTANCIA);
        // Validação: Apenas Inteiros
        campoDistancia.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (!Character.isDigit(c) && !Character.isISOControl(c)) {
                    e.consume();
                }
            }
        });
        painelCampos.add(campoDistancia, gbc);

        // 5. Campo Extra (Dinâmico)
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.anchor = GridBagConstraints.EAST;
        labelExtra = criarLabel("Habitável:");
        painelCampos.add(labelExtra, gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;

        cardExtraLayout = new CardLayout();
        panelExtraCard = new JPanel(cardExtraLayout);
        aplicarTamanhoFixo(panelExtraCard); // Trava o container

        // Card Planeta
        comboHabitavel = new JComboBox<>(new String[]{"Sim", "Não"});
        JPanel pPlaneta = new JPanel(new BorderLayout());
        pPlaneta.add(comboHabitavel, BorderLayout.CENTER);

        // Card Estrela
        campoTipoEspectral = new JTextField();
        JPanel pEstrela = new JPanel(new BorderLayout());
        pEstrela.add(campoTipoEspectral, BorderLayout.CENTER);

        panelExtraCard.add(pPlaneta, "PLANETA");
        panelExtraCard.add(pEstrela, "ESTRELA");

        painelCampos.add(panelExtraCard, gbc);

        // Filler para empurrar conteúdo para o topo
        GridBagConstraints gbcFiller = new GridBagConstraints();
        gbcFiller.gridx = 0;
        gbcFiller.gridy = 100;
        gbcFiller.weighty = 1.0;
        painelCampos.add(Box.createVerticalGlue(), gbcFiller);

        // --- Montagem da Janela ---
        add(painelCampos, BorderLayout.NORTH);
        add(new JPanel(), BorderLayout.CENTER); // Preenche espaço vazio

        // Botões
        JPanel panelBotoes = new JPanel();
        btnSalvar = new JButton("Salvar");
        btnSalvar.setPreferredSize(new Dimension(100, 30));
        panelBotoes.add(btnSalvar);
        panelBotoes.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

        add(panelBotoes, BorderLayout.SOUTH);

        // --- Listeners Otimizados (Sem variável 'e' inútil) ---
        comboTipo.addActionListener(e -> atualizarCampoExtra());
        btnSalvar.addActionListener(e -> salvar());

        // Estado inicial
        atualizarCampoExtra();

        // Configurações finais da janela
        setSize(620, 320);
        setLocationRelativeTo(parent);
        setResizable(false);
    }

    // --- Métodos Auxiliares ---

    private JLabel criarLabel(String texto) {
        JLabel lbl = new JLabel(texto);
        lbl.setPreferredSize(DIMENSAO_LABEL);
        lbl.setMinimumSize(DIMENSAO_LABEL);
        lbl.setMaximumSize(DIMENSAO_LABEL);
        lbl.setHorizontalAlignment(SwingConstants.LEFT);
        return lbl;
    }

    private void aplicarTamanhoFixo(JComponent comp) {
        comp.setPreferredSize(TAMANHO_FIXO);
        comp.setMinimumSize(TAMANHO_FIXO);
        comp.setMaximumSize(TAMANHO_FIXO);
    }

    private void atualizarCampoExtra() {
        String tipo = (String) comboTipo.getSelectedItem();
        if ("Planeta".equalsIgnoreCase(tipo)) {
            labelExtra.setText("Habitável:");
            cardExtraLayout.show(panelExtraCard, "PLANETA");
        } else {
            labelExtra.setText("Tipo Espectral (ex: G2V):");
            cardExtraLayout.show(panelExtraCard, "ESTRELA");
        }
    }

    private void adicionarPlaceholder(JTextField campo, String placeholder) {
        campo.setForeground(Color.GRAY);
        campo.setText(placeholder);

        campo.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (campo.getText().equals(placeholder)) {
                    campo.setText("");
                    campo.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (campo.getText().trim().isEmpty()) {
                    campo.setForeground(Color.GRAY);
                    campo.setText(placeholder);
                }
            }
        });
    }

    private void salvar() {
        String tipoSelecionado = (String) comboTipo.getSelectedItem();
        String nome = campoNome.getText().trim();
        String massaTexto = campoMassa.getText().trim();
        String distanciaTexto = campoDistancia.getText().trim();

        if (nome.isEmpty() || massaTexto.isEmpty() || distanciaTexto.isEmpty()
                || PLACEHOLDER_MASSA.equals(massaTexto)
                || PLACEHOLDER_DISTANCIA.equals(distanciaTexto)) {
            JOptionPane.showMessageDialog(this, "Preencha todos os campos obrigatórios.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            double massa = Double.parseDouble(massaTexto);
            long distancia = Long.parseLong(distanciaTexto);

            CorpoCeleste corpo;
            if ("Planeta".equalsIgnoreCase(tipoSelecionado)) {
                String hab = (String) comboHabitavel.getSelectedItem();
                boolean habitavel = "Sim".equalsIgnoreCase(hab);
                corpo = new Planeta(nome, massa, distancia, habitavel);
            } else {
                String tipoEspectral = campoTipoEspectral.getText().trim();
                if (tipoEspectral.isEmpty()) tipoEspectral = "Desconhecido";
                corpo = new Estrela(nome, massa, distancia, tipoEspectral);
            }

            gerenciador.adicionar(corpo);
            JOptionPane.showMessageDialog(this, nome + " cadastrado com sucesso.", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            dispose();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Erro nos valores numéricos.\nVerifique se digitou apenas números e ponto (.) para a massa.", "Erro de Formato", JOptionPane.ERROR_MESSAGE);
        }
    }
}