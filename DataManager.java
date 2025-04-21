import java.sql.*;

public class DataManager {

    // Database connection details
    private static final String DB_URL = "jdbc:mysql://localhost:3306/snake_game_db";

    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "Jhotika@####";


    private Connection conn; // JDBC connection object

    // Constructor: Establishes connection and initializes the database schema
    public DataManager() {
        try {
            // Connect to MySQL database
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            System.out.println("Database connected successfully.");

            // Create necessary tables if they don't already exist
            createTables();
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Database connection failed.");
        }
    }

    // Creates Players and Scores tables with appropriate constraints
    private void createTables() throws SQLException {
        Statement stmt = conn.createStatement();

        // SQL to create the Players table
        stmt.executeUpdate("""
            CREATE TABLE IF NOT EXISTS Players (
                id INT AUTO_INCREMENT PRIMARY KEY,
                name VARCHAR(100) UNIQUE NOT NULL
            )
        """);

        // SQL to create the Scores table with a foreign key reference to Players
        stmt.executeUpdate("""
            CREATE TABLE IF NOT EXISTS Scores (
                id INT AUTO_INCREMENT PRIMARY KEY,
                player_id INT NOT NULL,
                score INT NOT NULL,
                FOREIGN KEY (player_id) REFERENCES Players(id)
            )
        """);
    }

    // Retrieves a player's ID by name, or creates a new record if player doesn't exist
    public int getOrCreatePlayerId(String playerName) {
        try {
            // Try to find the player by name
            PreparedStatement select = conn.prepareStatement(
                "SELECT id FROM Players WHERE name = ?");
            select.setString(1, playerName);
            ResultSet rs = select.executeQuery();

            if (rs.next()) {
                // If player exists, return the ID
                return rs.getInt("id");
            } else {
                // Player doesn't exist — insert a new player record
                PreparedStatement insert = conn.prepareStatement(
                    "INSERT INTO Players (name) VALUES (?)", Statement.RETURN_GENERATED_KEYS);
                insert.setString(1, playerName);
                insert.executeUpdate();

                // Retrieve the auto-generated ID of the new player
                ResultSet keys = insert.getGeneratedKeys();
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1; // Return -1 if something failed
    }

    // Inserts a player's score into the Scores table
    public void saveScore(int playerId, int score) {
        try {
            // Prepare an INSERT statement for Scores table
            PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO Scores (player_id, score) VALUES (?, ?)");
            ps.setInt(1, playerId); // Set the player ID
            ps.setInt(2, score);    // Set the score
            ps.executeUpdate();     // Execute the INSERT
            System.out.println("Score saved successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Failed to save score.");
        }
    }

    // Fetches the highest score of a specific player
    public int getHighScore(int playerId) {
        try {
            // Prepare a query to get the MAX score for a player
            PreparedStatement ps = conn.prepareStatement(
                "SELECT MAX(score) AS high_score FROM Scores WHERE player_id = ?");
            ps.setInt(1, playerId); // Bind the player ID
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt("high_score"); // Return the highest score
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0; // If player has no scores or error occurs, return 0
    }

    // Closes the database connection safely
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
