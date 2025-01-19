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
    private transient Label scoreLabel;
    private transient Label statsLabel;
    private int currentX, currentY;
    private int score;
    private int steps;
    private final Set<int[]> visitedCells;
    private String pathColor = "yellow";
    private transient Timeline timer;
    private int timeElapsed;

    public GameController(GameGrid gameGrid) {
        this.gameGrid = gameGrid;
        this.scoreLabel = new Label("Score: 0");
        this.visitedCells = new HashSet<>();
        resetGame();
        initializeTimer();
    }

    public void resetGame() {
        score = 0;
        steps = 0;
        timeElapsed = 0;
        visitedCells.clear();
        currentX = gameGrid.getStartX();
        currentY = gameGrid.getStartY();
        initializeGame();
        if (timer != null) {
            timer.stop();
            timer.playFromStart();
        }
    }

    private void initializeGame() {
        GridPane gridPane = gameGrid.getGrid();

        for (int i = 0; i < gameGrid.getGrid().getRowCount(); i++) {
            for (int j = 0; j < gameGrid.getGrid().getColumnCount(); j++) {
                Button button = (Button) gridPane.getChildren().get(i * gameGrid.getGrid().getColumnCount() + j);

                button.setOnAction(event -> handleMove(button));
            }
        }
    }


    private void initializeTimer() {
        timer = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            timeElapsed++;
            updateStats();
        }));
        timer.setCycleCount(Animation.INDEFINITE);
        timer.play();
    }

    public void setCurrentPosition(int x, int y) {
        this.currentX = x;
        this.currentY = y;
        visitedCells.add(new int[]{x, y});
    }


    private void handleMove(Button button) {
        int newX = GridPane.getRowIndex(button);
        int newY = GridPane.getColumnIndex(button);

        if (!isValidMove(newX, newY)) {
            showAlert("Invalid Move", "You can only move to adjacent fields!");
            return;
        }

        setCurrentPosition(newX, newY);
        button.setStyle("-fx-background-color: " + pathColor + ";");

        if ("End".equals(button.getText())) {
            if (timer != null) timer.stop();

            // U odnosu na proslu verziju
            // ovde sam zamenio da se timer zaustavi čim se završi igra
            // a ne tek nakom zatvaranja modala koji prikazuje finalni rezultat

            showAlert("Game Over", "Congratulations! Final score: " + calculateScore());
            return;
        }

        try {
            score += Integer.parseInt(button.getText());
        } catch (NumberFormatException ignored) {
        }

        steps++;
        scoreLabel.setText("Score: " + calculateScore());
        updateStats();
    }

    private boolean isValidMove(int newX, int newY) {
        return Math.abs(newX - currentX) + Math.abs(newY - currentY) == 1 &&
                visitedCells.stream().noneMatch(cell -> cell[0] == newX && cell[1] == newY);
    }

    private int calculateScore() {
        return steps > 0 ? score / steps : 0;
    }

    void updateStats() {
        if (statsLabel != null) {
            statsLabel.setText(String.format("Statistics: Path Length: %d, Sum: %d, Score: %d, Time: %ds",
                    steps, score, calculateScore(), timeElapsed));
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

        GridPane gridPane = gameGrid.getGrid();
        for (int[] cell : visitedCells) {
            int x = cell[0];
            int y = cell[1];
            Button button = (Button) gridPane.getChildren()
                    .get(x * gridPane.getColumnCount() + y);
            button.setStyle("-fx-background-color: " + pathColor + ";");
        }
    }

    public Label getScoreLabel() {
        return scoreLabel;
    }

    public void setStatsLabel(Label statsLabel) {
        this.statsLabel = statsLabel;
    }

    public void changeGameColor(String color) {
        gameGrid.changeGameColor(color, visitedCells);
    }

    public void reinitialize(GameGrid gameGrid) {
        this.scoreLabel = new Label("Score: " + calculateScore());
        this.statsLabel = new Label();
        setStatsLabel(this.statsLabel);

        GridPane gridPane = gameGrid.getGrid();

        for (int[] cell : visitedCells) {
            int x = cell[0];
            int y = cell[1];
            Button button = (Button) gridPane.getChildren()
                    .get(x * gameGrid.getGrid().getColumnCount() + y);
            button.setStyle("-fx-background-color: " + pathColor + ";");
        }

        initializeGame();
        updateStats();
        initializeTimer();
    }
}