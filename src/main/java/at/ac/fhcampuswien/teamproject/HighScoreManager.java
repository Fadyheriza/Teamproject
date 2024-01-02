package at.ac.fhcampuswien.teamproject;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HighScoreManager {

    private final List<HighScore> highScores;
    private final int maxScores;

    private static final String DB_URL = "jdbc:sqlite:snakegame.db";

    public HighScoreManager(int maxScores) {
        this.highScores = new ArrayList<>();
        this.maxScores = maxScores;
        setupDatabase();
    }

    private void setupDatabase() {
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            String sql = "CREATE TABLE IF NOT EXISTS highscores (" +
                    "username TEXT NOT NULL," +
                    "score INTEGER NOT NULL," +
                    "mode TEXT NOT NULL);";
            try (Statement stmt = conn.createStatement()) {
                stmt.execute(sql);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addScore(String username, int score, String mode) {
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            // Check if the user already has a score
            String checkSql = "SELECT score FROM highscores WHERE username = ? AND mode = ?";
            try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                checkStmt.setString(1, username);
                checkStmt.setString(2, mode);
                try (ResultSet rs = checkStmt.executeQuery()) {
                    if (rs.next()) {
                        int existingScore = rs.getInt("score");
                        if (score > existingScore) {
                            // Update the score if the new score is higher
                            String updateSql = "UPDATE highscores SET score = ? WHERE username = ? AND mode = ?";
                            try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                                updateStmt.setInt(1, score);
                                updateStmt.setString(2, username);
                                updateStmt.setString(3, mode);
                                updateStmt.executeUpdate();
                            }
                        }
                    } else {
                        // Insert new score if the user doesn't have a score yet
                        String insertSql = "INSERT INTO highscores (username, score, mode) VALUES (?, ?, ?)";
                        try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                            insertStmt.setString(1, username);
                            insertStmt.setInt(2, score);
                            insertStmt.setString(3, mode);
                            insertStmt.executeUpdate();
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public List<HighScore> getHighScores(String mode) {
        List<HighScore> scores = new ArrayList<>();
        String sql = "SELECT username, MAX(score) as score FROM highscores WHERE mode = ? GROUP BY username ORDER BY score DESC LIMIT ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
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


}
