package edu.scr;

// zaimportowane biblioteki potrzebne do działania programa

import java.io.IOException;
import java.util.Objects;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/** JavaFX App https://openjfx.io/openjfx-docs/ */
// Główna funkcja aplikacji odpowiedzialna za stworzenia okna(sceny) programu.
public class App extends Application {

  @Override
  public void start(Stage stage) throws IOException {
    Parent root =
        FXMLLoader.load(
            Objects.requireNonNull(getClass().getClassLoader().getResource("scene1.fxml")));
    Scene scene = new Scene(root);
    stage.setScene(scene);
    stage.show();
  }

  public static void main(String[] args) {
    launch();
  }
}
