package ca.ucalgary.seng300.screens;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

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

        content.getChildren().add(buildHeader());
        return content;
    }

    private HBox buildHeader() {
        // top row with the logo on the left and profile text on the right
        Label logoBlock = buildLogoBlock();
        Label profileLink = buildProfileLink();

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox header = new HBox(18, logoBlock, spacer, profileLink);
        header.setAlignment(Pos.TOP_LEFT);
        header.setPadding(new Insets(6, 4, 10, 4));
        return header;
    }

    private Label buildLogoBlock() {
        // text placeholder for the logo area till we swap in the real asset
        Label logo = new Label("SGP\nSTUDENT GAMING PLATFORM");
        logo.setTextFill(Color.web("#f1f7ff"));
        logo.setFont(Font.font("Arial", FontWeight.EXTRA_BOLD, 26));
        logo.setStyle(
                "-fx-background-color: linear-gradient(to bottom, rgba(34, 118, 204, 0.20), rgba(5, 12, 25, 0.10));" +
                "-fx-border-color: #25c3ff;" +
                "-fx-border-radius: 18;" +
                "-fx-background-radius: 18;" +
                "-fx-padding: 12 22 12 22;"
        );
        return logo;
    }

    private Label buildProfileLink() {
        Label profile = new Label("Profile");
        profile.setTextFill(Color.web("#3ed3ff"));
        profile.setFont(Font.font("Arial", FontWeight.NORMAL, 13));
        return profile;
    }
}
