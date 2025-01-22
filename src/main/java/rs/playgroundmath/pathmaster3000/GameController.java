package rs.playgroundmath.pathmaster3000;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.util.Duration;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class GameController implements Serializable {

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

    //Ovo je konstruktor game controller klase
    //Ova klasa će upravljati ovim stvarima
    public GameController(GameGrid gameGrid) {
        this.gameGrid = gameGrid;
        this.scoreLabel = new Label("Score: 0");
        this.visitedCells = new HashSet<>();
        resetGame();
        initializeTimer();
    }

    // Ovo je metoda koja resetuje stanje igre, resetovanje znači vratiti sve vrednosti na početnu vrednost
    public void resetGame() {
        //score, korati, vreme se vraćaju na 0
        //posećena polja sa čiste itd...zaustavlja se timer
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

    // Ova metoda inicira igru
    private void initializeGame() {
        GridPane gridPane = gameGrid.getGrid();

        //Kreira se grid odnosno mreža igre
        //I opet se koristi ona dvostruka petlja koja prolazi kroz sve dugmiće na ekranu
        for (int i = 0; i < gameGrid.getGrid().getRowCount(); i++) {
            for (int j = 0; j < gameGrid.getGrid().getColumnCount(); j++) {
                //uzima se dugme koje je trenutno u petlji
                //dakle pozicije dugmadi su 0,0   0,1   0,2   1,0   1,1   1,2   2,0   2,1   2,2 itd...
                Button button = (Button) gridPane.getChildren().get(i * gameGrid.getGrid().getColumnCount() + j);

                //Dugme se prilagodava da se može kliknut
                // i poziva se setOnAction akcija
                // do znači da svako dugme koje se klikne, nešto bi trebalo da se desi
                button.setOnAction(event -> handleMove(button));
            }
        }
    }


    //Ova metoda inicira timer
    //Koristi se Timeline klasa koja se instancira u obekat
    private void initializeTimer() {
        timer = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            timeElapsed++;
            updateStats();
        }));
        timer.setCycleCount(Animation.INDEFINITE);
        timer.play();
    }

    //Ova metoda postavlja trenutnu poziciju na kojoj je igrač
    //Ona nam je potrebna kako bi detektovali koji su sve ispravni mogući sledeći koraci
    public void setCurrentPosition(int x, int y) {
        this.currentX = x;
        this.currentY = y;
        //Ovde se dodaje trenutna pozicija u posećena polja tj putanju
        visitedCells.add(new int[]{x, y});
    }

    //radi akciju pomeranja putanje tj klika dugmića, poziva se u onom gore setOnAction()
    private void handleMove(Button button) {
        int newX = GridPane.getRowIndex(button);
        int newY = GridPane.getColumnIndex(button);

        //proverava da li je trenutna pozicija validna
        //ako nije, prikazuje se alert da je neispravan pokret
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

    //Ova metoda proverava da li je kliknut dugmić validan, odnosno, dugmić mora biti u nizu, ne može po dijagonali, ne može sa razmacima
    //kao parametre prosledjuju se koordinate novog dugmića - seti se, dugmić se u matrici predstavlja kao [0,0], [0,1], [0,2], [1,0], [1,1], [1,2], [2,0], [2,1], [2,2]
    /*
    Na primer imas 4x4 matricu
    [0,0] [0,1] [0,2] [0,3]
    [1,0] [1,1] [1,2] [1,3]
    [2,0] [2,1] [2,2] [2,3]
    [3,0] [3,1] [3,2] [3,3]

    Ako ti je trenutna pozicija [0,1] a kliknula si na dugme [2,2]
    logika ispod kaze (2 - 0) + (2 - 1) = 3, 3 nije jednako 1, dakle nije validan move

    kada bi izabrana dugme [0,2] bilo validno, bilo bi (0 - 0) + (2 - 1) = 1, jedan jeste jednako 1
     */
    private boolean isValidMove(int newX, int newY) {
        return Math.abs(newX - currentX) + Math.abs(newY - currentY) == 1 &&
                visitedCells.stream().noneMatch(cell -> cell[0] == newX && cell[1] == newY);
    }

    //Ova metoda se poziva kada se završi igra i računa rezultat
    //Opisano je u tekstu zadatka kako se računa rezultat
    private int calculateScore() {
        // Ovde ponovo imaš onaj ternarni operator
        // Prevod ove linije kora ti je: Ova metoda će vratiti:
        // a. ako su koraci veći od 0 vrati score / koraci
        // b. ako koraci nisu veći od 0 vrati 0
        return steps > 0 ? score / steps : 0;
    }

    //Ova metoda se poziva kada se završi igra i setuje statistike
    //void na početku ti znači da metoda ne praća nikakav rezultat nego samo setuje neku vrednost ili ovavlja neki posao
    void updateStats() {
        if (statsLabel != null) {
            statsLabel.setText(String.format("Statistics: Path Length: %d, Sum: %d, Score: %d, Time: %ds",
                    steps, score, calculateScore(), timeElapsed));
        }
    }

    //Ova metoda se poziva kada se završi igra i prikazuje modala
    //Odnosno alert
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    //Ova metoda služi za promenu boje putanje
    public void changePathColor(String color) {
        this.pathColor = color;

        GridPane gridPane = gameGrid.getGrid();

        //Ovde se koristi drugačiji tip petlje
        //takozvana foreach petlja
        //u prevodu na srpski jezik, za svaki element u visitedCells, odnosno za svaki kliknuti dugmić promeni mi boju
        for (int[] cell : visitedCells) {
            int x = cell[0];
            int y = cell[1];
            Button button = (Button) gridPane.getChildren()
                    .get(x * gridPane.getColumnCount() + y);
            button.setStyle("-fx-background-color: " + pathColor + ";");
        }
    }

    //getter metoda za scoreLabel
    public Label getScoreLabel() {
        return scoreLabel;
    }

    //setter metoda za scoreLabel
    public void setStatsLabel(Label statsLabel) {
        this.statsLabel = statsLabel;
    }

    //Ova metoda menja boju igre
    public void changeGameColor(String color) {
        gameGrid.changeGameColor(color, visitedCells);
    }

    //Metoda za ponovno inicijalizaciju igre
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