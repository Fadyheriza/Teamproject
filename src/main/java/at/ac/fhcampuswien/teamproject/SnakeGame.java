package at.ac.fhcampuswien.teamproject;

import at.ac.fhcampuswien.teamproject.UI.*;
import javafx.application.Application;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

/**
 * SnakeGame class extends Application and serves as the main class for the Snake game.
 * It initializes and manages different scenes and settings for the game.
 */
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

    private boolean instructionsShown = false;

    public boolean isInstructionsShown() {
        return instructionsShown;
    }

    public void setInstructionsShown(boolean instructionsShown) {
        this.instructionsShown = instructionsShown;
    }

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

        // Initialize UI scenes
        mainMenuScene = new MainMenuScene(this);
        usernameScene = new UsernameScene(this);
        settingsScene = new SettingsScene(this);
        gameModeScene = new GameModeScene(this);
        highScoreScene = new HighScoreScene(this);
        showInstructionsPopupscene = new showInstructionsPopup(this);

        // Prepare the main menu scene
        prepareMainMenuScene();

        // Set up the initial scene to the username scene
        primaryStage.setScene(usernameScene.scene);

        // Configure and show the primary stage
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
