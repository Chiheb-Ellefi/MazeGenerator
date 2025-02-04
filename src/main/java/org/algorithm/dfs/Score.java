package org.algorithm.dfs;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.util.Duration;
import org.algorithm.components.Node;

import java.util.HashSet;
import java.util.Set;
import java.util.ArrayList;
import java.util.List;

public class Score {
    private int score;
    private Set<String> foundWords;
    private Timeline timer;
    private int secondsRemaining;
    private static final int GAME_DURATION_SECONDS = 60;
    private Runnable gameEndCallback;

    public Score() {
        this.score = 0;
        this.foundWords = new HashSet<>();
    }

    public void setGameEndCallback(Runnable callback) {
        this.gameEndCallback = callback;
    }

    public int getScore() {
        return score;
    }

    public List<Node> updateScore(List<Node> currentPathNodes, MazeGenerator mazeGenerator) {
        Set<String> newWords = new HashSet<>();
        List<Node> nodesToReplace = new ArrayList<>();
        int pathLength = currentPathNodes.size();

        for (int start = 0; start < pathLength; start++) {
            StringBuilder wordBuilder = new StringBuilder();
            for (int end = start; end < pathLength; end++) {
                wordBuilder.append(currentPathNodes.get(end).getValue());
                String word = wordBuilder.toString().toLowerCase();

                if (mazeGenerator.containsWord(word) && !foundWords.contains(word)) {
                    newWords.add(word);

                    for (int i = start; i <= end; i++) {
                        nodesToReplace.add(currentPathNodes.get(i));
                    }
                }
            }
        }

        // Ajout des logs pour les nouveaux mots trouvés
        for (String word : newWords) {
            int points = word.length();
            score += points;
            foundWords.add(word);
            System.out.println("Mot trouvé: \"" + word + "\" (" + points + " points)");
        }

        // Log du total des points
        if (!newWords.isEmpty()) {
            System.out.println("Total gagné ce tour: " + newWords.stream().mapToInt(String::length).sum() + " points");
        }

        return nodesToReplace;
    }

    public void startTimer(Label timeLabel, Runnable onTimeOver) {
        stopTimer();
        secondsRemaining = GAME_DURATION_SECONDS;
        updateTimeLabel(timeLabel);

        timer = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            secondsRemaining--;
            updateTimeLabel(timeLabel);

            if (secondsRemaining <= 0) {
                timer.stop();
                Platform.runLater(onTimeOver);
            }
        }));
        timer.setCycleCount(Timeline.INDEFINITE);
        timer.play();
    }

    private void updateTimeLabel(Label timeLabel) {
        timeLabel.setText(String.format("Time: %02d", secondsRemaining));
    }

    public void gameOver(int moveCount, int shortestPathLength) {
        stopTimer();

        int wordScore = Math.min(score, 50);
        int efficiency = (int) (50 * ((double) shortestPathLength / Math.max(moveCount, 1)));
        int finalScore = Math.min(wordScore + efficiency, 100);

        String message = "Temps écoulé !\n\n" +
                "Score mots (50%) : " + wordScore + "\n" +
                "Score efficacité (50%) : " + efficiency + "\n" +
                "Score final : " + finalScore + "/100";

        showAlertAndReset("Fin du jeu", message);
    }

    public void victory() {
        stopTimer();
        score = 100; // Score maximal
        showAlertAndReset("Victoire !", "Score parfait : 100/100");
    }

    private void showAlertAndReset(String title, String content) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(content);
            alert.getDialogPane().setStyle("-fx-background-color: #4CAF50;");
            alert.showAndWait();
            if (gameEndCallback != null) {
                gameEndCallback.run();
            }
        });
    }

    private void stopTimer() {
        if (timer != null) {
            timer.stop();
        }
    }

    public void reset() {
        stopTimer();
        this.score = 0;
        this.foundWords.clear();
        this.secondsRemaining = GAME_DURATION_SECONDS;
    }
}