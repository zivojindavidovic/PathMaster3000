module rs.playgroundmath.pathmaster3000 {
    requires javafx.controls;
    requires javafx.fxml;


    opens rs.playgroundmath.pathmaster3000 to javafx.fxml;
    exports rs.playgroundmath.pathmaster3000;
}