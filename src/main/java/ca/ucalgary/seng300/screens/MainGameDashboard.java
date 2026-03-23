package ca.ucalgary.seng300.screens;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
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
        content.getChildren().add(buildGameCardSection());
        return content;
    }

    private HBox buildHeader() {
        // top row with the logo on the left and profile text on the right
        Label logoBlock = buildLogoBlock();
        Label profileLink = buildProfileLink();

        // this just pushes the profile text over to the right side
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // header row spacing is a bit loose rn so it feels more like the mockup
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
        // simple profile text for now not making it clickable yet
        Label profile = new Label("Profile");
        profile.setTextFill(Color.web("#3ed3ff"));
        profile.setFont(Font.font("Arial", FontWeight.NORMAL, 13));
        return profile;
    }

    private HBox buildGameCardSection() {
        // this row will hold the two game cards side by side
        VBox ticTacToeCard = createGameCard("Tic Tac Toe", "Take on the classic game of Tic Tac Toe!", "X O\nX O\nX O");
        VBox connectFourCard = createGameCard("Connect 4", "Outsmart your opponent in a game of Connect 4", "o o o o\no o o o\no o o o");

        HBox row = new HBox(18, ticTacToeCard, connectFourCard);
        HBox.setHgrow(ticTacToeCard, Priority.ALWAYS);
        HBox.setHgrow(connectFourCard, Priority.ALWAYS);
        return row;
    }

    private VBox createGameCard(String titleText, String subtitleText, String previewText) {
        // one reusable card so both game boxes stay styled the same
        Label preview = new Label(previewText);
        preview.setTextFill(Color.web("#38cfff"));
        preview.setFont(Font.font("Courier New", FontWeight.BOLD, 22));
        preview.setMinHeight(120);
        preview.setAlignment(Pos.CENTER);
        preview.setMaxWidth(Double.MAX_VALUE);
        preview.setStyle(
                "-fx-background-color: linear-gradient(to bottom, rgba(34, 54, 107, 0.55), rgba(9, 18, 38, 0.85));" +
                "-fx-background-radius: 14 14 0 0;" +
                "-fx-padding: 18;"
        );

        Label title = new Label(titleText);
        title.setTextFill(Color.WHITE);
        title.setFont(Font.font("Arial", FontWeight.BOLD, 15));

        Label subtitle = new Label(subtitleText);
        subtitle.setTextFill(Color.web("#89a4c6"));
        subtitle.setFont(Font.font("Arial", 11));

        Button playButton = createPlayButton();

        VBox card = new VBox(10, preview, title, subtitle, playButton);
        card.setPadding(new Insets(0, 0, 14, 0));
        card.setMaxWidth(Double.MAX_VALUE);
        card.setStyle(
                "-fx-background-color: linear-gradient(to bottom, rgba(34, 54, 107, 0.92), rgba(10, 20, 40, 0.96));" +
                "-fx-background-radius: 14;" +
                "-fx-border-color: rgba(72, 114, 170, 0.45);" +
                "-fx-border-radius: 14;"
        );
        return card;
    }

    private Button createPlayButton() {
        // leaving the button action empty till we hook up navigation later
        Button playButton = new Button("PLAY NOW");
        playButton.setMaxWidth(Double.MAX_VALUE);
        playButton.setOnAction(event -> {
        });
        playButton.setStyle(
                "-fx-background-color: transparent;" +
                "-fx-text-fill: #66dbff;" +
                "-fx-font-size: 11px;" +
                "-fx-font-weight: bold;" +
                "-fx-border-color: #2eaed8;" +
                "-fx-border-radius: 4;" +
                "-fx-background-radius: 4;" +
                "-fx-cursor: hand;"
        );
        return playButton;
    }
}
