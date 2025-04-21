import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class Main {
    public static void main(String[] args) {
        try {
            // Use fully qualified name for Swing's UIManager to avoid conflict with your UIManager class
            javax.swing.UIManager.setLookAndFeel(javax.swing.UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.out.println("Could not set look and feel: " + e.getMessage());
        }
        
        JFrame frame = new JFrame("Snake Game");
        UIManager gameUI = new UIManager(frame);

        frame.add(gameUI);
        frame.pack();
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        
        // Add window listener to properly close database connection
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                gameUI.cleanup();
            }
        });
        
        frame.setVisible(true);
    }
}
