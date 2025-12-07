package br.com.projeto.principal;

import br.com.projeto.gui.MainFrame;
import br.com.projeto.modelo.GerenciadorCorposCelestes;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class MainApp {

    public static void main(String[] args) {

        // 1. Configura o visual para parecer um programa nativo do Sistema Operacional
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
            System.err.println("Aviso: Não foi possível carregar o visual nativo (" + e.getMessage() + ")");
        }

        // 2. Inicializa o Backend (Carrega dados do Banco, TXT ou RAM)
        // Isso é feito ANTES de abrir a janela para garantir que os dados já existam
        GerenciadorCorposCelestes gerenciador = new GerenciadorCorposCelestes();

        // 3. Inicia a Interface Gráfica na Thread de Eventos do Swing (Padrão obrigatório)
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame(gerenciador);
            frame.setVisible(true);
        });
    }
}