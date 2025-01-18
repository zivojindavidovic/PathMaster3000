package rs.playgroundmath.pathmaster3000;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;

import java.io.Serial;
import java.io.Serializable;
import java.util.Random;
import java.util.Set;

public class GameGrid implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private transient GridPane grid;
    private final int gridSize;
    private final String[][] buttonStates;
    private int startX, startY, endX, endY;

    public GameGrid(int gridSize) {
        this.gridSize = gridSize;
        this.buttonStates = new String[gridSize][gridSize];
        initializeGrid();
    }

    private void initializeGrid() {
        Random random = new Random();
        startX = random.nextInt(gridSize);
        startY = random.nextInt(gridSize);

        do {
            endX = random.nextInt(gridSize);
            endY = random.nextInt(gridSize);
        } while (startX == endX && startY == endY);

        grid = new GridPane();
        grid.setHgap(5);
        grid.setVgap(5);
        grid.setAlignment(Pos.CENTER);

        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                Button button = new Button();
                button.setPrefSize(50, 50);

                if (i == startX && j == startY) {
                    button.setText("Start");
                    button.setStyle("-fx-background-color: green; -fx-text-fill: white;");
                } else if (i == endX && j == endY) {
                    button.setText("End");
                    button.setStyle("-fx-background-color: red; -fx-text-fill: white;");
                } else {
                    button.setText(String.valueOf(random.nextInt(10)));
                }

                buttonStates[i][j] = button.getText();
                grid.add(button, j, i);
            }
        }
    }

    public GridPane getGrid() {
        if (grid == null) {
            initializeGrid();
        }
        return grid;
    }

    public void randomizeStartAndEnd() {
        initializeGrid();
    }

    public void changeGameColor(String color, Set<Button> visitedButtons) {
        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                Button button = (Button) grid.getChildren().get(i * gridSize + j);
                if (!visitedButtons.contains(button) && !"Start".equals(button.getText()) && !"End".equals(button.getText())) {
                    button.setStyle("-fx-background-color: " + color + ";");
                }
            }
        }
    }

    public int getStartX() {
        return startX;
    }

    public int getStartY() {
        return startY;
    }

    public int getEndX() {
        return endX;
    }

    public int getEndY() {
        return endY;
    }
}
