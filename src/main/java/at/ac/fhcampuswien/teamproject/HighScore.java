package at.ac.fhcampuswien.teamproject;



public class HighScore implements Comparable<HighScore>{
    private final String username;
    private final int score;

    public HighScore(String username, int score) {
        this.username = username;
        this.score = score;
    }

    public String getUsername() {
        return username;
    }

    public int getScore() {
        return score;
    }

    @Override
    public int compareTo(HighScore other) {
        // Compare scores in descending order
        return Integer.compare(other.score, this.score);
    }

    @Override
    public String toString() {
        return username + ": " + score;
    }
}
