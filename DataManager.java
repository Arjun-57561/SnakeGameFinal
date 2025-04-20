import java.sql.*;

public class DataManager {
    // Replace with your actual MySQL credentials and database details
    private static final String DB_URL = "jdbc:mysql://localhost:3306/snake_game_db";
    private static final String DB_USER = "root"; // Replace with your MySQL username
    private static final String DB_PASSWORD = "Jhotikar@2005"; // Replace with your MySQL password

    private Connection conn;

    // Constructor to initialize database connection and create tables
    public DataManager() {
        try {
            // Establish connection to the MySQL database
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            System.out.println("Database connected successfully.");
            createTables(); // Ensure tables are created if they don't already exist
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Database connection failed.");
        }
    }

    // Method to create Players and Scores tables
    private void createTables() throws SQLException {
        Statement stmt = conn.createStatement();

        // Create Players table if it does not exist
        stmt.executeUpdate("""
            CREATE TABLE IF NOT EXISTS Players (
                id INT AUTO_INCREMENT PRIMARY KEY,
                name VARCHAR(100) UNIQUE NOT NULL
            )
        """);

        // Create Scores table if it does not exist (with foreign key reference to Players table)
        stmt.executeUpdate("""
            CREATE TABLE IF NOT EXISTS Scores (
                id INT AUTO_INCREMENT PRIMARY KEY,
                player_id INT NOT NULL,
                score INT NOT NULL,
                FOREIGN KEY (player_id) REFERENCES Players(id)
            )
        """);
    }

    // Method to get or create a player in the database based on player name
    public int getOrCreatePlayerId(String playerName) {
        try {
            // Check if player already exists
            PreparedStatement select = conn.prepareStatement(
                "SELECT id FROM Players WHERE name = ?");
            select.setString(1, playerName);
            ResultSet rs = select.executeQuery();

            // If player exists, return player ID
            if (rs.next()) {
                return rs.getInt("id");
            } else {
                // If player doesn't exist, create new player and return their ID
                PreparedStatement insert = conn.prepareStatement(
                    "INSERT INTO Players (name) VALUES (?)", Statement.RETURN_GENERATED_KEYS);
                insert.setString(1, playerName);
                insert.executeUpdate();

                ResultSet keys = insert.getGeneratedKeys();
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1; // If creation failed
    }

    // Method to save the player's score to the database
    public void saveScore(int playerId, int score) {
        try {
            PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO Scores (player_id, score) VALUES (?, ?)");
            ps.setInt(1, playerId);
            ps.setInt(2, score);
            ps.executeUpdate();
            System.out.println("Score saved successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Failed to save score.");
        }
    }

    // Method to get the high score of a player from the database
    public int getHighScore(int playerId) {
        try {
            PreparedStatement ps = conn.prepareStatement(
                "SELECT MAX(score) AS high_score FROM Scores WHERE player_id = ?");
            ps.setInt(1, playerId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt("high_score");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0; // If no scores found, return 0
    }

    // Method to close the database connection
    public void closeConnection() {
        try {
            if (conn != null) {
                conn.close();
                System.out.println("Database connection closed.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Failed to close the database connection.");
        }
    }
}
