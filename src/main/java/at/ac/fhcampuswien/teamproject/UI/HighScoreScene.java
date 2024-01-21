package at.ac.fhcampuswien.teamproject.UI;

import at.ac.fhcampuswien.teamproject.HighScore;
import at.ac.fhcampuswien.teamproject.SnakeGame;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;

import java.util.List;

/**
 * HighScoreScene class provides the UI to display the high scores in the Snake game.
 * It uses JavaFX for rendering the UI components.
 */
public class HighScoreScene {
    public Scene scene;
    private VBox layout = new VBox(10);
    private Label titleLabel;
    private ListView<String> highScoreList;
    private SnakeGame snakeGame;

    /**
     * Sets up the user interface for displaying high scores, including a title, a list of high scores, and a back button.
     *
     * @param snakeGame Instance of the SnakeGame, used to interact with other components of the game.
     */
    public HighScoreScene(SnakeGame snakeGame) {
        this.snakeGame = snakeGame;
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(10));

        titleLabel = new Label();
        titleLabel.setFont(new Font("Arial", 20));

        highScoreList = new ListView<>();
        highScoreList.setPrefHeight(5 * 24);

        Button backButton = new Button("Back");
        backButton.setOnAction(e -> snakeGame.primaryStage.setScene(snakeGame.gameModeScene.scene)); // Go back to the game mode selection scene

        layout.getChildren().addAll(titleLabel, highScoreList, backButton);
        scene = new Scene(layout, 517, 412);
    }

    /**
     * Updates the UI with the high scores for the specified game mode.
     * Clears the current high score list and adds new entries based on the provided mode.
     *
     * @param mode The game mode for which high scores are to be displayed.
     */
    public void setmode(String mode) {
        titleLabel.setText(mode + " High Scores");
        highScoreList.getItems().clear(); // Clear existing items

        List<HighScore> highScores = mode.equals("Standard") ?
                snakeGame.standardModeHighScores.getHighScores(mode) :
                snakeGame.advancedModeHighScores.getHighScores(mode);

        int rank = 1;
        for (HighScore hs : highScores) {
            String scoreEntry = rank + ": " + hs.getUsername() + " - " + hs.getScore();
            highScoreList.getItems().add(scoreEntry);
            rank++;
        }
    }
}
