package at.ac.fhcampuswien.teamproject;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.scene.image.Image;

public class SnakeGame extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Hintergrundbild URL
        String imageUrl = "https://html5-games.io/data/image/snakelogo.png"; // Replace with your direct image URL

        // Hintergrundbild einrichten
        Image backgroundImage = new Image(imageUrl);
        BackgroundImage background = new BackgroundImage(backgroundImage, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, BackgroundSize.DEFAULT);
        Background backgroundLayout = new Background(background);

        // Layout-Pane
        VBox layout = new VBox(10);
        layout.setAlignment(Pos.CENTER);
        layout.setBackground(backgroundLayout);

        // Buttons erstellen
        Button startGameButton = new Button("Start Game");
        Button settingsButton = new Button("Settings");
        Button quitButton = new Button("Quit");
        startGameButton.setStyle("-fx-font-size: 16px; -fx-padding: 10px;");
        settingsButton.setStyle("-fx-font-size: 16px; -fx-padding: 10px;");
        quitButton.setStyle("-fx-font-size: 16px; -fx-padding: 10px;");

        // Add buttons to the layout
        layout.getChildren().addAll(startGameButton, settingsButton, quitButton);

        // Scene erstellen und anzeigen
        Scene scene = new Scene(layout, 500, 400);
        primaryStage.setTitle("Snake Game");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
