package ProjectFiles;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class SearchApp extends Application {
  private Search model;
  private SearchView view;
  private Pane searchPane;

  public void start (Stage primaryStage) {
    model = new Search();
    searchPane = new Pane();

    view = new SearchView();
    searchPane.getChildren().add(view);

    // Functionality when user clicks the search button
    view.getSearchButton().setOnAction(new EventHandler<ActionEvent>() {
      public void handle(ActionEvent actionEvent) {
        handleSearch();
      }
    });

    // Window/Stage settings
    primaryStage.setTitle("Search");
    primaryStage.setResizable(false);
    primaryStage.setScene(new Scene(searchPane));
    primaryStage.show();
  }

  public static void main(String[] args) {
    launch(args);
  }

  public void handleSearch() {
    /*
    * Gets the search query from the TextField
    * Get the boost value (true/false) from the Radiobutton ToggleGroup
    * Call the search() method on the model with the search query and boost
    * Update the view
    * */
    String searchQuery = view.getSearchQuery().getText();
    boolean boost = (boolean) view.getBoostToggleGroup().getSelectedToggle().getUserData();

    if(searchQuery.length() > 0) {
      model.search(searchQuery, boost, 10);
      view.update(model);
    }
  }
}
