package at.ac.fhcampuswien.teamproject;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class HighScoreManager {
    private static final String DB_URL = "jdbc:sqlite:data/snakegame.db";
    private final int maxScores;
    private Connection conn;

    public HighScoreManager(int maxScores) {
        this.maxScores = maxScores;
        connectToDatabase();
        setupDatabase();
    }

    private void connectToDatabase() {
        try {
            this.conn = DriverManager.getConnection(DB_URL);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

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
