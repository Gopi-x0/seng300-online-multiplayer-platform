package ca.ucalgary.seng300.screens;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
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
        content.getChildren().add(buildLeaderboardSection());
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

    private VBox buildLeaderboardSection() {
        // leaderboard block under the game cards
        TableView<LeaderboardRow> table = buildLeaderboardTable();

        VBox section = new VBox(14, buildLeaderboardHeading(), table);
        section.setPadding(new Insets(8, 0, 0, 0));
        return section;
    }

    private TableView<LeaderboardRow> buildLeaderboardTable() {
        TableView<LeaderboardRow> table = new TableView<>();

        TableColumn<LeaderboardRow, Number> rankColumn = new TableColumn<>("RANK");
        rankColumn.setCellValueFactory(data -> data.getValue().rankProperty());

        TableColumn<LeaderboardRow, String> playerColumn = new TableColumn<>("PLAYER");
        playerColumn.setCellValueFactory(data -> data.getValue().playerProperty());

        TableColumn<LeaderboardRow, Number> pointsColumn = new TableColumn<>("POINTS");
        pointsColumn.setCellValueFactory(data -> data.getValue().pointsProperty());

        TableColumn<LeaderboardRow, Number> scoreColumn = new TableColumn<>("SCORE");
        scoreColumn.setCellValueFactory(data -> data.getValue().scoreProperty());

        TableColumn<LeaderboardRow, String> gameColumn = new TableColumn<>("GAME");
        gameColumn.setCellValueFactory(data -> data.getValue().gameProperty());

        styleLeaderboardColumn(rankColumn);
        styleLeaderboardColumn(playerColumn);
        styleLeaderboardColumn(pointsColumn);
        styleLeaderboardColumn(scoreColumn);
        styleLeaderboardColumn(gameColumn);

        table.getColumns().addAll(rankColumn, playerColumn, pointsColumn, scoreColumn, gameColumn);
        table.setItems(FXCollections.observableArrayList(
                new LeaderboardRow(1, "Bilal Ahmed", 2780, 47, "Tic Tac Toe"),
                new LeaderboardRow(2, "Shayan Sheikh", 2680, 42, "Connect 4"),
                new LeaderboardRow(3, "Muhammad Abad", 2510, 39, "Tic Tac Toe"),
                new LeaderboardRow(4, "npc", 2340, 35, "Connect 4"),
                new LeaderboardRow(5, "npc", 2190, 31, "Tic Tac Toe"),
                new LeaderboardRow(6, "npc", 2050, 28, "Connect 4"),
                new LeaderboardRow(7, "npc", 1830, 25, "Tic Tac Toe"),
                new LeaderboardRow(8, "haris Naveed", 1792, 23, "Connect 4")
        ));
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        table.setPrefHeight(260);
        styleLeaderboardTable(table);
        return table;
    }

    private HBox buildLeaderboardHeading() {
        // little heading row so the leaderboard doesnt just start plain
        Label icon = new Label("♛");
        icon.setTextFill(Color.web("#42d7ff"));
        icon.setFont(Font.font("Arial", FontWeight.BOLD, 14));

        Label heading = new Label("LEADERBOARD");
        heading.setTextFill(Color.WHITE);
        heading.setFont(Font.font("Arial", FontWeight.BOLD, 16));

        HBox headingRow = new HBox(8, icon, heading);
        headingRow.setAlignment(Pos.CENTER_LEFT);
        return headingRow;
    }

    private void styleLeaderboardTable(TableView<LeaderboardRow> table) {
        // keeping the table dark so it fits the rest of the dashboard better
        table.setStyle(
                "-fx-background-color: rgba(12, 22, 42, 0.95);" +
                "-fx-control-inner-background: rgba(12, 22, 42, 0.95);" +
                "-fx-table-cell-border-color: rgba(75, 105, 150, 0.18);" +
                "-fx-table-header-border-color: transparent;" +
                "-fx-background-radius: 14;" +
                "-fx-border-color: rgba(63, 97, 145, 0.45);" +
                "-fx-border-radius: 14;"
        );
    }

    private void styleLeaderboardColumn(TableColumn<LeaderboardRow, ?> column) {
        // tiny bit of cleanup for the header labels
        column.setSortable(false);
        column.setResizable(false);
        column.setStyle("-fx-alignment: CENTER-LEFT;");
    }

    private static class LeaderboardRow {
        private final SimpleIntegerProperty rank;
        private final SimpleStringProperty player;
        private final SimpleIntegerProperty points;
        private final SimpleIntegerProperty score;
        private final SimpleStringProperty game;

        private LeaderboardRow(int rank, String player, int points, int score, String game) {
            this.rank = new SimpleIntegerProperty(rank);
            this.player = new SimpleStringProperty(player);
            this.points = new SimpleIntegerProperty(points);
            this.score = new SimpleIntegerProperty(score);
            this.game = new SimpleStringProperty(game);
        }

        private SimpleIntegerProperty rankProperty() {
            return rank;
        }

        private SimpleStringProperty playerProperty() {
            return player;
        }

        private SimpleIntegerProperty pointsProperty() {
            return points;
        }

        private SimpleIntegerProperty scoreProperty() {
            return score;
        }

        private SimpleStringProperty gameProperty() {
            return game;
        }
    }
}
