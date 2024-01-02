package at.ac.fhcampuswien.teamproject;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;




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
        String imageUrl = "https://html5-games.io/data/image/snakelogo.png";
        Image backgroundImage = new Image(imageUrl);
        BackgroundImage background = new BackgroundImage(backgroundImage, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, BackgroundSize.DEFAULT);
        layout.setBackground(new Background(background));

        // Username Input Field
        TextField usernameField = new TextField();
        usernameField.setPromptText("Enter Username");

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
                instructionLabel.setText("Invalid username. Please try again.");
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
        String musicUrl = "https://github.com/Fadyheriza/Music/raw/main/tv.mp3";
        Media sound = new Media(musicUrl);
        mediaPlayer = new MediaPlayer(sound);
        // Set the MediaPlayer to repeat the music indefinitely
        mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
        mediaPlayer.play();

        // Username Input UI Setup
        VBox usernameInputLayout = createUsernameInputLayout();
        usernameInputScene = new Scene(usernameInputLayout, 517, 412);

        // Main Menu UI Setup
        VBox mainMenuLayout = createMainMenuLayout();

        // Settings UI Setup
        VBox settingsLayout = createSettingsLayout();

        // Prevent the window from being resizable
        primaryStage.setResizable(false);

        // Main Menu Scene
        mainMenuScene = new Scene(mainMenuLayout, 517, 412);

        // Settings Scene
        settingsScene = new Scene(settingsLayout, 517, 412);

        // Show Main Menu
        primaryStage.setTitle("Snake Game");
        primaryStage.setScene(usernameInputScene);
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
        startGameButton.setOnAction(e -> {
            VBox gameModeLayout = createGameModeLayout();
            gameModeScene = new Scene(gameModeLayout, 517, 412);
            primaryStage.setScene(gameModeScene);
        });

        // Display Username
        // Initialize and style the username label
        usernameLabel = new Label();
        usernameLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-padding: 10px; -fx-text-fill: black; -fx-effect: dropshadow(one-pass-box, white, 5, 0.5, 0, 0);");

        // Settings button action
        Button settingsButton = new Button("Settings");
        settingsButton.setStyle("-fx-font-size: 16px; -fx-padding: 10px;");
        settingsButton.setOnAction(e -> primaryStage.setScene(settingsScene)); // Switch to settings scene

        // Quit Button
        Button quitButton = new Button("Quit");
        quitButton.setStyle("-fx-font-size: 16px; -fx-padding: 10px;");
        quitButton.setOnAction(e -> primaryStage.close()); // Close the application

        layout.getChildren().addAll(usernameLabel,startGameButton, settingsButton, quitButton);
        return layout;
    }
    private VBox createGameModeLayout() {
        VBox layout = new VBox(20); // Spacing between elements
        layout.setAlignment(Pos.CENTER);
        layout.setFillWidth(true); // Ensure the VBox fills its width

        // Background setup
        String imageUrl = "https://html5-games.io/data/image/snakelogo.png";
        Image backgroundImage = new Image(imageUrl);
        BackgroundImage background = new BackgroundImage(backgroundImage, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, BackgroundSize.DEFAULT);
        Background backgroundLayout = new Background(background);
        layout.setBackground(backgroundLayout);

        // Display high scores
        Label highScoresLabel = new Label("High Scores");
        VBox highScoresList = new VBox(5);
        for (HighScore hs : standardModeHighScores.getHighScores()) {
            highScoresList.getChildren().add(new Label(hs.toString()));
        }

        // Configure HBox for Standard Mode
        HBox standardModeLayout = createModeLayout("Standard Mode", "Advanced Mod");

        // Configure HBox for Advanced Mode
        HBox advancedModeLayout = createModeLayout("Standard High Score", "Advanced High Score");

        // Back Button
        Button backButton = new Button("Back");
        backButton.setStyle("-fx-font-size: 16px; -fx-padding: 10px;");
        backButton.setOnAction(e -> primaryStage.setScene(mainMenuScene)); // Switch back to main menu

        // Add all layouts to the main VBox
        layout.getChildren().addAll(highScoresLabel, highScoresList, backButton);
        return layout;
    }

    private HBox createModeLayout(String modeButtonText, String highScoreButtonText) {
        HBox modeLayout = new HBox(10); // Spacing between buttons
        modeLayout.setAlignment(Pos.CENTER);

        Button modeButton = new Button(modeButtonText);
        Button highScoreButton = new Button(highScoreButtonText);

        modeButton.setStyle("-fx-font-size: 16px; -fx-padding: 10px;");
        highScoreButton.setStyle("-fx-font-size: 16px; -fx-padding: 10px;");

        modeLayout.getChildren().addAll(modeButton, highScoreButton);
        return modeLayout;
    }



    private VBox createSettingsLayout() {
        VBox layout = new VBox(20);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20, 50, 20, 50));

        // Background setup
        String imageUrl = "https://www.shutterstock.com/image-vector/setting-maintenance-icon-260nw-1053800327.jpg";
        Image backgroundImage = new Image(imageUrl);
        BackgroundImage background = new BackgroundImage(backgroundImage, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, BackgroundSize.DEFAULT);
        Background backgroundLayout = new Background(background);
        layout.setBackground(backgroundLayout);

        // Volume Slider
        Slider volumeSlider = new Slider(0, 100, 50);
        volumeSlider.setShowTickLabels(true);
        volumeSlider.setShowTickMarks(true);
        volumeSlider.setStyle(
                "-fx-font-size: 25px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-padding: 18px; " +
                        "-fx-slider-track-color: blue; " + // Color of the track
                        "-fx-thumb-color: red; " + // Color of the thumb
                        "-fx-text-fill: black; " +
                        "-fx-effect: dropshadow(one-pass-box, white, 5, 0.5, 0, 0); " +
                        "-fx-control-inner-background: orange; " + // Background of the slider
                        "-fx-stroke: green; " + // Border color of the thumb
                        "-fx-stroke-width: 2px; " + // Border width of the thumb
                        "-fx-pref-width: 200px; " + // Preferred width of the slider
                        "-fx-pref-height: 30px; " // Preferred height of the slider
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


    public static void main(String[] args) {
        launch(args);
    }
}
