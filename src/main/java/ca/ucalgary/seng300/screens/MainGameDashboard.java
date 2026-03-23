package ca.ucalgary.seng300.screens;

import javafx.geometry.Insets;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class MainGameDashboard extends BorderPane {

    public MainGameDashboard() {
        // keeping the constructor simple and pushing the setup into one method
        initializeLayout();
    }

    private void initializeLayout() {
        // setting up the dashboard shell first so we can drop sections in one by one
        setPadding(new Insets(30));
        setStyle(
                "-fx-background-color: radial-gradient(center 25% 15%, radius 120%, #12263d 0%, #081526 38%, #040c17 100%);" +
                "-fx-background-insets: 0;"
        );

        VBox mainContent = buildMainContent();
        // center is where the whole dashboard stack will live
        setCenter(mainContent);
    }

    private VBox buildMainContent() {
        // this will hold the header cards and leaderboard later
        VBox content = new VBox(24);
        content.setPadding(new Insets(8));
        content.setFillWidth(true);

        Region topSpacer = new Region();
        VBox.setVgrow(topSpacer, Priority.NEVER);

        content.getChildren().add(topSpacer);
        return content;
    }
}
