package at.ac.fhcampuswien.teamproject;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.Pane;
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
        Canvas c = new Canvas(width * cornersize, height * cornersize);
        GraphicsContext gc = c.getGraphicsContext2D();
        root.getChildren().add(c);

        new AnimationTimer() {
            long lastTick = 0;

            @Override
            public void handle(long now) {
                if (gameOver) {
                    this.stop(); // Stop the animation timer
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


        c.setFocusTraversable(true);
        c.addEventFilter(KeyEvent.KEY_PRESSED, key -> {
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

        if (gameOver) {
            gc.setFill(Color.RED);
            gc.setFont(new Font("", 100));
            gc.fillText("LOSER!", 100, 250);
            return;
        }

        if (snake.size() == width * height) {
            gameOver = true;
            gc.setFill(Color.GOLD);
            gc.setFont(new Font("", 50));
            gc.fillText("WINNER!", 100, 250);
            return;
        }

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
    private static void render(GraphicsContext gc) {
        // Clear the canvas
        gc.clearRect(0, 0, width * cornersize, height * cornersize);

        // background
        String imageUrl = "https://img.freepik.com/vektoren-kostenlos/nahtloses-gruenes-grasmuster_1284-52275.jpg?size=626&ext=jpg";
        Image image = new Image(imageUrl, width * cornersize, height * cornersize, false, false);
        gc.drawImage(image,0,0);




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

    private static void handleGameOver() {
        // Update the high score in the database
        highScoreManager.addScore(currentPlayerUsername, score, "Standard");

        // Reset the score for the next game
        score = 0;

        // Show game over screen and ask if the player wants to play again
        showGameOverScreen();
    }

    public static void resetGame() {
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

    private static void showGameOverScreen() {
        Alert gameOverAlert = new Alert(Alert.AlertType.CONFIRMATION);
        gameOverAlert.setTitle("Game Over");
        gameOverAlert.setHeaderText(null);
        gameOverAlert.setContentText("Would you like to play again?");

        ButtonType buttonTypeYes = new ButtonType("Yes");
        ButtonType buttonTypeNo = new ButtonType("No", ButtonBar.ButtonData.CANCEL_CLOSE);

        gameOverAlert.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo);

        Optional<ButtonType> result = gameOverAlert.showAndWait();
        if (result.isPresent() && result.get() == buttonTypeYes) {
            resetGame();
            Scene gameScene = createGameScene(highScoreManager, currentPlayerUsername);
            mainStage.setScene(gameScene);
        } else {
            // Go back to the main menu
            mainStage.setScene(mainMenuScene);
        }
    }

}