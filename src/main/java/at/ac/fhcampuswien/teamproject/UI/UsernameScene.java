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

public class UsernameScene {
    public Scene scene;
    private VBox layout = new VBox(10);

    private String username;


    public UsernameScene(SnakeGame snakeGame) {

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
                snakeGame.usernameLabel.setText("Username: " + username); // Update the username label
                snakeGame.primaryStage.setScene(snakeGame.mainMenuScene.scene);
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
        scene = new Scene(layout, 517,412);
    }

    public String getUsername() {
        return username;
    }


}