package at.ac.fhcampuswien.teamproject.UI;
import at.ac.fhcampuswien.teamproject.AdvancedGameMode;
import at.ac.fhcampuswien.teamproject.SnakeGame;
import at.ac.fhcampuswien.teamproject.StandardGameMode;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.*;

public class GameModeScene {
    public Scene scene;
    private VBox layout = new VBox(20);
    public GameModeScene(SnakeGame snakeGame)
    {
        layout.setAlignment(Pos.CENTER);
        layout.setFillWidth(true); // Ensure the VBox fills its width

        // Background setup
        String imageUrl = "snakelogo.png";
        Image backgroundImage = new Image(imageUrl);
        BackgroundImage background = new BackgroundImage(backgroundImage, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, BackgroundSize.DEFAULT);
        Background backgroundLayout = new Background(background);
        layout.setBackground(backgroundLayout);

        // Standard Mode Button
        Button standardModeButton = new Button("Standard Mode");
        standardModeButton.setStyle("-fx-font-size: 16px; -fx-padding: 10px;");
        standardModeButton.setOnAction(e -> {
            // Logic to start the standard mode game
            Scene standardGameScene = StandardGameMode.createGameScene(snakeGame.standardModeHighScores,snakeGame.getUsername());
            snakeGame.primaryStage.setScene(standardGameScene);
        });

        // Standard High Score Button
        Button standardHighScoreButton = new Button("Standard High Score");
        standardHighScoreButton.setStyle("-fx-font-size: 16px; -fx-padding: 10px;");
        standardHighScoreButton.setOnAction(e -> {
            snakeGame.highScoreScene.setmode("Standard");
            snakeGame.primaryStage.setScene(snakeGame.highScoreScene.scene);
        });

        // Advanced Mode Button
        Button advancedModeButton = new Button("Advanced Mode");
        advancedModeButton.setStyle("-fx-font-size: 16px; -fx-padding: 10px;");
        advancedModeButton.setOnAction(e -> {
            Scene advancedGameMode = AdvancedGameMode.createGameScene(snakeGame.advancedModeHighScores,snakeGame.getUsername());
            snakeGame.primaryStage.setScene(advancedGameMode);
        });

        // Advanced High Score Button
        Button advancedHighScoreButton = new Button("Advanced High Score");
        advancedHighScoreButton.setStyle("-fx-font-size: 16px; -fx-padding: 10px;");
        advancedHighScoreButton.setOnAction(e -> {
            snakeGame.highScoreScene.setmode("Advanced");
            snakeGame.primaryStage.setScene(snakeGame.highScoreScene.scene);
        });

        // Configure HBoxes for buttons
        HBox standardModeLayout = new HBox(10, standardModeButton, standardHighScoreButton);
        standardModeLayout.setAlignment(Pos.CENTER);

        HBox advancedModeLayout = new HBox(10, advancedModeButton, advancedHighScoreButton);
        advancedModeLayout.setAlignment(Pos.CENTER);

        // Back Button
        Button backButton = new Button("Back");
        backButton.setStyle("-fx-font-size: 16px; -fx-padding: 10px;");
        backButton.setOnAction(e ->   snakeGame.primaryStage.setScene(snakeGame.mainMenuScene.scene)); // Switch back to main menu

        // Add all layouts to the main VBox
        layout.getChildren().addAll(standardModeLayout, advancedModeLayout, backButton);
        scene = new Scene(layout, 517,412);
    }
    public Parent getLayout() {
        return layout ;
    }
}