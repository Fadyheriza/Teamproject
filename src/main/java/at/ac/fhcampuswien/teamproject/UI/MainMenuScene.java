package at.ac.fhcampuswien.teamproject.UI;

import at.ac.fhcampuswien.teamproject.SnakeGame;
import at.ac.fhcampuswien.teamproject.StandardGameMode;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.*;

/**
 * MainMenuScene class sets up the main menu UI for the Snake game.
 * It includes options to start the game, access settings, and quit the game.
 */
public class MainMenuScene {
    public Scene scene;
    private VBox layout = new VBox(10);

    /**
     * Constructor for MainMenuScene.
     * Initializes the layout, background, and buttons for the main menu.
     *
     * @param snakeGame Instance of the main game class, used to interact with other components.
     */
    public MainMenuScene(SnakeGame snakeGame) {
        // Try-catch block for loading the background image
        try {
            String imageUrl = "snakelogo.png";
            Image backgroundImage = new Image(imageUrl);
            BackgroundImage background = new BackgroundImage(backgroundImage, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, BackgroundSize.DEFAULT);
            Background backgroundLayout = new Background(background);

            layout.setAlignment(Pos.CENTER);
            layout.setBackground(backgroundLayout);
        } catch (Exception e) {
            System.out.println("Error loading background image: " + e.getMessage());
        }

        // Start Game Button
        Button startGameButton = new Button("Start Game");
        startGameButton.setStyle("-fx-font-size: 16px; -fx-padding: 10px;");
        startGameButton.setOnAction(e -> {
            StandardGameMode.resetGame(); // Reset the game state
            snakeGame.primaryStage.setScene(snakeGame.gameModeScene.scene);
        });

        // Display Username
        snakeGame.usernameLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-padding: 10px; -fx-text-fill: black; -fx-effect: dropshadow(one-pass-box, white, 5, 0.5, 0, 0);");

        // Settings button action
        Button settingsButton = new Button("Settings");
        settingsButton.setStyle("-fx-font-size: 16px; -fx-padding: 10px;");
        settingsButton.setOnAction(e -> snakeGame.primaryStage.setScene(snakeGame.settingsScene.scene));

        // Quit Button
        Button quitButton = new Button("Quit");
        quitButton.setStyle("-fx-font-size: 16px; -fx-padding: 10px;");
        quitButton.setOnAction(e -> snakeGame.primaryStage.close());

        layout.getChildren().addAll(snakeGame.usernameLabel, startGameButton, settingsButton, quitButton);
        scene = new Scene(layout, 517, 412);
    }
}
