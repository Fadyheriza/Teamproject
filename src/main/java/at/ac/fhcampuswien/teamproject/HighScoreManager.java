package at.ac.fhcampuswien.teamproject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.io.*;
import java.util.*;
public class HighScoreManager {
    private final List<HighScore> highScores;
    private final int maxScores;

    public HighScoreManager(int maxScores) {
        this.highScores = new ArrayList<>();
        this.maxScores = maxScores;
    }

    public void addScore(String username, int score) {
        highScores.add(new HighScore(username, score));
        Collections.sort(highScores);

        if (highScores.size() > maxScores) {
            highScores.subList(maxScores, highScores.size()).clear();
        }
    }

    public List<HighScore> getHighScores() {
        return Collections.unmodifiableList(highScores);
    }
    public void saveHighScoresToFile(String filename) {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filename))) {
            out.writeObject(highScores);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void loadHighScoresFromFile(String filename) {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(filename))) {
            highScores.clear();
            highScores.addAll((List<HighScore>) in.readObject());
            Collections.sort(highScores);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
