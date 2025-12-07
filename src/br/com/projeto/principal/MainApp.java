package br.com.projeto.principal;

import br.com.projeto.gui.MainFrame;
import br.com.projeto.modelo.GerenciadorCorposCelestes;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class MainApp {

    public static void main(String[] args) {

        // 1. Configura o visual nativo (Corrige o aviso de "Multicatch")
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
            System.err.println("Aviso: Não foi possível carregar o visual nativo (" + e.getMessage() + ")");
        }

        // 2. Inicializa o Backend
        GerenciadorCorposCelestes gerenciador = new GerenciadorCorposCelestes();

        // 3. Inicia a Interface
        SwingUtilities.invokeLater(() -> {
            // Corrige o aviso "New instance ignored" atribuindo a uma variável
            MainFrame frame = new MainFrame(gerenciador);
            frame.setVisible(true);
        });
    }
}