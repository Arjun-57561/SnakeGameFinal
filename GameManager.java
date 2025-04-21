import java.util.Random;

public class GameManager implements GameInterface {
    private final int WIDTH = 600, HEIGHT = 600, TILE_SIZE = 25;
    private Snake snake;
    private int foodX, foodY, score = 0;
    private int highScore = 0;
    private String playerName;
    private int playerId = -1;
    private boolean running = false;
    private Random random = new Random();
    private DataManager dataManager;

    public GameManager() throws GameInitializationException {
        try {
            snake = new Snake((WIDTH * HEIGHT) / (TILE_SIZE * TILE_SIZE));
            dataManager = new DataManager();
        } catch (DatabaseConnectionException e) {
            throw new GameInitializationException("Failed to initialize game: " + e.getMessage(), e);
        }
    }
    
    public void setPlayerName(String playerName) throws PlayerRegistrationException {
        this.playerName = playerName;
        // Get player ID from database and retrieve their high score
        if (dataManager != null) {
            try {
                playerId = dataManager.getOrCreatePlayerId(playerName);
                if (playerId != -1) {
                    highScore = dataManager.getHighScore(playerId);
                }
            } catch (ScoreRecordingException e) {
                System.out.println("Could not retrieve high score: " + e.getMessage());
                // Not rethrowing as this is not critical
            }
        }
    }
    
    public String getPlayerName() {
        return playerName;
    }

    @Override
    public void startGame() throws GameInitializationException {
        if (playerName == null || playerName.trim().isEmpty()) {
            throw new GameInitializationException("Player name not set");
        }
        
        // Reset the snake
        snake = new Snake((WIDTH * HEIGHT) / (TILE_SIZE * TILE_SIZE));
        spawnFood();
        running = true;
        score = 0;
    }

    public void spawnFood() {
        foodX = random.nextInt(WIDTH / TILE_SIZE) * TILE_SIZE;
        foodY = random.nextInt(HEIGHT / TILE_SIZE) * TILE_SIZE;
        
        // Make sure food doesn't spawn on the snake
        for (int i = 0; i < snake.getLength(); i++) {
            if (foodX == snake.getX()[i] && foodY == snake.getY()[i]) {
                spawnFood(); // Recursively try again
                return;
            }
        }
    }

    @Override
    public void move() {
        try {
            snake.move();
            // Handle wrap-around behavior
            if (snake.getX()[0] < 0) snake.getX()[0] = WIDTH - TILE_SIZE;
            if (snake.getX()[0] >= WIDTH) snake.getX()[0] = 0;
            if (snake.getY()[0] < 0) snake.getY()[0] = HEIGHT - TILE_SIZE;
            if (snake.getY()[0] >= HEIGHT) snake.getY()[0] = 0;
        } catch (Exception e) {
            System.err.println("Error during snake movement: " + e.getMessage());
            // Continue game despite error
        }
    }

    @Override
    public void checkCollision() {
        // Check if snake head collides with its body
        for (int i = 1; i < snake.getLength(); i++) {
            if (snake.getX()[0] == snake.getX()[i] && snake.getY()[0] == snake.getY()[i]) {
                gameOver();
                return;
            }
        }
    }

    @Override
    public void checkFood() {
        if (snake.getX()[0] == foodX && snake.getY()[0] == foodY) {
            snake.grow();
            score += 10;
            
            // Update high score if current score is higher
            if (score > highScore) {
                highScore = score;
            }
            spawnFood();
        }
    }

    @Override
    public void gameOver() { 
        running = false; 
        
        // Save score to database if dataManager is initialized and playerId is valid
        if (dataManager != null && playerId != -1) {
            try {
                dataManager.saveScore(playerId, score);
                // Refresh high score after saving
                highScore = dataManager.getHighScore(playerId);
            } catch (ScoreRecordingException e) {
                System.err.println("Failed to save score: " + e.getMessage());
            }
        }
    }
    
    public DataManager.HighScoreEntry getGlobalHighScore() {
        if (dataManager != null) {
            try {
                return dataManager.getGlobalHighScore();
            } catch (ScoreRecordingException e) {
                System.err.println("Failed to get global high score: " + e.getMessage());
                return new DataManager.HighScoreEntry("Error", 0);
            }
        }
        return new DataManager.HighScoreEntry("N/A", 0);
    }
    
    public DataManager.HighScoreEntry[] getTopScores(int limit) {
        if (dataManager != null) {
            try {
                return dataManager.getTopScores(limit);
            } catch (ScoreRecordingException e) {
                System.err.println("Failed to get top scores: " + e.getMessage());
                DataManager.HighScoreEntry[] errorEntries = new DataManager.HighScoreEntry[limit];
                for (int i = 0; i < limit; i++) {
                    errorEntries[i] = new DataManager.HighScoreEntry("Error", 0);
                }
                return errorEntries;
            }
        }
        
        DataManager.HighScoreEntry[] emptyEntries = new DataManager.HighScoreEntry[limit];
        for (int i = 0; i < limit; i++) {
            emptyEntries[i] = new DataManager.HighScoreEntry("N/A", 0);
        }
        return emptyEntries;
    }
    
    public void cleanup() {
        if (dataManager != null) {
            try {
                dataManager.closeConnection();
            } catch (DatabaseConnectionException e) {
                System.err.println("Error closing database connection: " + e.getMessage());
            }
        }
    }

    public boolean isRunning() { return running; }
    public int getFoodX() { return foodX; }
    public int getFoodY() { return foodY; }
    public int getScore() { return score; }
    public int getHighScore() { return highScore; }
    public Snake getSnake() { return snake; }
}
