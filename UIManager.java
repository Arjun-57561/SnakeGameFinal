import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class UIManager extends JPanel implements ActionListener, KeyListener {
    private GameManager gameManager;
    private Timer timer;
    private JFrame parentFrame;
    private boolean gameStarted = false;
    private boolean showTopScores = false;
    private String errorMessage = null;

    public UIManager(JFrame parentFrame) {
        this.parentFrame = parentFrame;
        this.setPreferredSize(new Dimension(600, 600));
        this.setBackground(new Color(50, 50, 50));
        this.setFocusable(true);
        this.addKeyListener(this);
        
        try {
            gameManager = new GameManager();
            timer = new Timer(100, this);
            
            // Show welcome screen and get player name
            showWelcomeScreen();
        } catch (GameInitializationException e) {
            errorMessage = "Failed to initialize game: " + e.getMessage();
            JOptionPane.showMessageDialog(parentFrame, 
                errorMessage, 
                "Game Initialization Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void showWelcomeScreen() {
        String playerName = JOptionPane.showInputDialog(
            parentFrame,
            "Enter your name:",
            "Snake Game",
            JOptionPane.QUESTION_MESSAGE
        );
        
        // Handle null or empty input
        if (playerName == null || playerName.trim().isEmpty()) {
            playerName = "Player"; // Default name
        }
        
        try {
            gameManager.setPlayerName(playerName);
            gameStarted = true;
            timer.start();
        } catch (PlayerRegistrationException e) {
            errorMessage = "Failed to register player: " + e.getMessage();
            JOptionPane.showMessageDialog(parentFrame, 
                errorMessage, 
                "Player Registration Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        // If there was an error initializing the game
        if (errorMessage != null) {
            g.setColor(Color.RED);
            g.setFont(new Font("Arial", Font.BOLD, 20));
            g.drawString("Error: " + errorMessage, 50, 300);
            return;
        }
        
        // If top scores are shown
        if (showTopScores) {
            drawTopScores(g);
            return;
        }
        
        if (gameManager != null && gameManager.isRunning()) {
            // Draw food
            g.setColor(Color.RED);
            g.fillOval(gameManager.getFoodX(), gameManager.getFoodY(), 25, 25);

            // Draw snake
            int[] x = gameManager.getSnake().getX();
            int[] y = gameManager.getSnake().getY();
            int length = gameManager.getSnake().getLength();

            for (int i = 0; i < length; i++) {
                if (i == 0) {
                    // Snake head
                    g.setColor(new Color(50, 200, 50));
                } else {
                    // Snake body
                    g.setColor(new Color(34, 139, 34));
                }
                g.fillRect(x[i], y[i], 25, 25);
            }

            // Draw score information
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 20));
            g.drawString("Player: " + gameManager.getPlayerName(), 10, 25);
            g.drawString("Score: " + gameManager.getScore(), 10, 50);
            g.drawString("High Score: " + gameManager.getHighScore(), 10, 75);
            
            // Draw global high score
            DataManager.HighScoreEntry globalHighScore = gameManager.getGlobalHighScore();
            g.drawString("Global Best: " + globalHighScore.getPlayerName() + " - " + globalHighScore.getScore(), 10, 100);
            
            // Instructions
            g.setFont(new Font("Arial", Font.PLAIN, 12));
            g.drawString("Press 'Tab' to view top scores", 400, 20);
        } else if (gameManager != null) {
            drawGameOver(g);
        }
    }

    private void drawGameOver(Graphics g) {
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 40));
        g.drawString("Game Over", 200, 250);
        
        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.drawString("Your Score: " + gameManager.getScore(), 225, 290);
        
        if (gameManager.getScore() >= gameManager.getHighScore()) {
            g.setColor(Color.YELLOW);
            g.drawString("New High Score!", 215, 330);
            g.setColor(Color.WHITE);
        }
        
        g.drawString("Press SPACE to Restart", 180, 370);
        g.drawString("Press TAB to View Top Scores", 160, 400);
    }
    
    private void drawTopScores(Graphics g) {
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 30));
        g.drawString("Top 5 Scores", 200, 80);
        
        DataManager.HighScoreEntry[] topScores = gameManager.getTopScores(5);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        
        int yPos = 150;
        for (int i = 0; i < topScores.length; i++) {
            if (topScores[i] == null) continue;
            
            String playerName = topScores[i].getPlayerName();
            int score = topScores[i].getScore();
            
            g.drawString((i+1) + ". " + playerName, 180, yPos);
            g.drawString(String.valueOf(score), 400, yPos);
            yPos += 50;
        }
        
        g.setFont(new Font("Arial", Font.BOLD, 16));
        g.drawString("Press TAB to return to game", 200, 450);
        g.drawString("Press SPACE to restart game", 200, 480);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (gameManager != null && !showTopScores && gameManager.isRunning()) {
            gameManager.move();
            gameManager.checkFood();
            gameManager.checkCollision();
        }
        repaint();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        // Toggle top scores view
        if (e.getKeyCode() == KeyEvent.VK_TAB) {
            showTopScores = !showTopScores;
            repaint();
            return;
        }
        
        // Handle restart game
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            showTopScores = false;
            try {
                gameManager.startGame();
            } catch (GameInitializationException ex) {
                errorMessage = "Failed to start game: " + ex.getMessage();
                JOptionPane.showMessageDialog(parentFrame, 
                    errorMessage, 
                    "Game Start Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
            repaint();
            return;
        }
        
        // Handle snake movement only if game is running and not showing top scores
        if (gameManager != null && !showTopScores && gameManager.isRunning()) {
            char dir = gameManager.getSnake().getDirection();
            try {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_LEFT: if (dir != 'R') gameManager.getSnake().setDirection('L'); break;
                    case KeyEvent.VK_RIGHT: if (dir != 'L') gameManager.getSnake().setDirection('R'); break;
                    case KeyEvent.VK_UP: if (dir != 'D') gameManager.getSnake().setDirection('U'); break;
                    case KeyEvent.VK_DOWN: if (dir != 'U') gameManager.getSnake().setDirection('D'); break;
                }
            } catch (InvalidMovementException ex) {
                // Just log the error, don't interrupt gameplay
                System.err.println("Invalid movement: " + ex.getMessage());
            }
        }
    }
    
    public void cleanup() {
        if (timer != null) {
            timer.stop();
        }
        if (gameManager != null) {
            gameManager.cleanup();
        }
    }

    @Override public void keyReleased(KeyEvent e) {}
    @Override public void keyTyped(KeyEvent e) {}
}
