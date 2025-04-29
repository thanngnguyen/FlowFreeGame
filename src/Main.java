import flowfree.FlowFreeGame;

import javax.swing.*;


public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                new FlowFreeGame();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Error starting game: " + e.getMessage());
                System.exit(1);
            }
        });
    }
}