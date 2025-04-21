import java.sql.*;

public class DataManager {
    // Database credentials and connection details
    private static final String DB_URL = "jdbc:mysql://localhost:3306/snake_game_db";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "#*#*123#*#*a";

    private Connection conn;

    // Constructor with custom exception handling
    public DataManager() throws DatabaseConnectionException {
        try {
            // Load the JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            // Establish connection to the MySQL database
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            System.out.println("Database connected successfully.");
            createTables(); // Ensure tables are created if they don't already exist
        } catch (ClassNotFoundException e) {
            throw new DatabaseConnectionException("MySQL JDBC Driver not found.", e);
        } catch (SQLException e) {
            throw new DatabaseConnectionException("Failed to connect to database: " + e.getMessage(), e);
        }
    }

    // Create tables with custom exception handling
    private void createTables() throws DatabaseConnectionException {
        try (Statement stmt = conn.createStatement()) {
            // Create Players table if it does not exist
            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS Players (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    name VARCHAR(100) UNIQUE NOT NULL
                )
            """);

            // Create Scores table if it does not exist
            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS Scores (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    player_id INT NOT NULL,
                    score INT NOT NULL,
                    date_created TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    FOREIGN KEY (player_id) REFERENCES Players(id)
                )
            """);
        } catch (SQLException e) {
            throw new DatabaseConnectionException("Failed to create tables: " + e.getMessage(), e);
        }
    }

    // Get or create player with custom exception
    public int getOrCreatePlayerId(String playerName) throws PlayerRegistrationException {
        if (playerName == null || playerName.trim().isEmpty()) {
            throw new PlayerRegistrationException("Player name cannot be empty");
        }
        
        try {
            // Check if player already exists
            try (PreparedStatement select = conn.prepareStatement(
                    "SELECT id FROM Players WHERE name = ?")) {
                select.setString(1, playerName);
                try (ResultSet rs = select.executeQuery()) {
                    // If player exists, return player ID
                    if (rs.next()) {
                        return rs.getInt("id");
                    }
                }
            }
            
            // If player doesn't exist, create new player
            try (PreparedStatement insert = conn.prepareStatement(
                    "INSERT INTO Players (name) VALUES (?)", Statement.RETURN_GENERATED_KEYS)) {
                insert.setString(1, playerName);
                insert.executeUpdate();

                try (ResultSet keys = insert.getGeneratedKeys()) {
                    if (keys.next()) {
                        return keys.getInt(1);
                    } else {
                        throw new PlayerRegistrationException("Failed to retrieve new player ID");
                    }
                }
            }
        } catch (SQLException e) {
            throw new PlayerRegistrationException("Database error during player registration: " + e.getMessage(), e);
        }
        
        throw new PlayerRegistrationException("Unknown error during player registration");
    }

    // Save score with custom exception
    public void saveScore(int playerId, int score) throws ScoreRecordingException {
        if (playerId <= 0) {
            throw new ScoreRecordingException("Invalid player ID: " + playerId);
        }
        
        try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO Scores (player_id, score) VALUES (?, ?)")) {
            ps.setInt(1, playerId);
            ps.setInt(2, score);
            ps.executeUpdate();
            System.out.println("Score saved successfully.");
        } catch (SQLException e) {
            throw new ScoreRecordingException("Failed to save score: " + e.getMessage(), e);
        }
    }

    // Get high score with custom exception
    public int getHighScore(int playerId) throws ScoreRecordingException {
        if (playerId <= 0) {
            throw new ScoreRecordingException("Invalid player ID: " + playerId);
        }
        
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT MAX(score) AS high_score FROM Scores WHERE player_id = ?")) {
            ps.setInt(1, playerId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("high_score");
                }
                return 0; // No scores yet
            }
        } catch (SQLException e) {
            throw new ScoreRecordingException("Failed to retrieve high score: " + e.getMessage(), e);
        }
    }
    
    // Get global high score
    public HighScoreEntry getGlobalHighScore() throws ScoreRecordingException {
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT p.name, MAX(s.score) AS high_score FROM Scores s " +
                "JOIN Players p ON s.player_id = p.id " +
                "GROUP BY p.id ORDER BY high_score DESC LIMIT 1")) {
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String name = rs.getString("name");
                    int score = rs.getInt("high_score");
                    return new HighScoreEntry(name, score);
                }
                return new HighScoreEntry("No Records", 0);
            }
        } catch (SQLException e) {
            throw new ScoreRecordingException("Failed to retrieve global high score: " + e.getMessage(), e);
        }
    }
    
    // Get top scores
    public HighScoreEntry[] getTopScores(int limit) throws ScoreRecordingException {
        if (limit <= 0) {
            throw new ScoreRecordingException("Limit must be positive");
        }
        
        HighScoreEntry[] entries = new HighScoreEntry[limit];
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT p.name, MAX(s.score) AS high_score FROM Scores s " +
                "JOIN Players p ON s.player_id = p.id " +
                "GROUP BY p.id ORDER BY high_score DESC LIMIT ?")) {
            ps.setInt(1, limit);
            try (ResultSet rs = ps.executeQuery()) {
                int index = 0;
                while (rs.next() && index < limit) {
                    String name = rs.getString("name");
                    int score = rs.getInt("high_score");
                    entries[index++] = new HighScoreEntry(name, score);
                }
                
                // Fill remaining entries if there are fewer than limit
                while (index < limit) {
                    entries[index++] = new HighScoreEntry("---", 0);
                }
                
                return entries;
            }
        } catch (SQLException e) {
            throw new ScoreRecordingException("Failed to retrieve top scores: " + e.getMessage(), e);
        }
    }

    // Close connection with custom exception
    public void closeConnection() throws DatabaseConnectionException {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
                System.out.println("Database connection closed.");
            }
        } catch (SQLException e) {
            throw new DatabaseConnectionException("Failed to close database connection: " + e.getMessage(), e);
        }
    }
    
    // Inner class for high score entries
    public static class HighScoreEntry {
        private String playerName;
        private int score;
        
        public HighScoreEntry(String playerName, int score) {
            this.playerName = playerName;
            this.score = score;
        }
        
        public String getPlayerName() {
            return playerName;
        }
        
        public int getScore() {
            return score;
        }
    }
}
