package edu.scr;
// zaimportowane biblioteki potrzebne do działania programa

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.Semaphore;
import java.util.stream.Collectors;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;

public class Scene implements Initializable {
  // Deklaracja obiektów umieszczonych w scenie aplikacji
  @FXML private TextArea writersTextArea;
  @FXML private ListView<String> writersListView;
  @FXML private ListView<Reader> readersListView;
  @FXML private ProgressBar writingProgressBar;

  private volatile Integer readersNumber;
  private Semaphore semaphore;
  ObservableList<String> writers;

  // Inicjalizacja głównej sceny aplikacji
  @Override
  public void initialize(URL url, ResourceBundle resourceBundle) {
    this.readersNumber = 0;
    this.readersListView.setCellFactory(readerListView -> new ReaderListViewCell());
    this.semaphore = new Semaphore(0);
    this.semaphore.release();
  }
  // opis reakcji na naciśnięcie przycisku dodania wpisu do bazy danych
  public void writersButtonClicked(MouseEvent mouseEvent) {
    // sprawdzenie czy są czytelnicy w czytelni i próba podniesienia semafora - czyli zabolkowania
    // tego kawałku kodu - może myć tylko jeden pisarz w czytelni
    if (this.readersNumber == 0 && this.semaphore.tryAcquire()) {
      // pobranie listy wpisanych danych w bazie
      this.writers = this.writersListView.getItems();
      // ustawienie wskaźnika progresu
      this.writingProgressBar.setProgress(1);
      // Utworzenie wątku odpowiedzialnego za pobranie wpisanych danych i umieszczenie go w bazie
      // a następnie dodanie go do listView (listy danych odczytanych)
      new Thread(
              () -> {
                while (this.writingProgressBar.getProgress() > 0) {
                  Platform.runLater(
                      () -> {
                        // modyfikacja paska progresu
                        this.writingProgressBar.setProgress(
                            this.writingProgressBar.getProgress() - 0.1);
                      });
                  try {
                    Thread.sleep(400);
                  } catch (InterruptedException e) {
                    e.printStackTrace();
                  }
                }
                // dodanie wyniku zapisu danych do listView
                Platform.runLater(
                    () -> {
                      writers.add(this.writersTextArea.getText());
                      this.writingProgressBar.setProgress(0);
                    });
                // ustawienie semafora na pozwalanie na dosep innym procesom
                semaphore.release();
              })
          .start();
    } else {
      // wyświetlenie informacji gdy czytelnia jest zajęta
      System.out.println("Czytelnia zajęta");
    }
  }

  // opis reakcji na naciśnięcie przycisku odczytu z bazy danych
  public void readersButtonClicked(MouseEvent mouseEvent) {
    // gdy nie ma pisarza w czytelni, czyli semafor pozwala na dostęp
    if (this.semaphore.availablePermits() > 0) {
      // zwiększenie ilości czytelników
      this.increaseReaders();
      // pobranie listy danych
      var readers = this.readersListView.getItems();
      // zapisanie pobranych danych w liście
      List<String> records = writers.stream().sequential().collect(Collectors.toList());
      // utworzenie nowego czytelnika
      Reader reader = new Reader(readers.size() + 1, records, this);
      // zapisanie informacji pobranbych przez czytelnika
      readers.add(reader);
      // zmniejszenie ilości czytelników
      this.decreaseReaders();
    } else {
      // wyświetlenie informacji gdy pisarz jest w czytelni
      System.out.println("Pisarz w czytelni");
    }
  }

  //funkcja odpowiedzialna za zmniejszenie ilości czytelników
  public void decreaseReaders() {
    synchronized (Scene.class) {
      this.readersNumber--;
    }
  }
  //funkcja odpowiedzialna za zwiększenie ilości czytelników

  public void increaseReaders() {
    synchronized (Scene.class) {
      this.readersNumber++;
    }
  }
}
