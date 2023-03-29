package ProjectFiles;

import ProjectTesterFiles.SearchResult;
import javafx.collections.FXCollections;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;

import java.util.List;

public class SearchView extends Pane {
  private TextField searchQuery;
  private Button searchButton;
  private Label boostLabel;
  private ToggleGroup boostToggleGroup;
  private RadioButton yesBoostButton, noBoostButton;
  private ListView<SearchResult> resultsList;

  public SearchView() {
    // Create the search query TextField
    searchQuery = new TextField();
    searchQuery.relocate(10, 10);
    searchQuery.setPrefSize(425, 30);

    // Create the "Search" button
    searchButton = new Button("Search");
    searchButton.relocate(440, 10);
    searchButton.setPrefSize(100, 30);

    // Create Boost Radio Buttons
    boostLabel = new Label("PageRank Boost?");
    boostToggleGroup = new ToggleGroup();
    noBoostButton = new RadioButton("No");
    yesBoostButton = new RadioButton("Yes");

    yesBoostButton.setToggleGroup(boostToggleGroup);
    noBoostButton.setToggleGroup(boostToggleGroup);
    yesBoostButton.setUserData(true);
    noBoostButton.setUserData(false);
    yesBoostButton.setSelected(true);

    boostLabel.relocate(10, 45);
    noBoostButton.relocate(165, 45);
    yesBoostButton.relocate(115, 45);

    // Create and position search results ListView
    resultsList = new ListView<SearchResult>();
    resultsList.relocate(10, 75);
    resultsList.setPrefSize(530, 245);

    // Add all the components to the Pane
    getChildren().addAll(searchQuery, searchButton, resultsList, boostLabel, yesBoostButton, noBoostButton);

    // Set window size
    setPrefSize(550, 330);
  }

  public TextField getSearchQuery() {
    /*
    * Returns searchQuery TextField
    * */
    return searchQuery;
  }

  public Button getSearchButton() {
    /*
    * Returns searchButton Button
    * */
    return searchButton;
  }

  public ToggleGroup getBoostToggleGroup() {
    /*
    * Returns boost ToggleGroup which the radiobuttons are set to
    * */
    return boostToggleGroup;
  }

  public void update(Search model) {
    /*
    * Update results ViewList with search results
    * */

    List<SearchResult> searchResults = model.getSearchResults();

    resultsList.setItems(FXCollections.observableList(searchResults));
  }

}
