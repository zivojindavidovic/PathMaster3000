package rs.playgroundmath.pathmaster3000;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;

import java.io.Serializable;
import java.util.Random;
import java.util.Set;

public class GameGrid implements Serializable {

    //Kreira GridPane odnosno mrežu sa poljima u igri
    private transient GridPane grid;
    //Definiše veličinu mreže - mreža je u kodu predstavljena kao dvodimenzionalni niz npr. 5x5 ili 7x7
    private final int gridSize;
    //Definiše stanje svih polja u igri
    private final String[][] buttonStates;
    //Definiše pocetak i kraj igre
    private int startX, startY, endX, endY;


    //Kreira konstruktor GameGrid klase
    //U konstruktoru se definiše veličina mreže i pocetak i kraj igre
    public GameGrid(int gridSize) {
        this.gridSize = gridSize;
        this.buttonStates = new String[gridSize][gridSize];
        initializeGrid();
    }

    //Inicira grid odnosno mrežu igre
    // Ako je igra prazna, generira se random grid sa dugmićima sa random vrednostima
    //poziva se createGrid() metoda koja iscrtava grid na ekran - ona se bavi iscrtavanjem dugmića
    private void initializeGrid() {
        if (isButtonStatesEmpty()) {
            generateRandomGrid();
        }
        createGrid();
    }

    //Proverava da li su sva polja u igri prazna
    //Ako su polja tj. dugmići prazni odnosno nisu popunjeni vraća true, u suprotnom vraća false
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

    //Generiše random grid sa dugmićima sa random vrednostima
    //Dakle, ova metoda ne generiše dugmiće i ne iscrtava ih na ekranu nego generiše
    // random vrednosti brojeva za te dugmiće
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

    //Ova metoda iscrtava grid na ekran
    //grid je zapravo layout koji će se prikazati na ekranu
    private void createGrid() {
        grid = new GridPane();
        grid.setHgap(5);
        grid.setVgap(5);
        grid.setAlignment(Pos.CENTER);

        // for predstavlja petlju (eng. loop) koja se koristi za iscrtavanje dugmića
        // Koristi se dvostruka petlja zato što se iscrtava matrica, odnosno, dvodimenzionalni niz koji
        // predstavlja mrežu ili mapu u igri
        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                Button button = new Button();
                button.setPrefSize(50, 50);

                //Ovde se proverava da li je dugmić Start ili End
                //Ako je state dugmića start ili end, ne iscrtava se broj nego se prikazuje Start ili End polja
                // U igri crveno i zeleno polje
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

    //Metoda koja vraća grid odnosno mrežu igre
    // Ovakve metode se obično nazivaju getter metodama - pošto je polje grid ove klase privatno
    // onda se onda metoda koja vraća grid odnosno mrežu igre naziva "getGrid"
    //Ovo se u obektno orijentiranom programiranju naziva enkapsulacija (da li je polje privatno ili javno / private ili public)
    public GridPane getGrid() {
        if (grid == null) {
            createGrid();
        }
        return grid;
    }

    //Ova metoda služi za funkcionalnost promene pozicije Start i End dugmića
    public void randomizeStartAndEnd() {
        generateRandomGrid();
        createGrid();
    }


    //Ova metoda menja pozadinsku boju dugmića u igri
    public void changeGameColor(String color, Set<int[]> visitedCells) {
        //Ponovo se koristi dvostruka petlja
        //Da bi se proverilo koji dugmići već nisu kliknuti (nisu u označenoj putanji)
        //Tako da budemo sigurni da menjamo samo boju dugmića koji nisu u označenoj putanji
        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                if (!isCellVisited(i, j, visitedCells) && !"Start".equals(buttonStates[i][j]) && !"End".equals(buttonStates[i][j])) {
                    Button button = (Button) grid.getChildren().get(i * gridSize + j);
                    button.setStyle("-fx-background-color: " + color + ";");
                }
            }
        }
    }

    //Ova metoda proverava da li je dugmić kliknut, odnosno, da li je deo putanje ili nije
    private boolean isCellVisited(int x, int y, Set<int[]> visitedCells) {
        return visitedCells.stream().anyMatch(cell -> cell[0] == x && cell[1] == y);
    }

    //Ispod su sve 4 getter metode za propertije ove GameGrid klase
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

    //Ova metoda se poziva kada se igra ponovo inicijalizira
    public void reinitializeGrid() {
        if (grid == null) {
            createGrid();
        }
    }
}
