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
    // Konstante - FINAL polje označava da se vrednost ne može menjati
    private static final int DEFAULT_GRID_SIZE = 5;
    // Polje koje označava veličinu mape
    private int gridSize = DEFAULT_GRID_SIZE;
    // Polje koje če imati instanciranu vrednost GameGrid klase
    private GameGrid gameGrid;
    // Polje koje če imati instanciranu vrednost GameController klase
    private GameController gameController;
    // Polje koje če imati instanciranu vrednost BorderPane klase
    private BorderPane root;
    // Polje koje če imati instanciranu vrednost Label klase
    private Label statsLabel;
    // Random klasa se koristi za generisanje random vrednosti poput ranom brojeva (nextInt() itd)
    private final Random random = new Random();

    public static void main(String[] args) {
        launch(args);
    }

    // Metoda koja se poziva kada se pokrene aplikacija
    // Iscrtava se ekran i se inicijalizira igra
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


    //Metoda koja inicira igru
    private void initializeGame() {
        //Kreira se grid instanca, odnosno obekat klase GameGrid i prosledjuje se vrednost gridSize
        // odnosno veličina mape
        gameGrid = new GameGrid(gridSize);

        //Kreira se instanca klase GameController
        // Prosljeđuje se gameGrid odnosno mreža igre kojom treba upravljati
        gameController = new GameController(gameGrid);

        //game grid se prikazuje u centru ekrana
        root.setCenter(gameGrid.getGrid());

        //Kreira se panel za statistike koji je vertikalno orijentisan
        VBox bottomPanel = new VBox(10);
        bottomPanel.setAlignment(Pos.CENTER);

        //Kreira se label za statistike
        statsLabel = new Label("Statistics: Path Length: 0, Sum: 0, Score: 0, Time: 0s");
        gameController.setStatsLabel(statsLabel);

        //Dodaje se labela za statistike u panel
        Label scoreLabel = gameController.getScoreLabel();

        //UI elementu se prosledjuje vrednost statistika
        bottomPanel.getChildren().addAll(scoreLabel, statsLabel);

        //Prikazuje se panel na dnu ekrana
        //root zapravo predstavlja koren ekrana
        root.setBottom(bottomPanel);
    }

    //Ova medota kreira meni sa podešavanjima i opcijama
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

    // Metoda koja restartuje igru pozivanjem initializeGame metode
    private void restartGame() {
        initializeGame();
    }

    // Ova metoda menja veličinu mape
    private void changeGridSize() {
        //Ovo je ternarni operator koji sam ti pominjao na whatsapp-u
        //Ako je gridSize 5, postavi ga na 7, inace postavi ga na 5
        //Cela ova jedna linija koda iznad može se zameniti sa običnim if else uslovom kao npr:
        /*
            if (gridSize == 5) {
                gridSize = 7;
            } else {
                gridSize = 5;
            }

            Ove četiri linije koda su zamenjene ternarnim operatorom ? i : , u prevodu
            postavi gridSize na - ako je gridSize jednak broju 5 postavi ga na 7, inace postavi ga na 5
         */
        gridSize = gridSize == 5 ? 7 : 5;
        initializeGame();
    }

    // Metoda koja randomizuje start i end pozivanjem randomizeStartAndEnd metode а koristi se u
    // podešavanjima igre odnosno menuju
    private void randomizeStartAndEnd() {
        gameGrid.randomizeStartAndEnd();
        gameController.resetGame();

        root.setCenter(gameGrid.getGrid());
    }

    // Metoda koja randomizuje boju igre
    private void changeGameColor() {
        // Ovo je lista mogućih boja
        List<String> possibleGameBackgroundColors = List.of("#B39DDB", "#FFCC80", "#C5E1A5");
        // Ova varijabla koristi ona random property klase Random za odabir random broja
        int randomIndex = random.nextInt(possibleGameBackgroundColors.size());
        // randomIndex će imati na primer vrednost 1, i tom indeksu u nizu se pristupa i uzima se ta boja za pozadinu
        // Bitno je da znaš da u programiranju brojanje počinje od nule
        // Dakle, #B39DDB se čuva na indeksu 0, #FFCC80 na indeksu 1, #C5E1A5 na indeksu 2
        // Lista je veličine 3, ali trećem elementu se pristupa kao possibleGameBackgroundColors.get(2) ili possibleGameBackgroundColors[2]
        gameController.changeGameColor(possibleGameBackgroundColors.get(randomIndex));
    }

    //Isto važi kao i gore, samo ova metoda menja boju putanje
    private void changePathColor() {
        List<String> possiblePathColors = List.of("#F06292", "#FFF176", "#80DEEA");
        int randomIndex = random.nextInt(possiblePathColors.size());
        gameController.changePathColor(possiblePathColors.get(randomIndex));
    }

    // Ova metoda čuva stanje igre
    private void saveGame(Stage primaryStage) {
        //FileChooser je zapravo onaj modal koji otvara prozor za odabir datoteke u koju ćeš sačuvati stanje igre
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Game");
        //Igra se čuva sa .game ekstenzijom
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Game Files", "*.game"));
        File file = fileChooser.showSaveDialog(primaryStage);
        if (file != null) {
            //Ovo je obrada izuzetaka
            //Ako čuvanje igre ne uspe i program negde pukne, ulazi se u catch blok i hvata se izuzetak
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
                //Ako čuvanje igre uspe, objekti gameGrid i gameController se serijalizuju i čuvaju u fajlu
                oos.writeObject(gameGrid);
                oos.writeObject(gameController);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Ova metoda učitava igru koji si prethodno sačuvala
    // Takodje koristi isti onaj FileChooser
    private void loadGame(Stage primaryStage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Load Game");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Game Files", "*.game"));
        File file = fileChooser.showOpenDialog(primaryStage);
        if (file != null) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                // Čitaju se obekti gameGrid i gameController
                // Ovo što stoji ispred ois.readObject() (GameGrid) i (GameController) se zove kastovanje
                // Na primer, ako ovde dodje vrednost kao neki broj, npr 10, on nije instanca GameGrid klase
                // Kastovanje pokušava da pretvori taj broj u instancu GameGrid klase
                // S tim da moraš da znaš razliku izmedju primitivnih tipova i objektnih tipova
                // int i Integer nije isto, int je primitivni tip a Integer je obekat
                gameGrid = (GameGrid) ois.readObject();
                gameController = (GameController) ois.readObject();

                // Nakon što se učitaju objekti gameGrid i gameController, inicira se igra
                gameGrid.reinitializeGrid();
                gameController.reinitialize(gameGrid);

                initializeLoadedGame();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    //Ova metoda reinicira učitanu igru, boji prethodno selektovana polja itd..
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