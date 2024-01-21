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
import java.util.Random;
import javafx.scene.image.Image;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import java.util.LinkedList;
import java.util.Queue;

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
    static KeyCode lastKey = KeyCode.UNDEFINED;
    static Queue<Dir> directionQueue = new LinkedList<>();
    static boolean isPaused = false;

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
    private static Scene currentGameScene;

    public static void handlePauseGame() {
        // Check if the game is already over, and if so, do nothing
        if (gameOver) {
            return;
        }

        isPaused = !isPaused;
        if (isPaused) {
            // When pausing the game
            VBox pauseMenuLayout = createPauseMenuLayout();
            StackPane root = new StackPane();
            root.getChildren().addAll(canvas, pauseMenuLayout);
            Scene pauseScene = new Scene(root, canvas.getWidth(), canvas.getHeight());
            mainStage.setScene(pauseScene);

            // Stop the game loop
            if (gameLoop != null) {
                gameLoop.stop();
            }
        } else {
            // When resuming the game
            if (gameLoop != null) {
                gameLoop.start(); // Resume the game loop
            }
            StackPane root = new StackPane();
            root.getChildren().add(canvas); // Add only canvas to the root
            currentGameScene = new Scene(root, canvas.getWidth(), canvas.getHeight()); // Update currentGameScene
            mainStage.setScene(currentGameScene); // Set the updated scene
            render(gc); // Explicitly call render to update the screen
        }
    }



    public static VBox createPauseMenuLayout() {
        VBox pauseMenuLayout = new VBox(20);
        pauseMenuLayout.setAlignment(Pos.CENTER);
        pauseMenuLayout.setPadding(new Insets(20, 50, 20, 50));
        pauseMenuLayout.setBackground(new Background(new BackgroundFill(Color.rgb(0, 0, 0, 0.5), CornerRadii.EMPTY, Insets.EMPTY)));

        Label pauseLabel = new Label("Game Paused");
        pauseLabel.setFont(new Font("Arial", 24));
        pauseLabel.setTextFill(Color.WHITE);

        Button continueButton = new Button("Continue");
        continueButton.setFont(new Font("Arial", 18));
        continueButton.setOnAction(e -> handlePauseGame()); // Resume the game

        Button mainMenuButton = new Button("Back to Main Menu");
        mainMenuButton.setFont(new Font("Arial", 18));
        mainMenuButton.setOnAction(e -> {
            resetGame(); // Reset the game state before returning to the main menu
            mainStage.setScene(mainMenuScene);
        });

        pauseMenuLayout.getChildren().addAll(pauseLabel, continueButton, mainMenuButton);

        return pauseMenuLayout;
    }


    private static AnimationTimer gameLoop;

    public static Scene createGameScene(HighScoreManager scoreManager, String username) {
        resetGame(); // Reset the game state before starting a new game
        highScoreManager = scoreManager;
        currentPlayerUsername = username;
        newFood();

        Pane root = new Pane();
        canvas = new Canvas(width * cornersize, (height + 1) * cornersize); // +1 für Punkteanzeige
        gc = canvas.getGraphicsContext2D();
        root.getChildren().add(canvas);

        // Initialize the game loop only if it hasn't been already
        if (gameLoop == null) {
            gameLoop = new AnimationTimer() {
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
                    if (now - lastTick > (1000 / speed) * 1e6) {
                        lastTick = now;
                        tick(gc, (now - lastTick) / 1e9);
                    }
                }
            };
        }
        gameLoop.start();

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

        canvas.addEventFilter(KeyEvent.KEY_PRESSED, key -> {
            if (isOppositeDirection(key.getCode(), lastKey)) {
                // Ignore if the directions are opposite
                return;
            }

            if (key.getCode() == KeyCode.ESCAPE) {
                handlePauseGame();
            } else if (key.getCode() == KeyCode.W && direction != Dir.down) {
                direction = Dir.up;
            } else if (key.getCode() == KeyCode.A && direction != Dir.right) {
                direction = Dir.left;
            } else if (key.getCode() == KeyCode.S && direction != Dir.up) {
                direction = Dir.down;
            } else if (key.getCode() == KeyCode.D && direction != Dir.left) {
                direction = Dir.right;
            }

            lastKey = key.getCode(); // Update the last key pressed
        });

        canvas.setFocusTraversable(true);
        canvas.addEventFilter(KeyEvent.KEY_PRESSED, key -> {
            Dir newDirection = null;
            if (key.getCode() == KeyCode.W && direction != Dir.down) {
                newDirection = Dir.up;
            } else if (key.getCode() == KeyCode.A && direction != Dir.right) {
                newDirection = Dir.left;
            } else if (key.getCode() == KeyCode.S && direction != Dir.up) {
                newDirection = Dir.down;
            } else if (key.getCode() == KeyCode.D && direction != Dir.left) {
                newDirection = Dir.right;
            }
            if (newDirection != null) {
                directionQueue.add(newDirection);
            }
        });

        snake.clear();
        snake.add(new Corner(width / 2, height / 2));
        snake.add(new Corner(width / 2, height / 2));
        snake.add(new Corner(width / 2, height / 2));

        currentGameScene = new Scene(root, width * cornersize, height * cornersize);
        return currentGameScene;
    }

    private static boolean isOppositeDirection(KeyCode current, KeyCode last) {
        if (current == KeyCode.W && last == KeyCode.S) return true;
        if (current == KeyCode.S && last == KeyCode.W) return true;
        if (current == KeyCode.A && last == KeyCode.D) return true;
        if (current == KeyCode.D && last == KeyCode.A) return true;
        return false;
    }

    public static void tick(GraphicsContext gc, double deltaTime) {
        if (isPaused) {
            return; // Skip updating game logic if the game is paused
        }
        if (!directionQueue.isEmpty()) {
            direction = directionQueue.poll();
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


        // Überprüfen, ob die Schlange den Rand des Bildschirms erreicht hat
        if (newX < 0 || newX >= width || newY < 1 || newY >= height) { // Start checking from y = 1
            gameOver = true;
            return;
        }

        // Collision detection mit sich selbst
        for (int i = 1; i < snake.size(); i++) {
            Corner segment = snake.get(i);
            if (newX == segment.x && newY == segment.y) {
                gameOver = true;
                return;
            }
        }

        // Bewegung des Körpers
        for (int i = snake.size() - 1; i > 0; i--) {
            snake.get(i).x = snake.get(i - 1).x;
            snake.get(i).y = snake.get(i - 1).y;
        }

        head.x = newX;
        head.y = newY;

        // Überprüfen, ob die Schlange den Apfel isst
        if (appleX == head.x && appleY == head.y) {
            addNewSegment();
            newFood();
            score++;
        }

        // Alles rendern
        render(gc);
    }

    private static void renderBackground(GraphicsContext gc) {
        // Clear the canvas
        gc.clearRect(0, 0, width * cornersize, height * cornersize);

        // background
        String imageUrl = "bg.png";
        Image image = new Image("bg.png", width * cornersize, height * cornersize, false, false);
        gc.drawImage(image, 0, 0);
    }

    private static void render(GraphicsContext gc) {
        renderBackground(gc);

        // Setzen Sie die Schriftart und -größe
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 25));

        // Zeichnen des Texts mit einem schwarzen Rand
        String scoreText = "Score: " + score;
        gc.setStroke(Color.BLACK); // Farbe des Rands
        gc.setLineWidth(3); // Dicke des Rands
        gc.strokeText(scoreText, 10, 25); // Zeichnen des Rands

        // Zeichnen des Texts mit weißer Füllung
        gc.setFill(Color.WHITE); // Farbe der Füllung
        gc.fillText(scoreText, 10, 25); // Zeichnen der Füllung

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

            // Überprüfen, ob der Apfel in der ersten Zeile ist, und wenn ja, erneut generieren
            if (!isOccupied && appleY != 0) {
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
        gameOver = true;
        if (gameLoop != null) {
            gameLoop.stop();
        }
        String gameMode = "Standard" ;
        highScoreManager.addScore(currentPlayerUsername, score, gameMode);
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
        if (gameLoop != null) {
            gameLoop.stop();
        }
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
        lastKey = KeyCode.UNDEFINED; // Reset the last key pressed
        directionQueue.clear(); // Clear the direction queue
    }


    public static void setMainMenuScene(Scene scene) {
        mainMenuScene = scene;
    }

}