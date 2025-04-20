public class GameManager {
    private Snake snake;
    private int foodX, foodY;
    private int score;
    private int highScore;
    private boolean running;
    private DataManager dataManager; // Reference to DataManager for database operations

    public GameManager() {
        snake = new Snake(100); // Initialize the snake with a max size of 100
        score = 0;
        running = false;
        dataManager = new DataManager(); // Initialize DataManager

        // Fetch high score from the database (example player name "Player1")
        highScore = dataManager.getHighScore(getPlayerId("Player1"));
    }

    // Start the game
    public void startGame() {
        snake.reset();
        score = 0;
        running = true;
        spawnFood();
    }

    // Move the snake and check game conditions
    public void move() {
        if (running) {
            snake.move(); // Move the snake
            if (checkFood()) {
                score += 10;
                spawnFood();
                snake.grow(); // Grow the snake when it eats the food
            }
            checkCollision(); // Check for collision with the wall or itself
        }
    }

    // Check if the snake has eaten the food
    public boolean checkFood() {
        if (snake.getHeadX() == foodX && snake.getHeadY() == foodY) {
            return true;
        }
        return false;
    }

    // Check for collisions with the snake's body or the wall
    public void checkCollision() {
        // Check if the snake collides with itself
        for (int i = 1; i < snake.getLength(); i++) {
            if (snake.getX()[0] == snake.getX()[i] && snake.getY()[0] == snake.getY()[i]) {
                running = false;
                saveScore(); // Save the score to the database if collision occurs
                break;
            }
        }

        // Check if the snake collides with the walls
        if (snake.getX()[0] < 0 || snake.getX()[0] >= 600 || snake.getY()[0] < 0 || snake.getY()[0] >= 600) {
            running = false;
            saveScore(); // Save the score to the database if collision occurs
        }
    }

    // Save the current score to the database
    public void saveScore() {
        int playerId = getPlayerId("Player1");
        dataManager.saveScore(playerId, score);

        // Update high score if the current score is higher
        if (score > highScore) {
            highScore = score;
        }
    }

    // Get player ID from the database
    private int getPlayerId(String playerName) {
        return dataManager.getOrCreatePlayerId(playerName);
    }

    // Spawn food at random location
    private void spawnFood() {
        foodX = (int) (Math.random() * 20) * 25;
        foodY = (int) (Math.random() * 20) * 25;
    }

    // Getters
    public int getScore() {
        return score;
    }

    public int getHighScore() {
        return highScore;
    }

    public boolean isRunning() {
        return running;
    }

    public int getFoodX() {
        return foodX;
    }

    public int getFoodY() {
        return foodY;
    }

    public Snake getSnake() {
        return snake;
    }
}
