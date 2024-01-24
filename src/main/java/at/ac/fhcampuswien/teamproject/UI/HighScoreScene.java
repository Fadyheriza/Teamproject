package at.ac.fhcampuswien.teamproject.UI;

import at.ac.fhcampuswien.teamproject.HighScore;
import at.ac.fhcampuswien.teamproject.SnakeGame;
import javafx.animation.FadeTransition;
import javafx.animation.FillTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.util.Duration;

import java.util.List;

public class HighScoreScene {
    public Scene scene;
    private VBox layout = new VBox(10);
    private Label titleLabel;
    private ListView<String> highScoreList;
    private SnakeGame snakeGame;
    private FadeTransition fadeTransition;
    private FillTransition fillTransition;

    public HighScoreScene(SnakeGame snakeGame) {
        this.snakeGame = snakeGame;
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(10));

        // Load the background image
        Image backgroundImage = new Image("bg2.png");
        BackgroundImage background = new BackgroundImage(backgroundImage, BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT, BackgroundSize.DEFAULT);
        layout.setBackground(new Background(background));

        titleLabel = new Label();
        titleLabel.setFont(new Font("Arial", 20));
        titleLabel.setTextFill(Color.WHITE);

        highScoreList = new ListView<>();
        highScoreList.setPrefHeight(5 * 24);

        // Set up fade-in animation for the high scores
        fadeTransition = new FadeTransition(Duration.seconds(1), highScoreList);
        fadeTransition.setFromValue(0.0);
        fadeTransition.setToValue(1.0);

        // Set up color-changing animation for the title
        fillTransition = new FillTransition(Duration.seconds(1), titleLabel.getShape());
        fillTransition.setFromValue(Color.BLACK);
        fillTransition.setToValue(Color.GREEN);

        Button backButton = new Button("Back");
        backButton.setOnAction(e -> {
            fadeTransition.play();
            fillTransition.play();
            snakeGame.primaryStage.setScene(snakeGame.gameModeScene.scene);
        });

        layout.getChildren().addAll(titleLabel, highScoreList, backButton);
        scene = new Scene(layout, 517, 412);
    }

    public void setMode(String mode) {
        titleLabel.setText(mode + " High Scores");

        // Play animations when setting the mode
        fadeTransition.play();
        fillTransition.play();
        highScoreList.getItems().clear();

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
