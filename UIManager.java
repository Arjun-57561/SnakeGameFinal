import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class UIManager extends JPanel implements ActionListener, KeyListener {
    private GameManager gameManager;
    private Timer timer;

    public UIManager() {
        this.setPreferredSize(new Dimension(600, 600));
        this.setBackground(Color.BLACK);
        this.setFocusable(true);
        this.addKeyListener(this);
        gameManager = new GameManager();
        timer = new Timer(100, this);
        timer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (gameManager.isRunning()) {
            g.setColor(Color.WHITE);
            g.fillOval(gameManager.getFoodX(), gameManager.getFoodY(), 25, 25);

            int[] x = gameManager.getSnake().getX();
            int[] y = gameManager.getSnake().getY();
            int length = gameManager.getSnake().getLength();

            for (int i = 0; i < length; i++) {
                g.setColor(i == 0 ? Color.RED : new Color(139, 0, 0));
                g.fillRect(x[i], y[i], 25, 25);
            }

            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 20));
            g.drawString("Score: " + gameManager.getScore(), 10, 25);
            g.drawString("High Score: " + gameManager.getHighScore(), 10, 50);
        } else {
            drawGameOver(g);
        }
    }

    private void drawGameOver(Graphics g) {
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 40));
        g.drawString("Game Over", 200, 250);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.drawString("Press SPACE to Restart", 180, 300);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (gameManager.isRunning()) {
            gameManager.move();
            gameManager.checkFood();
            gameManager.checkCollision();
        }
        repaint();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        char dir = gameManager.getSnake().getDirection();
        switch (e.getKeyCode()) {
            case KeyEvent.VK_LEFT: if (dir != 'R') gameManager.getSnake().setDirection('L'); break;
            case KeyEvent.VK_RIGHT: if (dir != 'L') gameManager.getSnake().setDirection('R'); break;
            case KeyEvent.VK_UP: if (dir != 'D') gameManager.getSnake().setDirection('U'); break;
            case KeyEvent.VK_DOWN: if (dir != 'U') gameManager.getSnake().setDirection('D'); break;
            case KeyEvent.VK_SPACE: gameManager.startGame(); break;
        }
    }

    @Override public void keyReleased(KeyEvent e) {}
    @Override public void keyTyped(KeyEvent e) {}
}
