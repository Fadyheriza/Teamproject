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



public class SettingsScene {
    public Scene scene;
    private VBox layout = new VBox(20);
    public SettingsScene(SnakeGame snakeGame) {
        layout.setAlignment(Pos.TOP_CENTER);
        layout.setPadding(new Insets(20, 50, 20, 50));

        // Background setup
        String imageUrl = "settinglogo.png";
        Image backgroundImage = new Image(imageUrl);
        BackgroundImage background = new BackgroundImage(backgroundImage, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, BackgroundSize.DEFAULT);
        Background backgroundLayout = new Background(background);
        layout.setBackground(backgroundLayout);

        // Volume Slider
        Slider volumeSlider = new Slider(0, 100, 50);
        volumeSlider.setShowTickLabels(true);
        volumeSlider.setShowTickMarks(true);
        volumeSlider.setStyle(
                "-fx-font-size: 25px; " + //  font size for the numbers
                        "-fx-font-weight: bold; " +
                        "-fx-padding: 30px; " + //  padding
                        "-fx-slider-track-color: white; " + //  track color
                        "-fx-thumb-color: red; " + //  thumb color t
                        "-fx-text-fill: red; " + // Text color
                        "-fx-effect: dropshadow(one-pass-box, white, 5, 0.5, 0, 0); " +
                        "-fx-control-inner-background: #00000055; " + // Semi-transparent black background
                        "-fx-stroke: black; " + //  color for the thumb
                        "-fx-stroke-width: 1px; " + // border width of the thumb
                        "-fx-pref-width: 200px; " + //  width of the slider
                        "-fx-pref-height: 5px; " //  height of the slider
        );
        volumeSlider.setMinWidth(300);
        volumeSlider.valueProperty().addListener((obs, oldValue, newValue) -> {
            snakeGame.mediaPlayer.setVolume(newValue.doubleValue() / 100.0);
        });

        // Mute Checkbox
        CheckBox muteCheckbox = new CheckBox("Mute Sound");
        muteCheckbox.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-padding: 10px; -fx-text-fill: black; -fx-effect: dropshadow(one-pass-box, white, 5, 0.5, 0, 0);");
        muteCheckbox.setOnAction(e -> snakeGame.mediaPlayer.setMute(muteCheckbox.isSelected()));

        // Back Button
        Button backButton = new Button("Back");
        backButton.setOnAction(e -> snakeGame.primaryStage.setScene(snakeGame.mainMenuScene.scene));

        layout.getChildren().addAll(volumeSlider, muteCheckbox, backButton);
        scene = new Scene(layout, 517,412);
    }
}