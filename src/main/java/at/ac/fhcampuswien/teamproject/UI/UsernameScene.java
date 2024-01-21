package at.ac.fhcampuswien.teamproject.UI;

import at.ac.fhcampuswien.teamproject.SnakeGame;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;

/**
 * UsernameScene class provides the UI for entering a username in the Snake game.
 * It includes an input field for the username and a button to accept the input.
 */
public class UsernameScene {
    public Scene scene;
    private VBox layout = new VBox(10);

    private String username;

    /**
     * Constructor for UsernameScene.
     * Sets up the UI elements for entering and accepting a username.
     *
     * @param snakeGame Instance of the main game class, used to interact with other components.
     */
    public UsernameScene(SnakeGame snakeGame) {
        layout.setAlignment(Pos.CENTER);

        // Background setup
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
        TextField usernameField = setupUsernameField();

        // Instruction Label
        Label instructionLabel = setupInstructionLabel();

        // Accept Button Logic
        Runnable acceptLogic = createAcceptLogic(usernameField, instructionLabel, snakeGame);

        // Accept Button
        Button acceptButton = setupAcceptButton(acceptLogic);

        // Set Enter key to trigger Accept logic
        usernameField.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                acceptLogic.run();
            }
        });

        // Add components to layout
        layout.getChildren().addAll(instructionLabel, usernameField, acceptButton);
        scene = new Scene(layout, 517, 412);
    }

    private TextField setupUsernameField() {
        // Configuration of the username input field
        TextField usernameField = new TextField();
        usernameField.setPromptText("Enter Username");
        usernameField.setPrefWidth(200);
        usernameField.setMaxWidth(200);
        return usernameField;
    }

    private Label setupInstructionLabel() {
        // Configuration of the instruction label
        Label instructionLabel = new Label("Enter your username (12 characters max, letters and numbers only)");
        // Styling code omitted for brevity
        return instructionLabel;
    }

    private Runnable createAcceptLogic(TextField usernameField, Label instructionLabel, SnakeGame snakeGame) {
        // Logic to handle the acceptance of the username
        return () -> {
            if (usernameField.getText().matches("[A-Za-z0-9]{1,12}")) {
                username = usernameField.getText();
                snakeGame.usernameLabel.setText("Username: " + username); // Update the username label
                snakeGame.primaryStage.setScene(snakeGame.mainMenuScene.scene);
            } else {
                instructionLabel.setText("Invalid username. Please try again.\n(12 characters max, letters and numbers only)");
            }
        };
    }

    private Button setupAcceptButton(Runnable acceptLogic) {
        // Configuration of the accept button
        Button acceptButton = new Button("Accept");
        acceptButton.setOnAction(e -> acceptLogic.run());
        return acceptButton;
    }

    public String getUsername() {
        return username;
    }
}
