package at.ac.fhcampuswien.teamproject;

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


public class SnakeGame extends Application {

    private Stage primaryStage;
    private Scene mainMenuScene;
    private Scene gameModeScene;

    private Scene settingsScene;
    private MediaPlayer mediaPlayer;
    private Scene usernameInputScene;
    private String username;
    private Label usernameLabel;
    private HighScoreManager standardModeHighScores = new HighScoreManager(5);
    private HighScoreManager advancedModeHighScores = new HighScoreManager(5);


    private VBox createUsernameInputLayout() {
        VBox layout = new VBox(10);
        layout.setAlignment(Pos.CENTER);

        // Background setup (same as other scenes)
        Image backgroundImage;
        try {
            String imageUrl = "snakelogo.png";
            backgroundImage = new Image(imageUrl);
        } catch (Exception e) {
            System.out.println("Error loading image: " + e.getMessage());
            backgroundImage = null;
        }

        BackgroundImage background = new BackgroundImage(
                backgroundImage,
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                BackgroundSize.DEFAULT
        );
        layout.setBackground(new Background(background));

        // Username Input Field
        TextField usernameField = new TextField();
        usernameField.setPromptText("Enter Username");
        usernameField.setPrefWidth(200);
        usernameField.setMaxWidth(200);

        // Instruction Label
        Label instructionLabel = new Label("Enter your username (12 characters max, letters and numbers only)");
        instructionLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: black; -fx-effect: dropshadow(one-pass-box, white, 5, 0.5, 0, 0);");

        // Accept Button Logic as a Runnable
        Runnable acceptLogic = () -> {
            if (usernameField.getText().matches("[A-Za-z0-9]{1,12}")) {
                username = usernameField.getText();
                usernameLabel.setText("Username: " + username); // Update the username label
                primaryStage.setScene(mainMenuScene);
            } else {
                instructionLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: black; -fx-effect: dropshadow(one-pass-box, white, 5, 0.5, 0, 0);");
                instructionLabel.setText("Invalid username. Please try again.\n(12 characters max, letters and numbers only)");
            }
        };

        // Accept Button
        Button acceptButton = new Button("Accept");
        acceptButton.setOnAction(e -> acceptLogic.run());

        // Set Enter key to trigger Accept logic
        usernameField.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                acceptLogic.run();
            }
        });

        // Add components to layout
        layout.getChildren().addAll(instructionLabel, usernameField, acceptButton);

        return layout;
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
            // Optionally, handle the absence of sound
        }

        // Set up the initial scene to the username input scene
        VBox usernameInputLayout = createUsernameInputLayout();
        usernameInputScene = new Scene(usernameInputLayout, 517, 412);
        primaryStage.setScene(usernameInputScene);

        // Prepare the main menu scene
        prepareMainMenuScene();

        // Settings UI Setup
        VBox settingsLayout = createSettingsLayout();
        settingsScene = new Scene(settingsLayout, 517, 412);

        // Show Main Menu
        primaryStage.setTitle("Snake Game");
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    private void prepareMainMenuScene() {
        VBox mainMenuLayout = createMainMenuLayout();
        mainMenuScene = new Scene(mainMenuLayout, 517, 412);
        StandardGameMode.setMainStage(primaryStage);
        StandardGameMode.setMainMenuScene(mainMenuScene);
        AdvancedGameMode.setMainStage(primaryStage);
        AdvancedGameMode.setMainMenuScene(mainMenuScene);
    }


    private VBox createMainMenuLayout() {
        // Background setup
        String imageUrl = "snakelogo.png";
        Image backgroundImage = new Image(imageUrl);
        BackgroundImage background = new BackgroundImage(backgroundImage, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, BackgroundSize.DEFAULT);
        Background backgroundLayout = new Background(background);

        VBox layout = new VBox(10);
        layout.setAlignment(Pos.CENTER);
        layout.setBackground(backgroundLayout);

        // Start Game Button
        Button startGameButton = new Button("Start Game");
        startGameButton.setStyle("-fx-font-size: 16px; -fx-padding: 10px;");
        startGameButton.setOnAction(e -> {
            StandardGameMode.resetGame(); // Reset the game state

            VBox gameModeLayout = createGameModeLayout();
            gameModeScene = new Scene(gameModeLayout, 517, 412);
            primaryStage.setScene(gameModeScene);
        });

        // Display Username
        usernameLabel = new Label();
        usernameLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-padding: 10px; -fx-text-fill: black; -fx-effect: dropshadow(one-pass-box, white, 5, 0.5, 0, 0);");

        // Settings button action
        Button settingsButton = new Button("Settings");
        settingsButton.setStyle("-fx-font-size: 16px; -fx-padding: 10px;");
        settingsButton.setOnAction(e -> primaryStage.setScene(settingsScene));

        // Quit Button
        Button quitButton = new Button("Quit");
        quitButton.setStyle("-fx-font-size: 16px; -fx-padding: 10px;");
        quitButton.setOnAction(e -> primaryStage.close());

        layout.getChildren().addAll(usernameLabel, startGameButton, settingsButton, quitButton);

        return layout;
    }

    private VBox createGameModeLayout() {
        VBox layout = new VBox(20); // Spacing between elements
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
            Scene standardGameScene = StandardGameMode.createGameScene(standardModeHighScores, username);
            primaryStage.setScene(standardGameScene);
        });

        // Standard High Score Button
        Button standardHighScoreButton = new Button("Standard High Score");
        standardHighScoreButton.setStyle("-fx-font-size: 16px; -fx-padding: 10px;");
        standardHighScoreButton.setOnAction(e -> {
            Scene highScoreScene = createHighScoreScene("Standard");
            primaryStage.setScene(highScoreScene);
        });

        // Advanced Mode Button
        Button advancedModeButton = new Button("Advanced Mode");
        advancedModeButton.setStyle("-fx-font-size: 16px; -fx-padding: 10px;");
        advancedModeButton.setOnAction(e -> {
            Scene advancedGameMode = AdvancedGameMode.createGameScene(advancedModeHighScores, username);
            primaryStage.setScene(advancedGameMode);
        });

        // Advanced High Score Button
        Button advancedHighScoreButton = new Button("Advanced High Score");
        advancedHighScoreButton.setStyle("-fx-font-size: 16px; -fx-padding: 10px;");
        advancedHighScoreButton.setOnAction(e -> {
            Scene highScoreScene = createHighScoreScene("Advanced");
            primaryStage.setScene(highScoreScene);
        });

        // Configure HBoxes for buttons
        HBox standardModeLayout = new HBox(10, standardModeButton, standardHighScoreButton);
        standardModeLayout.setAlignment(Pos.CENTER);

        HBox advancedModeLayout = new HBox(10, advancedModeButton, advancedHighScoreButton);
        advancedModeLayout.setAlignment(Pos.CENTER);

        // Back Button
        Button backButton = new Button("Back");
        backButton.setStyle("-fx-font-size: 16px; -fx-padding: 10px;");
        backButton.setOnAction(e -> primaryStage.setScene(mainMenuScene)); // Switch back to main menu

        // Add all layouts to the main VBox
        layout.getChildren().addAll(standardModeLayout, advancedModeLayout, backButton);
        return layout;
    }
    private Scene createHighScoreScene(String mode) {
        VBox layout = new VBox(10);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(10));

        Label titleLabel = new Label(mode + " High Scores");
        titleLabel.setFont(new Font("Arial", 20));

        ListView<String> highScoreList = new ListView<>();
        highScoreList.setPrefHeight(5*24);
        List<HighScore> highScores = (mode.equals("Standard")) ? standardModeHighScores.getHighScores(mode) : advancedModeHighScores.getHighScores(mode);

        // Process and display high scores with rankings
        int rank = 1;
        for (HighScore hs : highScores) {
            String scoreEntry = rank + ": " + hs.getUsername() + " - " + hs.getScore();
            highScoreList.getItems().add(scoreEntry);
            rank++;
        }

        Button backButton = new Button("Back");
        backButton.setOnAction(e -> primaryStage.setScene(gameModeScene)); // Go back to the game mode selection scene

        layout.getChildren().addAll(titleLabel, highScoreList, backButton);

        return new Scene(layout, 517, 412);
    }


    private VBox createSettingsLayout() {
        VBox layout = new VBox(20);
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
            mediaPlayer.setVolume(newValue.doubleValue() / 100.0);
        });

        // Mute Checkbox
        CheckBox muteCheckbox = new CheckBox("Mute Sound");
        muteCheckbox.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-padding: 10px; -fx-text-fill: black; -fx-effect: dropshadow(one-pass-box, white, 5, 0.5, 0, 0);");
        muteCheckbox.setOnAction(e -> mediaPlayer.setMute(muteCheckbox.isSelected()));

        // Back Button
        Button backButton = new Button("Back");
        backButton.setOnAction(e -> primaryStage.setScene(mainMenuScene));

        layout.getChildren().addAll(volumeSlider, muteCheckbox, backButton);
        return layout;


    }
    @Override
    public void stop() {
        // Close the HighScoreManager connections
        standardModeHighScores.closeConnection();
        advancedModeHighScores.closeConnection();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
