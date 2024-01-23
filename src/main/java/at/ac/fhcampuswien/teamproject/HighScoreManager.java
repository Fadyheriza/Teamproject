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
        String sql = "CREATE TABLE IF NOT EXISTS highscores (" + "id INTEGER PRIMARY KEY AUTOINCREMENT," + "username TEXT NOT NULL," + "score INTEGER NOT NULL," + "mode TEXT NOT NULL);";
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
    public void addScore(String username, int score, String mode) {
        String insertSql = "INSERT INTO highscores (username, score, mode) VALUES (?, ?, ?)";
        try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
            insertStmt.setString(1, username);
            insertStmt.setInt(2, score);
            insertStmt.setString(3, mode);
            insertStmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        cleanupOldScores(username, mode);
    }

    /**
     * Removes old scores to maintain the maximum limit of high scores per user per mode.
     *
     * @param username The username of the player.
     * @param mode     The game mode.
     */
    private void cleanupOldScores(String username, String mode) {
        String cleanupSql = "DELETE FROM highscores WHERE id IN (" + "SELECT id FROM highscores WHERE username = ? AND mode = ? " + "ORDER BY score DESC LIMIT " + maxScores + ", 99999)";
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
}