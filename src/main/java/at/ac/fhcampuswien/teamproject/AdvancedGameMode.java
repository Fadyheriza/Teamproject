package at.ac.fhcampuswien.teamproject;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
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
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.animation.AnimationTimer;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javafx.scene.image.Image;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.LinkedList;
import java.util.Queue;

public class AdvancedGameMode {
    static int score = 0;
    static int speed = 5;
    static int width = 21;
    static int height = 17;
    static int appleX = 0;
    static int appleY = 0;
    static int cornersize = 24;
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
    private static final int PowerUps_GOLD_APPLE = 1;
    private static final int PowerUps_BLUE_APPLE = 2;
    private static final int PowerUps_CHOCOLATE_Apple = 3;

    // Variables to track apple types and their effects
    private static int currentAppleType = 0;
    private static Timeline speedBoostTimer;
    private static boolean isPoisoned = false;
    private static long poisonEndTime = 0;
    private static final String EAT_APPLE_SOUND = "eat_apple.mp3";
    private static final String SPEED_BOOST_SOUND = "speed_boost.mp3";
    private static final String GAME_OVER_SOUND = "games.mp3";
    private static MediaPlayer mediaPlayer;




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


    private static Scene currentGameScene;
    /**
     * Handles the pausing and resuming of the game.
     * When paused, displays a pause menu with options to continue or return to the main menu.
     * When resumed, updates the game loop and scene.
     */

    public static void handlePauseGame() {
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
            ;
            StackPane root = new StackPane();
            root.getChildren().add(canvas); // Add only canvas to the root
            currentGameScene = new Scene(root, canvas.getWidth(), canvas.getHeight()); // Update currentGameScene
            mainStage.setScene(currentGameScene); // Set the updated scene
            render(gc); // Explicitly call render to update the screen

        }
    }
    /**
     * Creates the layout for the pause menu, including labels and buttons.
     * @return VBox layout for the pause menu.
     */

    private static VBox createPauseMenuLayout() {
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
            isPaused = false; // Ensure the game is unpaused
            score = 0; // Reset the score
            mainStage.setScene(mainMenuScene); // Return to main menu without resetting
        });

        pauseMenuLayout.getChildren().addAll(pauseLabel, continueButton, mainMenuButton);

        return pauseMenuLayout;
    }


    private static AnimationTimer gameLoop;
    /**
     * Initializes the main game scene, sets up canvas, event handlers, and starts the game loop.
     * Also initializes snake, apple, and other game-related variables.
     * @param scoreManager The high score manager.
     * @param username The current player's username.
     * @return The initialized game scene.
     */
    public static Scene createGameScene(HighScoreManager scoreManager, String username) {
        highScoreManager = scoreManager;
        currentPlayerUsername = username;
        newFood();

        Pane root = new Pane();
        canvas = new Canvas(width * cornersize, height * cornersize);
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
                    if (now - lastTick > 1000000000 / speed) {
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
    /**
     * Handles the core game logic in each frame, including snake movement, collision detection, and rendering.
     * Updates the game state based on the elapsed time (deltaTime).
     * @param gc The graphics context for rendering.
     * @param deltaTime The elapsed time since the last frame.
     */
    public static void tick(GraphicsContext gc, double deltaTime) {
        if (isPaused || gameOver) {
            return;
        }

        if (!directionQueue.isEmpty()) {
            direction = directionQueue.poll();
        }

        // Ändern der Richtungslogik, wenn vergiftet
        if (isPoisoned) {
            switch (direction) {
                case up:
                    direction = Dir.down;
                    break;
                case down:
                    direction = Dir.up;
                    break;
                case left:
                    direction = Dir.right;
                    break;
                case right:
                    direction = Dir.left;
                    break;
            }
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
        if (newX < 0 || newX >= width || newY <  1 || newY >= height) {
            gameOver = true;
            isPoisoned = false; // Reset poison effect after game over
            return;
        }

        // Collision detection with walls
        if (newX < 0 || newX >= width || newY < 1 || newY >= height) {
            gameOver = true;
            return;
        }

        // Collision detection with itself
        for (int i = 1; i < snake.size(); i++) {
            Corner segment = snake.get(i);
            if (newX == segment.x && newY == segment.y) {
                if (!isPoisoned) {
                    // Only end the game if not poisoned
                    gameOver = true;
                }
                return;
            }
        }




        // moving body
        for (int i = snake.size() - 1; i > 0; i--) {
            snake.get(i).x = snake.get(i - 1).x;
            snake.get(i).y = snake.get(i - 1).y;
        }

        head.x = newX;
        head.y = newY;

        // Check if snake eats the apple
        if (appleX == head.x && appleY == head.y) {
            switch (currentAppleType) {
                case PowerUps_GOLD_APPLE:
                    System.out.println("GOLD_APPLE");
                    playSound(EAT_APPLE_SOUND);
                    score += 1; // Nur 1 Punkt für den goldenen Apfel
                    startSpeedslowMotionTimer();
                    break;
                case PowerUps_BLUE_APPLE:
                    System.out.println("BLUE_APPLE");
                    playSound(EAT_APPLE_SOUND);
                    score += 3; // 3 zusätzliche Punkte für den blauen Apfel
                    break;
                case PowerUps_CHOCOLATE_Apple:
                    System.out.println("CHOCOLATE_Apple");
                    playSound(EAT_APPLE_SOUND);
                    playSound(SPEED_BOOST_SOUND);
                    startPoisonEffect();
                    score += 1; // Nur 1 Punkt für den Schokoladenapfel
                    break;
                default:
                    System.out.println("RED_APPLE");
                    playSound(EAT_APPLE_SOUND);
                    score += 1; // 1 Punkt für den roten Apfel
                    break;
            }

            addNewSegment(); // Add the new segment after updating score and apple type
            newFood(); // Generate a new food after updating score
        }

        // Render everything
        render(gc);
    }


    /**
     * Starts a timer for a speed boost power-up, temporarily slowing down the snake.
     */
    private static void startSpeedslowMotionTimer() {
        int originalSpeed = speed; // Save the original speed

        speed = 1; // Set the low speed

        if (speedBoostTimer != null) {
            speedBoostTimer.stop();
        }

        speedBoostTimer = new Timeline(new KeyFrame(Duration.seconds(5), event -> {
            speed = originalSpeed;
            speedBoostTimer.stop();
        }));
        speedBoostTimer.play();
    }

    /**
     * Starts a poison effect power-up, temporarily reducing the snake's speed and inverting its controls.
     */
    private static void startPoisonEffect() {
        isPoisoned = true;
        int originalSpeed = speed;

        speed = Math.max(1, speed - 2); // Verringern der Geschwindigkeit

        if (speedBoostTimer != null) {
            speedBoostTimer.stop();
        }

        speedBoostTimer = new Timeline(new KeyFrame(Duration.seconds(5), event -> {
            isPoisoned = false; // Effekt beenden
            speed = originalSpeed; // Geschwindigkeit zurücksetzen

            if (!isPaused) {
                gameLoop.start();
            }
        }));



        speedBoostTimer.play();
    }

    /**
     * Renders the background of the game, clearing the canvas and drawing the specified background image.
     * The image is loaded from the given file path, and its size is determined by the canvas dimensions.
     * @param gc The graphics context for rendering.
     */

    private static void renderBackground(GraphicsContext gc) {
        // Clear the canvas
        gc.clearRect(0, 0, width * cornersize, height * cornersize);

        // background
        String imageUrl = "bg.png";
        Image image = new Image(imageUrl, width * cornersize, height * cornersize, false, false);
        gc.drawImage(image, 0, 0);
    }

    /**
     * Draws an apple on the canvas at the specified position with the given color.
     * The apple is represented as a filled oval shape.
     * @param gc The graphics context for rendering.
     * @param x The x-coordinate of the apple's position.
     * @param y The y-coordinate of the apple's position.
     * @param color The color of the apple.
     */
    private static void drawApple(GraphicsContext gc, double x, double y, Color color) {
        gc.setFill(color);
        gc.fillOval(x, y, cornersize, cornersize);
    }
    /**
     * Renders the game elements, including the background, snake, apple, and score.
     * @param gc The graphics context for rendering.
     */

    private static void render(GraphicsContext gc) {
        renderBackground(gc);

        // score
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 25));

        // Zeichnen des Texts mit einem schwarzen Rand
        String scoreText = "Score: " + score;
        gc.setStroke(Color.BLACK); // Farbe des Rands
        gc.setLineWidth(2); // Dicke des Rands
        gc.strokeText(scoreText, 10, 20); // Zeichnen des Rands

        // Zeichnen des Texts mit weißer Füllung
        gc.setFill(Color.WHITE); // Farbe der Füllung
        gc.fillText(scoreText, 10, 20);

        switch (currentAppleType) {
            case PowerUps_GOLD_APPLE:
                drawApple(gc, appleX * cornersize, appleY * cornersize, Color.GOLD);
                break;
            case PowerUps_BLUE_APPLE:
                drawApple(gc, appleX * cornersize, appleY * cornersize, Color.BLUE);
                break;
            case PowerUps_CHOCOLATE_Apple:
                drawApple(gc, appleX * cornersize, appleY * cornersize, Color.BROWN);
                break;
            default:
                drawApple(gc, appleX * cornersize, appleY * cornersize, Color.RED);
                break;
        }

        // snake
        for (Corner c : snake) {
            gc.setFill(Color.DARKGRAY);
            gc.fillRect(c.x * cornersize, c.y * cornersize, cornersize - 1, cornersize - 1);
            gc.setFill(Color.BLACK);
            gc.fillRect(c.x * cornersize, c.y * cornersize, cornersize - 2, cornersize - 2);
        }

    }

    /**
     * Generates a new food item (apple) and ensures it is placed in an unoccupied position on the canvas.
     * The position of the apple is randomized, avoiding collision with the snake's body.
     * Additionally, the type of the apple (red, gold, blue, chocolate) is determined based on the game's logic.
     */    public static void newFood() {
        while (true) {
            appleX = rand.nextInt(width);
            appleY = rand.nextInt(height - 1) + 1; // Verhindert, dass der Apfel in der ersten Zeile (y=0) erscheint

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
        // Ensure that the red apple (0) always appears
        if (score % 5 == 0) {
            currentAppleType = 0; // Red apple every 5 points
        } else {
            int randomAppleType = rand.nextInt(3) + 1; // 1 for GOLD_APPLE, 2 for BLUE_APPLE, 3 for YELLOW_APPLE
            currentAppleType = randomAppleType;
        }
    }
    /**
     * Adds a new segment to the snake, extending its length.
     */
    public static void addNewSegment() {
        Corner lastSegment = snake.get(snake.size() - 1);
        snake.add(new Corner(lastSegment.x, lastSegment.y));
    }
    /**
     * Draws the shaking effect for the snake on the canvas.
     * Randomly shifts each segment of the snake by -1, 0, or 1 pixels in both x and y directions.
     * @param gc The graphics context for rendering.
     */
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
        }
        ;
    }

    /**
     * Handles game over actions, stops the game loop, and displays the game over screen.
     */
    public static void handleGameOver() {
        gameOver = true;
        if (gameLoop != null) {
            gameLoop.stop();
        }

        // Create game over interface elements
        VBox gameOverLayout = new VBox(20);
        gameOverLayout.setAlignment(Pos.CENTER);
        gameOverLayout.setPadding(new Insets(20, 50, 20, 50));
        gameOverLayout.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, CornerRadii.EMPTY, Insets.EMPTY)));
        playSound(GAME_OVER_SOUND);

        // Display High Score and Username
        Label highScoreLabel = new Label("High Score: " + score);
        highScoreLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;-fx-background-color: white; -fx-text-fill: black; -fx-padding: 5;");

        Label usernameLabel = new Label("Username: " + currentPlayerUsername);
        usernameLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;-fx-background-color: white; -fx-text-fill: black; -fx-padding: 5;");

        highScoreManager.addScore(currentPlayerUsername, score, "Advanced");

        // Play Again Button
        Button playAgainButton = new Button("Play Again");
        playAgainButton.setOnAction(e -> {
            resetGame();
            Scene gameScene = createGameScene(highScoreManager, currentPlayerUsername);
            mainStage.setScene(gameScene);
        });

        // Back to Main Menu Button
        Button backButton = new Button("Back to Main Menu");
        backButton.setOnAction(e -> {
            resetGame();
            mainStage.setScene(mainMenuScene);
        });
        gameOverLayout.getChildren().addAll(highScoreLabel, usernameLabel, playAgainButton, backButton);

        // Overlay the game over layout on top of the game canvas
        StackPane root = new StackPane();
        root.getChildren().addAll(canvas, gameOverLayout); // canvas is your game canvas

        // Set the new scene, which is essentially an overlay over the existing canvas
        Scene gameOverScene = new Scene(root, canvas.getWidth(), canvas.getHeight());
        mainStage.setScene(gameOverScene);

        score = 0;

    }
    /**
     * Resets the game state to its initial conditions.
     */

    public static void resetGame() {
        speed = 5;
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
        score = 0;
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

    }

    /**
     * Plays a specified sound file for in-game events.
     * @param soundFileName The filename of the sound file.
     */
    private static void playSound(String soundFileName) {
        try {
            String soundFileUrl = AdvancedGameMode.class.getResource("/" + soundFileName).toExternalForm();
            Media sound = new Media(soundFileUrl);
            mediaPlayer = new MediaPlayer(sound);
            mediaPlayer.play();
        } catch (NullPointerException e) {
            System.err.println("Sound file not found: " + soundFileName);
        }
    }

    /**
     * Sets the main menu scene for navigation purposes.
     * @param scene The main menu scene.
     */
    public static void setMainMenuScene(Scene scene) {
        mainMenuScene = scene;
    }
    /**
     * Sets the main stage for navigation purposes.
     * @param stage The main stage.
     */
    public static void setMainStage(Stage stage) {
        mainStage = stage;
    }
}