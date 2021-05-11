
// moduły potrzebe do działania aplikacji.
module edu.scr {
    requires javafx.controls;
    requires javafx.graphics;
    requires javafx.fxml;
    exports edu.scr;
    opens edu.scr to javafx.fxml;
}