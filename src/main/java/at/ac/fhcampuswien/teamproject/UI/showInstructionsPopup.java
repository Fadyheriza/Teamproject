package at.ac.fhcampuswien.teamproject.UI;

import at.ac.fhcampuswien.teamproject.SnakeGame;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.text.Font;

public class showInstructionsPopup {
    private VBox layout = new VBox(20);
    public Scene scene;
    private Runnable gameStarter;
    public  showInstructionsPopup(SnakeGame snakeGame) {
        layout.setAlignment(Pos.BOTTOM_CENTER);
        layout.setPadding(new Insets(20));

        // Hintergrundbild einfügen
        Image backgroundImage = new Image("Instruction.jpg");

        // Passen Sie die Hintergrundgröße an den Bildschirm an
        double screenWidth = 517; // Bildschirmbreite
        double screenHeight = 412; // Bildschirmhöhe

        // Berechnen Sie die Skalierungsfaktoren, um das Seitenverhältnis des Bildes beizubehalten
        double imageWidth = backgroundImage.getWidth();
        double imageHeight = backgroundImage.getHeight();
        double scale = Math.min(screenWidth / imageWidth, screenHeight / imageHeight);
        double scaledWidth = imageWidth * scale;
        double scaledHeight = imageHeight * scale;

        BackgroundSize backgroundSize = new BackgroundSize(scaledWidth, scaledHeight, false, false, false, false);
        BackgroundImage background = new BackgroundImage(backgroundImage, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT, backgroundSize);
        layout.setBackground(new Background(background));

        // Erstellen Sie einen leeren Raum oben auf dem Bildschirm
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        // Button für den Spielstart erstellen
        Button startGameButton = new Button("Start Game");
        startGameButton.setFont(new Font("Arial", 18));
        startGameButton.setOnAction(e -> {
            layout.setVisible(false); // Verbergen Sie den Inhalt des Hauptfensters
            gameStarter.run(); // Starten Sie das Spiel
        });

        layout.getChildren().addAll(spacer, startGameButton);
        scene = new Scene(layout, 517,412);
    }
    public void setRunnable(Runnable gameStarter){
        this.gameStarter = gameStarter;


    }
}
