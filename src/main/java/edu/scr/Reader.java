package edu.scr;
// zaimportowane biblioteki potrzebne do działania programa

import java.util.List;

// Klasa Reader wykorzystywana do przechowyania informacji czytelników.
public class Reader {

  private final Integer id;
  private final List<String> read;
  private final Scene scene;

  // Konstruktor klasy
  public Reader(Integer id, List<String> read, Scene scene) {
    this.read = read;
    this.id = id;
    this.scene = scene;
  }
  // Gettery- funkce zwracające informacje i Settery - funkcje ustawiające zmienne
  public Integer getId() {
    return id;
  }

  public List<String> getRead() {
    return read;
  }

  public Scene getScene1() {
    return scene;
  }
}
