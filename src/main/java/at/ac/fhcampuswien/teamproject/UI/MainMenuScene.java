package at.ac.fhcampuswien.teamproject.UI;
import at.ac.fhcampuswien.teamproject.SnakeGame;
import at.ac.fhcampuswien.teamproject.StandardGameMode;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.*;

public class MainMenuScene {
    public Scene scene;
    private VBox layout = new VBox(10);

    public MainMenuScene(SnakeGame snakeGame) {
            // Background setup
            String imageUrl = "snakelogo.png";
            Image backgroundImage = new Image(imageUrl);
            BackgroundImage background = new BackgroundImage(backgroundImage, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, BackgroundSize.DEFAULT);
            Background backgroundLayout = new Background(background);

            layout.setAlignment(Pos.CENTER);
            layout.setBackground(backgroundLayout);

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
            scene = new Scene(layout, 517,412);

        }
}