package at.ac.fhcampuswien.teamproject.UI;

import at.ac.fhcampuswien.teamproject.AdvancedGameMode;
import at.ac.fhcampuswien.teamproject.SnakeGame;
import at.ac.fhcampuswien.teamproject.StandardGameMode;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.*;

/**
 * GameModeScene class provides the UI for selecting the game mode in the Snake game.
 * It presents the user with options to choose between Standard and Advanced modes
 * and to view high scores for each mode.
 */
public class GameModeScene {
    public Scene scene;
    private VBox layout = new VBox(20);

    /**
     * Constructor for GameModeScene.
     * Initializes the layout, background, and buttons for selecting game modes and viewing high scores.
     *
     * @param snakeGame Instance of the main game class, used to interact with other components.
     */
    public GameModeScene(SnakeGame snakeGame) {
        layout.setAlignment(Pos.CENTER);
        layout.setFillWidth(true);

        // Try-catch block for loading the background image
        try {
            String imageUrl = "snakelogo.png";
            Image backgroundImage = new Image(imageUrl);
            BackgroundImage background = new BackgroundImage(backgroundImage, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, BackgroundSize.DEFAULT);
            layout.setBackground(new Background(background));
        } catch (Exception e) {
            System.out.println("Error loading background image: " + e.getMessage());
        }

        // Button for starting the Standard Game Mode
        Button standardModeButton = new Button("Standard Mode");
        standardModeButton.setStyle("-fx-font-size: 16px; -fx-padding: 10px;");
        standardModeButton.setOnAction(e -> {
            if (!snakeGame.isInstructionsShown()) {
                snakeGame.setInstructionsShown(true);
                snakeGame.primaryStage.setScene(snakeGame.showInstructionsPopupscene.scene);
            } else {
                Scene standardGameScene = StandardGameMode.createGameScene(snakeGame.standardModeHighScores, snakeGame.getUsername());
                snakeGame.primaryStage.setScene(standardGameScene);
            }
        });

        // Button for viewing Standard Mode high scores
        Button standardHighScoreButton = new Button("Standard High Score");
        standardHighScoreButton.setStyle("-fx-font-size: 16px; -fx-padding: 10px;");
        standardHighScoreButton.setOnAction(e -> {
            snakeGame.highScoreScene.setmode("Standard");
            snakeGame.primaryStage.setScene(snakeGame.highScoreScene.scene);
        });

        // Button for starting the Advanced Game Mode
        Button advancedModeButton = new Button("Advanced Mode");
        advancedModeButton.setStyle("-fx-font-size: 16px; -fx-padding: 10px;");
        advancedModeButton.setOnAction(e -> {
            Scene advancedGameMode = AdvancedGameMode.createGameScene(snakeGame.advancedModeHighScores, snakeGame.getUsername());
            snakeGame.primaryStage.setScene(advancedGameMode);
        });

        // Button for viewing Advanced Mode high scores
        Button advancedHighScoreButton = new Button("Advanced High Score");
        advancedHighScoreButton.setStyle("-fx-font-size: 16px; -fx-padding: 10px;");
        advancedHighScoreButton.setOnAction(e -> {
            snakeGame.highScoreScene.setmode("Advanced");
            snakeGame.primaryStage.setScene(snakeGame.highScoreScene.scene);
        });

        HBox standardModeLayout = new HBox(10, standardModeButton, standardHighScoreButton);
        standardModeLayout.setAlignment(Pos.CENTER);

        HBox advancedModeLayout = new HBox(10, advancedModeButton, advancedHighScoreButton);
        advancedModeLayout.setAlignment(Pos.CENTER);

        // Back button to return to the main menu
        Button backButton = new Button("Back");
        backButton.setStyle("-fx-font-size: 16px; -fx-padding: 10px;");
        backButton.setOnAction(e -> snakeGame.primaryStage.setScene(snakeGame.mainMenuScene.scene));

        layout.getChildren().addAll(standardModeLayout, advancedModeLayout, backButton);
        scene = new Scene(layout, 517, 412);
    }
}
