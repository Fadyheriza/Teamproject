package at.ac.fhcampuswien.teamproject.UI;

import at.ac.fhcampuswien.teamproject.SnakeGame;
import at.ac.fhcampuswien.teamproject.StandardGameMode;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.text.Font;

public class showInstructionsPopup {
    private VBox layout = new VBox(20);
    public Scene scene;
    private SnakeGame snakeGame; // Store reference to the SnakeGame

    public showInstructionsPopup(SnakeGame snakeGame) {
        this.snakeGame = snakeGame; // Initialize the reference

        layout.setAlignment(Pos.BOTTOM_CENTER);
        layout.setPadding(new Insets(20));

        Image backgroundImage = new Image("Instruction.jpg");
        double screenWidth = 517;
        double screenHeight = 412;
        double imageWidth = backgroundImage.getWidth();
        double imageHeight = backgroundImage.getHeight();
        double scale = Math.min(screenWidth / imageWidth, screenHeight / imageHeight);
        double scaledWidth = imageWidth * scale;
        double scaledHeight = imageHeight * scale;

        BackgroundSize backgroundSize = new BackgroundSize(scaledWidth, scaledHeight, false, false, false, false);
        BackgroundImage background = new BackgroundImage(backgroundImage, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT, backgroundSize);
        layout.setBackground(new Background(background));

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        Button startGameButton = new Button("Start Game");
        startGameButton.setFont(new Font("Arial", 18));
        startGameButton.setOnAction(e -> {
            Scene standardGameScene = StandardGameMode.createGameScene(snakeGame.standardModeHighScores, snakeGame.getUsername());
            snakeGame.primaryStage.setScene(standardGameScene);
        });

        layout.getChildren().addAll(spacer, startGameButton);
        scene = new Scene(layout, 517, 412);
    }
}

