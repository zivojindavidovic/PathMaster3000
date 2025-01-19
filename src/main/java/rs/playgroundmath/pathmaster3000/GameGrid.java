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
        if (isButtonStatesEmpty()) {
            generateRandomGrid();
        }
        createGrid();
    }

    private boolean isButtonStatesEmpty() {
        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                if (buttonStates[i][j] != null) {
                    return false;
                }
            }
        }
        return true;
    }

    private void generateRandomGrid() {
        Random random = new Random();
        startX = random.nextInt(gridSize);
        startY = random.nextInt(gridSize);

        do {
            endX = random.nextInt(gridSize);
            endY = random.nextInt(gridSize);
        } while (startX == endX && startY == endY);

        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                if (i == startX && j == startY) {
                    buttonStates[i][j] = "Start";
                } else if (i == endX && j == endY) {
                    buttonStates[i][j] = "End";
                } else {
                    buttonStates[i][j] = String.valueOf(random.nextInt(10));
                }
            }
        }
    }

    private void createGrid() {
        grid = new GridPane();
        grid.setHgap(5);
        grid.setVgap(5);
        grid.setAlignment(Pos.CENTER);

        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                Button button = new Button();
                button.setPrefSize(50, 50);

                if ("Start".equals(buttonStates[i][j])) {
                    button.setText("Start");
                    button.setStyle("-fx-background-color: green; -fx-text-fill: white;");
                } else if ("End".equals(buttonStates[i][j])) {
                    button.setText("End");
                    button.setStyle("-fx-background-color: red; -fx-text-fill: white;");
                } else {
                    button.setText(buttonStates[i][j]);
                }

                grid.add(button, j, i);
            }
        }
    }

    public GridPane getGrid() {
        if (grid == null) {
            createGrid();
        }
        return grid;
    }

    public void randomizeStartAndEnd() {
        generateRandomGrid();
        createGrid();
    }

    public void changeGameColor(String color, Set<int[]> visitedCells) {
        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                if (!isCellVisited(i, j, visitedCells) && !"Start".equals(buttonStates[i][j]) && !"End".equals(buttonStates[i][j])) {
                    Button button = (Button) grid.getChildren().get(i * gridSize + j);
                    button.setStyle("-fx-background-color: " + color + ";");
                }
            }
        }
    }

    private boolean isCellVisited(int x, int y, Set<int[]> visitedCells) {
        return visitedCells.stream().anyMatch(cell -> cell[0] == x && cell[1] == y);
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

    public void reinitializeGrid() {
        if (grid == null) {
            createGrid();
        }
    }
}
