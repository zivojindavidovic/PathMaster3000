package rs.playgroundmath.pathmaster3000;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.util.Duration;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class GameController implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private final GameGrid gameGrid;
    private final transient Label scoreLabel;
    private transient Label statsLabel;
    private int currentX, currentY;
    private int score;
    private int steps;
    private final Set<Button> visitedButtons;
    private String pathColor = "yellow";
    private transient Timeline timer;
    private int timeElapsed;

    public GameController(GameGrid gameGrid) {
        this.gameGrid = gameGrid;
        this.scoreLabel = new Label("Score: 0");
        this.visitedButtons = new HashSet<>();
        resetGame();
        initializeTimer();
    }

    public void resetGame() {
        score = 0;
        steps = 0;
        timeElapsed = 0;
        visitedButtons.clear();
        currentX = gameGrid.getStartX();
        currentY = gameGrid.getStartY();
        initializeGame();
        if (timer != null) {
            timer.stop();
            timer.playFromStart();
        }
    }

    private void initializeGame() {
        for (int i = 0; i < gameGrid.getGrid().getRowCount(); i++) {
            for (int j = 0; j < gameGrid.getGrid().getColumnCount(); j++) {
                Button button = (Button) gameGrid.getGrid().getChildren().get(i * gameGrid.getGrid().getColumnCount() + j);
                button.setOnAction(event -> handleMove(button));
            }
        }

        Button startButton = (Button) gameGrid.getGrid().getChildren().get(currentX * gameGrid.getGrid().getColumnCount() + currentY);
        visitedButtons.add(startButton);
    }

    private void initializeTimer() {
        timer = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            timeElapsed++;
            updateStats();
        }));
        timer.setCycleCount(Animation.INDEFINITE);
        timer.play();
    }

    private void handleMove(Button button) {
        if (visitedButtons.contains(button)) {
            showAlert("Invalid Move", "This field has already been visited!");
            return;
        }

        int newX = GridPane.getRowIndex(button);
        int newY = GridPane.getColumnIndex(button);

        if (Math.abs(newX - currentX) + Math.abs(newY - currentY) != 1) {
            showAlert("Invalid Move", "You can only move to adjacent fields!");
            return;
        }

        currentX = newX;
        currentY = newY;

        button.setStyle("-fx-background-color: " + pathColor + ";");
        visitedButtons.add(button);

        if ("End".equals(button.getText())) {
            showAlert("Game Over", "Congratulations! Final score: " + calculateScore());
            if (timer != null) timer.stop();
            return;
        }

        try {
            score += Integer.parseInt(button.getText());
        } catch (NumberFormatException e) {
        }

        steps++;
        scoreLabel.setText("Score: " + calculateScore());
        updateStats();
    }

    private int calculateScore() {
        return steps > 0 ? score / steps : 0;
    }

    private void updateStats() {
        if (statsLabel != null) {
            statsLabel.setText(String.format("Statistics: Path Length: %d, Sum: %d, Score: %d, Time: %ds", steps, score, calculateScore(), timeElapsed));
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void changePathColor(String color) {
        this.pathColor = color;
    }

    public Label getScoreLabel() {
        return scoreLabel;
    }

    public void setStatsLabel(Label statsLabel) {
        this.statsLabel = statsLabel;
    }

    public void changeGameColor(String color) {
        gameGrid.changeGameColor(color, visitedButtons);
    }
}