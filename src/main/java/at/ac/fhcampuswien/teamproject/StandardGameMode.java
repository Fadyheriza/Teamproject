package at.ac.fhcampuswien.teamproject;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.animation.AnimationTimer;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class StandardGameMode {
    static int score = 0;
    static int speed = 5;
    static int width = 20;
    static int height = 20;
    static int appleX = 0;
    static int appleY = 0;
    static int cornersize = 25;
    static List<Corner> snake = new ArrayList<>();
    static Dir direction = Dir.left;
    static boolean gameOver = false;
    static Random rand = new Random();
    private static HighScoreManager highScoreManager;
    private static String currentPlayerUsername;
    private static Stage mainStage;
    private static Scene mainMenuScene;
    private static final double LERP_RATE = 0.1;

    private static Canvas canvas;
    private static GraphicsContext gc;
    private static AnimationTimer shakeTimer;
    private static void shakeSnake() {
        Random random = new Random();
        for (Corner c : snake) {
            c.x += random.nextInt(3) - 1; // Zufällige Verschiebung um -1, 0 oder 1
            c.y += random.nextInt(3) - 1;
        }
    }


    public enum Dir {
        left, right, up, down
    }

    public static class Corner {
        int x;
        int y;

        public Corner(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }
    private static double lerp(double start, double end, double t) {
        return start + t * (end - start);
    }
    public static Scene createGameScene(HighScoreManager scoreManager, String username) {
        highScoreManager = scoreManager;
        currentPlayerUsername = username;
        newFood();


        Pane root = new Pane();
        canvas = new Canvas(width * cornersize, height * cornersize);
        gc = canvas.getGraphicsContext2D();
        root.getChildren().add(canvas);

        new AnimationTimer() {
            long lastTick = 0;

            @Override
            public void handle(long now) {
                if (gameOver) {
                    // Stop the game loop when it's game over
                    this.stop();
                    Platform.runLater(() -> handleGameOver()); // Handle game over on the JavaFX thread
                    return;
                }

                if (lastTick == 0) {
                    lastTick = now;
                    tick(gc, (now - lastTick) / 1e9);
                    return;
                }
                if (now - lastTick > 1000000000 / speed) {
                    lastTick = now;
                    tick(gc, (now - lastTick) / 1e9);
                }
            }
        }.start();

// Create a separate AnimationTimer for the shaking effect
        shakeTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (gameOver) {
                    // Continue shaking even when it's game over
                    drawShakingSnake(gc);
                }
            }
        };
        shakeTimer.start();


        canvas.setFocusTraversable(true);
        canvas.addEventFilter(KeyEvent.KEY_PRESSED, key -> {
            if (key.getCode() == KeyCode.W && direction != Dir.down) {
                direction = Dir.up;
            }
            if (key.getCode() == KeyCode.A && direction != Dir.right) {
                direction = Dir.left;
            }
            if (key.getCode() == KeyCode.S && direction != Dir.up) {
                direction = Dir.down;
            }
            if (key.getCode() == KeyCode.D && direction != Dir.left) {
                direction = Dir.right;
            }
        });

        snake.clear();
        snake.add(new Corner(width / 2, height / 2));
        snake.add(new Corner(width / 2, height / 2));
        snake.add(new Corner(width / 2, height / 2));

        return new Scene(root, width * cornersize, height * cornersize);
    }


    public static void tick(GraphicsContext gc, double deltaTime) {

        Corner head = snake.get(0);
        int newX = head.x;
        int newY = head.y;

        switch (direction) {
            case up:
                newY--;
                break;
            case down:
                newY++;
                break;
            case left:
                newX--;
                break;
            case right:
                newX++;
                break;
        }



        if (newX < 0 || newX >= width || newY < 0 || newY >= height) {
            gameOver = true;
            return;
        }

        // Collision detection with itself
        for (int i = 1; i < snake.size(); i++) {
            Corner segment = snake.get(i);
            if (newX == segment.x && newY == segment.y) {
                gameOver = true;
                return;
            }
        }
        //moving body
        for (int i = snake.size() - 1; i > 0; i--) {
            snake.get(i).x = snake.get(i - 1).x;
            snake.get(i).y = snake.get(i - 1).y;
        }

        head.x = newX;
        head.y = newY;

        // Check if snake eats the apple
        if (appleX == head.x && appleY == head.y) {
            addNewSegment();
            newFood();
            score++;
        }

        // Render everything
        render(gc);
    }
    private static void renderBackground(GraphicsContext gc) {
        // Clear the canvas
        gc.clearRect(0, 0, width * cornersize, height * cornersize);

        // background
        String imageUrl = "https://img.freepik.com/vektoren-kostenlos/nahtloses-gruenes-grasmuster_1284-52275.jpg?size=626&ext=jpg";
        Image image = new Image(imageUrl, width * cornersize, height * cornersize, false, false);
        gc.drawImage(image, 0, 0);
    }

    private static void render(GraphicsContext gc) {
        renderBackground(gc);

        // score
        gc.setFill(Color.WHITE);
        gc.setFont(new Font("", 30));
        gc.fillText("Score: " + score, 10, 30);


        //foodcolor
        Color cc = Color.RED;

        gc.setFill(cc);
        gc.fillOval(appleX * cornersize, appleY * cornersize, cornersize, cornersize);

        // snake
        for (Corner c : snake) {
            gc.setFill(Color.DARKGRAY);
            gc.fillRect(c.x * cornersize, c.y * cornersize, cornersize - 1, cornersize - 1);
            gc.setFill(Color.BLACK);
            gc.fillRect(c.x * cornersize, c.y * cornersize, cornersize - 2, cornersize - 2);

        }

    }

    // food random places
    public static void newFood() {
        while (true) {
            appleX = rand.nextInt(width);
            appleY = rand.nextInt(height);

            boolean isOccupied = false;
            for (Corner c : snake) {
                if (c.x == appleX && c.y == appleY) {
                    isOccupied = true;
                    break;
                }
            }

            if (!isOccupied) {
                break;
            }

            if (snake.size() == width * height) {
                break;
            }
        }
    }
    public static void setMainStage(Stage stage) {
        mainStage = stage;
    }

    public static void addNewSegment() {
        Corner lastSegment = snake.get(snake.size() - 1);
        snake.add(new Corner(lastSegment.x, lastSegment.y));
    }
    public static void drawShakingSnake(GraphicsContext gc) {
        Random random = new Random();
        for (Corner c : snake) {
            int shakeX = random.nextInt(3) - 1; // Zufällige Verschiebung um -1, 0 oder 1
            int shakeY = random.nextInt(3) - 1;

            // Zeichnen der Schlange mit zufälligen Verschiebungen
            gc.setFill(Color.DARKGRAY);
            gc.fillRect(c.x * cornersize + shakeX, c.y * cornersize + shakeY, cornersize - 1, cornersize - 1);
            gc.setFill(Color.BLACK);
            gc.fillRect(c.x * cornersize + shakeX, c.y * cornersize + shakeY, cornersize - 2, cornersize - 2);
        };
    }

    public static void handleGameOver() {

            // Continue shaking animation, but stop other game logic
            gameOver = true;
            // Create game over interface elements
            VBox gameOverLayout = new VBox(20);
            gameOverLayout.setAlignment(Pos.CENTER);
            gameOverLayout.setPadding(new Insets(20, 50, 20, 50));
            gameOverLayout.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, CornerRadii.EMPTY, Insets.EMPTY)));

            // Display High Score and Username
        Label highScoreLabel = new Label("High Score: " + score);
        highScoreLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;-fx-background-color: white; -fx-text-fill: black; -fx-padding: 5;");

        Label usernameLabel = new Label("Username: " + currentPlayerUsername);
        usernameLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;-fx-background-color: white; -fx-text-fill: black; -fx-padding: 5;");


        // Play Again Button
        Button playAgainButton = new Button("Play Again");
        playAgainButton.setOnAction(e -> {
            resetGame();
            Scene gameScene = createGameScene(highScoreManager, currentPlayerUsername);
            mainStage.setScene(gameScene);
        });

        // Back to Main Menu Button
        Button backButton = new Button("Back to Main Menu");
        backButton.setOnAction(e -> mainStage.setScene(mainMenuScene));

        gameOverLayout.getChildren().addAll(highScoreLabel, usernameLabel, playAgainButton, backButton);

        // Overlay the game over layout on top of the game canvas
        StackPane root = new StackPane();
        root.getChildren().addAll(canvas, gameOverLayout); // canvas is your game canvas

        // Set the new scene, which is essentially an overlay over the existing canvas
        Scene gameOverScene = new Scene(root, canvas.getWidth(), canvas.getHeight());
        mainStage.setScene(gameOverScene);

        score = 0;

    }


    public static void resetGame() {

        if (shakeTimer != null) {
            shakeTimer.stop();
        }
        // Reset game variables
        snake.clear();
        snake.add(new Corner(width / 2, height / 2));
        snake.add(new Corner(width / 2, height / 2));
        snake.add(new Corner(width / 2, height / 2));
        direction = Dir.left;
        gameOver = false;
        newFood();
    }

    public static void setMainMenuScene(Scene scene) {
        mainMenuScene = scene;
    }

}