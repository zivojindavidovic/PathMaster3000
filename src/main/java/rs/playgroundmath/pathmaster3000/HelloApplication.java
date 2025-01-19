package rs.playgroundmath.pathmaster3000;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.util.List;
import java.util.Random;

public class HelloApplication extends Application {
    private static final int DEFAULT_GRID_SIZE = 5;
    private int gridSize = DEFAULT_GRID_SIZE;
    private GameGrid gameGrid;
    private GameController gameController;
    private BorderPane root;
    private Label statsLabel;
    private final Random random = new Random();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("PathMaster 3000");

        root = new BorderPane();
        initializeGame();

        MenuBar menuBar = createMenu(primaryStage);
        root.setTop(menuBar);

        Scene scene = new Scene(root, 500, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void initializeGame() {
        gameGrid = new GameGrid(gridSize);
        gameController = new GameController(gameGrid);

        root.setCenter(gameGrid.getGrid());

        VBox bottomPanel = new VBox(10);
        bottomPanel.setAlignment(Pos.CENTER);

        statsLabel = new Label("Statistics: Path Length: 0, Sum: 0, Score: 0, Time: 0s");
        gameController.setStatsLabel(statsLabel);

        Label scoreLabel = gameController.getScoreLabel();
        bottomPanel.getChildren().addAll(scoreLabel, statsLabel);

        root.setBottom(bottomPanel);
    }

    private MenuBar createMenu(Stage primaryStage) {
        MenuBar menuBar = new MenuBar();

        Menu gameMenu = new Menu("Game");

        MenuItem restartItem = new MenuItem("Restart Game");
        restartItem.setOnAction(e -> restartGame());

        MenuItem resizeItem = new MenuItem("Change Grid Size");
        resizeItem.setOnAction(e -> changeGridSize());

        MenuItem randomizeStartEndItem = new MenuItem("Randomize Start/End");
        randomizeStartEndItem.setOnAction(e -> randomizeStartAndEnd());

        MenuItem saveGameItem = new MenuItem("Save Game");
        saveGameItem.setOnAction(e -> saveGame(primaryStage));

        MenuItem loadGameItem = new MenuItem("Load Game");
        loadGameItem.setOnAction(e -> loadGame(primaryStage));

        gameMenu.getItems().addAll(restartItem, resizeItem, randomizeStartEndItem, saveGameItem, loadGameItem);

        Menu settingsMenu = new Menu("Settings");

        MenuItem changeGameColorItem = new MenuItem("Change Game Color");
        changeGameColorItem.setOnAction(e -> changeGameColor());

        MenuItem changePathColorItem = new MenuItem("Change Path Color");
        changePathColorItem.setOnAction(e -> changePathColor());

        settingsMenu.getItems().addAll(changeGameColorItem, changePathColorItem);

        menuBar.getMenus().addAll(gameMenu, settingsMenu);
        return menuBar;
    }

    private void restartGame() {
        initializeGame();
    }

    private void changeGridSize() {
        gridSize = gridSize == 5 ? 7 : 5;
        initializeGame();
    }

    private void randomizeStartAndEnd() {
        gameGrid.randomizeStartAndEnd();
        gameController.resetGame();

        root.setCenter(gameGrid.getGrid());
    }

    private void changeGameColor() {
        List<String> possibleGameBackgroundColors = List.of("#B39DDB", "#FFCC80", "#C5E1A5");
        int randomIndex = random.nextInt(possibleGameBackgroundColors.size());
        gameController.changeGameColor(possibleGameBackgroundColors.get(randomIndex));
    }

    private void changePathColor() {
        List<String> possiblePathColors = List.of("#F06292", "#FFF176", "#80DEEA");
        int randomIndex = random.nextInt(possiblePathColors.size());
        gameController.changePathColor(possiblePathColors.get(randomIndex));
    }

    private void saveGame(Stage primaryStage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Game");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Game Files", "*.game"));
        File file = fileChooser.showSaveDialog(primaryStage);
        if (file != null) {
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
                oos.writeObject(gameGrid);
                oos.writeObject(gameController);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void loadGame(Stage primaryStage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Load Game");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Game Files", "*.game"));
        File file = fileChooser.showOpenDialog(primaryStage);
        if (file != null) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                gameGrid = (GameGrid) ois.readObject();
                gameController = (GameController) ois.readObject();

                gameGrid.reinitializeGrid();
                gameController.reinitialize(gameGrid);

                initializeLoadedGame();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private void initializeLoadedGame() {
        root.setCenter(gameGrid.getGrid());

        VBox bottomPanel = new VBox(10);
        bottomPanel.setAlignment(Pos.CENTER);

        statsLabel = new Label();
        gameController.setStatsLabel(statsLabel);

        Label scoreLabel = gameController.getScoreLabel();
        bottomPanel.getChildren().addAll(scoreLabel, statsLabel);

        root.setBottom(bottomPanel);

        gameController.updateStats();
    }
}