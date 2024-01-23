package at.ac.fhcampuswien.teamproject;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * HighScoreManager handles database interactions for storing and retrieving high scores.
 * It uses SQLite for database operations.
 */
public class HighScoreManager {
    private static final String DB_URL = "jdbc:sqlite:data/snakegame.db"; // Database URL
    private final int maxScores; // Maximum number of scores to retain
    private Connection conn; // Database connection

    /**
     * Constructor for HighScoreManager.
     * Connects to the database and sets up the table if necessary.
     *
     * @param maxScores Maximum number of high scores to be stored per mode.
     */
    public HighScoreManager(int maxScores) {
        this.maxScores = maxScores;
        connectToDatabase();
        setupDatabase();
    }

    /**
     * Establishes a connection to the SQLite database.
     */
    private void connectToDatabase() {
        try {
            this.conn = DriverManager.getConnection(DB_URL);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sets up the high scores table in the database if it doesn't already exist.
     */
    private void setupDatabase() {
        String sql = "CREATE TABLE IF NOT EXISTS highscores (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "username TEXT NOT NULL," +
                "score INTEGER NOT NULL," +
                "mode TEXT NOT NULL);";
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Adds a new high score entry to the database.
     *
     * @param username The username of the player.
     * @param score    The score achieved by the player.
     * @param mode     The game mode in which the score was achieved.
     */



    /**
     * Removes old scores to maintain the maximum limit of high scores per user per mode.
     *
     * @param username The username of the player.
     * @param mode     The game mode.
     */
    private void cleanupOldScores(String username, String mode) {
        String cleanupSql = "DELETE FROM highscores WHERE id IN (" +
                "SELECT id FROM highscores WHERE username = ? AND mode = ? " +
                "ORDER BY score DESC LIMIT " + maxScores + ", 99999)";
        try (PreparedStatement cleanupStmt = conn.prepareStatement(cleanupSql)) {
            cleanupStmt.setString(1, username);
            cleanupStmt.setString(2, mode);
            cleanupStmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    /**
     * Retrieves the high scores for a given game mode.
     *
     * @param mode The game mode for which to retrieve high scores.
     * @return A list of HighScore objects.
     */
    public List<HighScore> getHighScores(String mode) {
        List<HighScore> scores = new ArrayList<>();
        String sql = "SELECT username, score FROM highscores WHERE mode = ? ORDER BY score DESC LIMIT ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, mode);
            pstmt.setInt(2, maxScores);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    scores.add(new HighScore(rs.getString("username"), rs.getInt("score")));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return scores;
    }



    /**
     * Finalizes the object by closing the database connection.
     */
    @Override
    protected void finalize() throws Throwable {
        closeConnection();
        super.finalize();
    }

    /**
     * Closes the database connection.
     */
    public void closeConnection() {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean usernameExists(String username, String mode) {
        String checkUsernameSql = "SELECT COUNT(*) FROM highscores WHERE username = ? AND mode = ?";
        try (PreparedStatement checkUsernameStmt = conn.prepareStatement(checkUsernameSql)) {
            checkUsernameStmt.setString(1, username);
            checkUsernameStmt.setString(2, mode);
            try (ResultSet rs = checkUsernameStmt.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void updateScore(String username, int score, String mode) {
        String updateSql = "UPDATE highscores SET score = ? WHERE username = ? AND mode = ?";
        try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
            updateStmt.setInt(1, score);
            updateStmt.setString(2, username);
            updateStmt.setString(3, mode);
            updateStmt.executeUpdate();

            // Cleanup old scores if necessary
            cleanupOldScores(username, mode);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void addScore(String username, int score, String mode) {
        // Check if the username already exists for the given mode
        if (usernameExists(username, mode)) {
            // Get the existing score for the user
            int existingScore = getExistingScore(username, mode);

            // Update the existing score if the new score is higher
            if (score > existingScore) {
                updateScore(username, score, mode);
            }
        } else {
            // Add a new score since the user doesn't exist
            insertNewScore(username, score, mode);
        }
    }


    // Method to get the existing score for a user in a specific mode
    private int getExistingScore(String username, String mode) {
        String getScoreSql = "SELECT score FROM highscores WHERE username = ? AND mode = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(getScoreSql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, mode);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("score");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private void insertNewScore(String username, int score, String mode) {
        String insertSql = "INSERT INTO highscores (username, score, mode) VALUES (?, ?, ?)";
        try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
            insertStmt.setString(1, username);
            insertStmt.setInt(2, score);
            insertStmt.setString(3, mode);
            insertStmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error executing SQL statement:");
            e.printStackTrace();
        }
    }

}
