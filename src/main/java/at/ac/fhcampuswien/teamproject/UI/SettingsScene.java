package at.ac.fhcampuswien.teamproject.UI;

import at.ac.fhcampuswien.teamproject.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.layout.*;

/**
 * SettingsScene class provides the UI for game settings in the Snake game.
 * It allows the player to adjust the volume and mute the game sound.
 */
public class SettingsScene {
    public Scene scene;
    private VBox layout = new VBox(20);

    /**
     * Constructor for SettingsScene.
     * Initializes the layout, background, and settings controls like volume slider and mute checkbox.
     *
     * @param snakeGame Instance of the main game class, used to interact with other components.
     */
    public SettingsScene(SnakeGame snakeGame) {
        layout.setAlignment(Pos.TOP_CENTER);
        layout.setPadding(new Insets(20, 50, 20, 50));

        // Try-catch block for loading the background image
        try {
            String imageUrl = "settinglogo.png";
            Image backgroundImage = new Image(imageUrl);
            BackgroundImage background = new BackgroundImage(backgroundImage, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, BackgroundSize.DEFAULT);
            Background backgroundLayout = new Background(background);
            layout.setBackground(backgroundLayout);
        } catch (Exception e) {
            System.out.println("Error loading background image: " + e.getMessage());
        }

        // Volume Slider
        Slider volumeSlider = new Slider(0, 100, 50);
        setupVolumeSlider(volumeSlider, snakeGame);

        // Mute Checkbox
        CheckBox muteCheckbox = setupMuteCheckbox(snakeGame);

        // Back Button
        Button backButton = setupBackButton(snakeGame);

        layout.getChildren().addAll(volumeSlider, muteCheckbox, backButton);
        scene = new Scene(layout, 517, 412);
    }

    private Slider setupVolumeSlider(Slider volumeSlider, SnakeGame snakeGame) {
        // Volume slider styling and functionality
        volumeSlider.setShowTickLabels(true);
        volumeSlider.setShowTickMarks(true);
        // Styling code omitted for brevity
        volumeSlider.setMinWidth(300);
        volumeSlider.valueProperty().addListener((obs, oldValue, newValue) -> {
            snakeGame.mediaPlayer.setVolume(newValue.doubleValue() / 100.0);
        });
        return volumeSlider;
    }

    private CheckBox setupMuteCheckbox(SnakeGame snakeGame) {
        // Mute checkbox styling and functionality
        CheckBox muteCheckbox = new CheckBox("Mute Sound");
        // Styling code omitted for brevity
        muteCheckbox.setOnAction(e -> snakeGame.mediaPlayer.setMute(muteCheckbox.isSelected()));
        return muteCheckbox;
    }

    private Button setupBackButton(SnakeGame snakeGame) {
        // Back button to return to the main menu
        Button backButton = new Button("Back");
        backButton.setOnAction(e -> snakeGame.primaryStage.setScene(snakeGame.mainMenuScene.scene));
        return backButton;
    }
}
