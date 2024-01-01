package at.ac.fhcampuswien.teamproject;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Slider;
import javafx.scene.layout.*;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import java.io.File;

public class SnakeGame extends Application {

    private Stage primaryStage;
    private Scene mainMenuScene;
    private Scene settingsScene;
    private MediaPlayer mediaPlayer;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;

        // Initialize MediaPlayer with a music link
        String musicUrl = "https://github.com/Fadyheriza/Music/raw/main/tv.mp3";
        Media sound = new Media(musicUrl);
        mediaPlayer = new MediaPlayer(sound);
        mediaPlayer.play(); // Start playing the music

        // Main Menu UI Setup
        VBox mainMenuLayout = createMainMenuLayout();

        // Settings UI Setup
        VBox settingsLayout = createSettingsLayout();

        // Main Menu Scene
        mainMenuScene = new Scene(mainMenuLayout, 517, 412);

        // Settings Scene
        settingsScene = new Scene(settingsLayout, 517, 412);

        // Show Main Menu
        primaryStage.setTitle("Snake Game");
        primaryStage.setScene(mainMenuScene);
        primaryStage.show();
    }

    private VBox createMainMenuLayout() {
        // Background setup
        String imageUrl = "https://html5-games.io/data/image/snakelogo.png";
        Image backgroundImage = new Image(imageUrl);
        BackgroundImage background = new BackgroundImage(backgroundImage, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, BackgroundSize.DEFAULT);
        Background backgroundLayout = new Background(background);

        VBox layout = new VBox(10);
        layout.setAlignment(Pos.CENTER);
        layout.setBackground(backgroundLayout);

        // Start Game Button
        Button startGameButton = new Button("Start Game");
        startGameButton.setStyle("-fx-font-size: 16px; -fx-padding: 10px;");

        // Settings button action
        Button settingsButton = new Button("Settings");
        settingsButton.setStyle("-fx-font-size: 16px; -fx-padding: 10px;");
        settingsButton.setOnAction(e -> primaryStage.setScene(settingsScene)); // Switch to settings scene

        // Quit Button
        Button quitButton = new Button("Quit");
        quitButton.setStyle("-fx-font-size: 16px; -fx-padding: 10px;");
        quitButton.setOnAction(e -> primaryStage.close()); // Close the application

        layout.getChildren().addAll(startGameButton, settingsButton, quitButton);
        return layout;
    }

    private VBox createSettingsLayout() {
        VBox layout = new VBox(20);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20, 50, 20, 50));

        // Volume Slider
        Slider volumeSlider = new Slider(0, 100, 50);
        volumeSlider.setShowTickLabels(true);
        volumeSlider.setShowTickMarks(true);
        volumeSlider.setStyle("-fx-font-size: 16px;");
        volumeSlider.setMinWidth(300);
        volumeSlider.valueProperty().addListener((obs, oldValue, newValue) -> {
            mediaPlayer.setVolume(newValue.doubleValue() / 100.0);
        });

        // Mute Checkbox
        CheckBox muteCheckbox = new CheckBox("Mute Sound");
        muteCheckbox.setOnAction(e -> mediaPlayer.setMute(muteCheckbox.isSelected()));

        // Back Button
        Button backButton = new Button("Back");
        backButton.setOnAction(e -> primaryStage.setScene(mainMenuScene));

        layout.getChildren().addAll(volumeSlider, muteCheckbox, backButton);
        return layout;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
