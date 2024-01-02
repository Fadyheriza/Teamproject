package at.ac.fhcampuswien.teamproject;

import javafx.scene.Scene;
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
import java.util.Random;
import javafx.scene.image.Image;

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

            public void handle(long now) {
                if (lastTick == 0) {
                    lastTick = now;
                    tick(gc);
                    return;
                }

                if (now - lastTick > 1000000000 / speed) {
                    lastTick = now;
                    tick(gc);
                }
            }
        }.start();

        c.setFocusTraversable(true);
        c.addEventFilter(KeyEvent.KEY_PRESSED, key -> {
            if (key.getCode() == KeyCode.W) {
                direction = Dir.up;
            }
            if (key.getCode() == KeyCode.A) {
                direction = Dir.left;
            }
            if (key.getCode() == KeyCode.S) {
                direction = Dir.down;
            }
            if (key.getCode() == KeyCode.D) {
                direction = Dir.right;
            }
        });

        snake.clear();
        snake.add(new Corner(width / 2, height / 2));
        snake.add(new Corner(width / 2, height / 2));
        snake.add(new Corner(width / 2, height / 2));

        return new Scene(root, width * cornersize, height * cornersize);
    }


    public static void tick(GraphicsContext gc) {

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
        for (int i = snake.size() - 1; i >= 1; i--) {
            snake.get(i).x = snake.get(i - 1).x;
            snake.get(i).y = snake.get(i - 1).y;
        }

        switch (direction) {
            case up:
                snake.get(0).y--;
                break;
            case down:
                snake.get(0).y++;
                break;
            case left:
                snake.get(0).x--;
                break;
            case right:
                snake.get(0).x++;
                break;
        }

        Corner head = snake.get(0);
        for (int i = 1; i < snake.size(); i++) {
            if (head.x == snake.get(i).x && head.y == snake.get(i).y) {
                gameOver = true;
            }
            if (head.x < 0 || head.x >= width || head.y < 0 || head.y >= height) {
                gameOver = true;
            }

        }
        // snake eats apple
        if (appleX == snake.get(0).x && appleY == snake.get(0).y) {
            addNewSegment();
            newFood();
            score++;
        }

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

    public static void addNewSegment() {
        Corner lastSegment = snake.get(snake.size() - 1);
        snake.add(new Corner(lastSegment.x, lastSegment.y));
    }
}