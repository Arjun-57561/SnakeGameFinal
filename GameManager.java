import java.util.Random;

public class GameManager implements GameInterface {
    private final int WIDTH = 600, HEIGHT = 600, TILE_SIZE = 25;
    private Snake snake;
    private int foodX, foodY, score = 0, highScore = 0;
    private boolean running = false;
    private Random random = new Random();

    public GameManager() {
        snake = new Snake((WIDTH * HEIGHT) / (TILE_SIZE * TILE_SIZE));
        startGame();
    }

    @Override
    public void startGame() {
        snake = new Snake((WIDTH * HEIGHT) / (TILE_SIZE * TILE_SIZE));
        spawnFood();
        running = true;
        score = 0;
    }

    public void spawnFood() {
        foodX = random.nextInt(WIDTH / TILE_SIZE) * TILE_SIZE;
        foodY = random.nextInt(HEIGHT / TILE_SIZE) * TILE_SIZE;
    }

    @Override
    public void move() {
        snake.move();
        if (snake.getX()[0] < 0) snake.getX()[0] = WIDTH - TILE_SIZE;
        if (snake.getX()[0] >= WIDTH) snake.getX()[0] = 0;
        if (snake.getY()[0] < 0) snake.getY()[0] = HEIGHT - TILE_SIZE;
        if (snake.getY()[0] >= HEIGHT) snake.getY()[0] = 0;
    }

    @Override
    public void checkCollision() {
        for (int i = 1; i < snake.getLength(); i++) {
            if (snake.getX()[0] == snake.getX()[i] && snake.getY()[0] == snake.getY()[i]) {
                gameOver();
            }
        }
    }

    @Override
    public void checkFood() {
        if (snake.getX()[0] == foodX && snake.getY()[0] == foodY) {
            snake.grow();
            score += 10;
            highScore = Math.max(score, highScore);
            spawnFood();
        }
    }

    @Override
    public void gameOver() { running = false; }

    public boolean isRunning() { return running; }
    public int getFoodX() { return foodX; }
    public int getFoodY() { return foodY; }
    public int getScore() { return score; }
    public int getHighScore() { return highScore; }
    public Snake getSnake() { return snake; }
}

