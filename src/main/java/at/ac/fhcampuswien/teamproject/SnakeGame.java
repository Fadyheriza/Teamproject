package at.ac.fhcampuswien.teamproject;

import at.ac.fhcampuswien.teamproject.UI.*;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.image.Image;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.util.List;
import java.util.Set;


public class SnakeGame extends Application {

    public Stage primaryStage;
    public MainMenuScene mainMenuScene;
    public GameModeScene gameModeScene;

    public SettingsScene settingsScene;
    public MediaPlayer mediaPlayer;
    private UsernameScene usernameScene;
    public HighScoreScene highScoreScene;
    public showInstructionsPopup showInstructionsPopupscene;

    public Label usernameLabel = new Label();

    public HighScoreManager standardModeHighScores = new HighScoreManager(5);
    public HighScoreManager advancedModeHighScores = new HighScoreManager(5);
    String imageUrl = SnakeGame.class.getResource("/icon.png").toExternalForm();
    Image iconImage = new Image(imageUrl);


    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;

        // Initialize MediaPlayer with a music link
        try {
            String musicUrl = SnakeGame.class.getResource("/tv.mp3").toString();
            Media sound = new Media(musicUrl);
            mediaPlayer = new MediaPlayer(sound);
            mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
            mediaPlayer.play();
        } catch (Exception e) {
            System.out.println("Error loading sound: " + e.getMessage());
        }

        // Load the icon image from resources
        String imageUrl = SnakeGame.class.getResource("/icon.png").toExternalForm();
        Image iconImage = new Image(imageUrl);

        // Set the icon for the primaryStage
        primaryStage.getIcons().add(iconImage);
        // UI scenes
        mainMenuScene  = new MainMenuScene(this);
        usernameScene = new UsernameScene(this);
        settingsScene = new SettingsScene(this);
        gameModeScene = new GameModeScene(this);
        highScoreScene = new HighScoreScene(this);
        showInstructionsPopupscene = new showInstructionsPopup(this);
        // Prepare the main menu scene
        prepareMainMenuScene();
        // Set up the initial scene to the  input scene
        primaryStage.setScene(usernameScene.scene);
        // Show Main Menu
        primaryStage.setTitle("Snake Game");
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    private void prepareMainMenuScene() {
        StandardGameMode.setMainStage(primaryStage);
        StandardGameMode.setMainMenuScene(mainMenuScene.scene);
        AdvancedGameMode.setMainStage(primaryStage);
        AdvancedGameMode.setMainMenuScene(mainMenuScene.scene);
    }
    @Override
    public void stop() {
        // Close the HighScoreManager connections
        standardModeHighScores.closeConnection();
        advancedModeHighScores.closeConnection();
    }

    public String getUsername() {
        return usernameScene.getUsername();
    }
    public static void main(String[] args) {
        launch(args);
    }
}