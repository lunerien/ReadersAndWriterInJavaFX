package edu.scr;
// zaimportowane biblioteki potrzebne do działania programa

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.GridPane;
// Klasa definiująca jedno pole listView
public class ReaderListViewCell extends ListCell<Reader> {
  // Deklaracja obiektów umieszczonych w scenie aplikacji
  @FXML private GridPane pane;
  @FXML private Label readerNameLabel;
  @FXML private Label valueLabel;
  @FXML private ProgressBar progressBar;

  private FXMLLoader fxmlLoader;
  // Funkcja odpowiedzialna, za aktualizacje danych w listView
  @Override
  protected void updateItem(Reader reader, boolean empty) {

    super.updateItem(reader, empty);

    // ustawienie wyglądu graficznego pola listView
    if (empty || reader == null) {
      setText(null);
      setGraphic(null);
    } else {
      // zwiększenie ilość aktualnych czytelników
      reader.getScene1().increaseReaders();
      if (fxmlLoader == null) {
        fxmlLoader =
            new FXMLLoader(getClass().getClassLoader().getResource("readerListViewCell.fxml"));
        fxmlLoader.setController(this);
        try {
          fxmlLoader.load();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
      // Wyświetlenie nowego czytelnika w oknie aplikacji
      this.readerNameLabel.setText("Reader: " + reader.getId());

      // Stworzenie dla nowego czytelnika wątku tworzącego napis zawierający pobrane dane i
      // aktulaizujący pasek progresu, oraz dodanie wyniku programu do listy.
      List<String> list = reader.getRead();
      Task<String> getData =
          new Task<>() {
            @Override
            protected String call() throws Exception {
              StringBuilder result = new StringBuilder();
              for (int i = 0; i < list.size(); i++) {
                // aktulizacja paska progresu
                updateProgress(i, list.size());
                // dodanie pobranego pola z bazy danych do listy
                result.append(list.get(i)).append(" ");
                Thread.sleep(1000);
              }
              // obsługa elementów graficznych aplikacji
              Platform.runLater(
                  () -> {
                    progressBar.setVisible(false);
                    progressBar.setMaxWidth(0.);
                    valueLabel.setText("Test String");
                    // zmniejszenie ilośći aktualnych czytelników
                    reader.getScene1().decreaseReaders();
                  });
              return result.toString();
            }
          };
      this.progressBar.progressProperty().bind(getData.progressProperty());
      // wyświetlenie przeczytanych rekordów przez użytkownika
      getData.setOnSucceeded(
          event -> {
            try {
              this.valueLabel.setText(getData.get());

            } catch (InterruptedException | ExecutionException e) {
              e.printStackTrace();
            }
          });
      setText(null);
      setGraphic(pane);
      // uruchomienie procesu odpowiedzialnego za utworzenie napisu ia ktualzację interfejsu
      // graficznego
      Thread wait = new Thread(getData);
      wait.setDaemon(true);
      wait.start();
    }
  }
}
