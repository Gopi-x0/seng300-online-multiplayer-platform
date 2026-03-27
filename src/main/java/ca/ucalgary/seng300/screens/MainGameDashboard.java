package ca.ucalgary.seng300.screens;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
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
        setPadding(new Insets(28));
        setStyle(
                "-fx-background-color: linear-gradient(to bottom, #0c1d31, #081221 60%, #050c17 100%);" +
                "-fx-background-insets: 0;"
        );

        VBox mainContent = buildMainContent();
        // center is where the whole dashboard stack will live
        setCenter(mainContent);
    }

    private VBox buildMainContent() {
        // this will hold the header cards and leaderboard later
        VBox content = new VBox(26);
        content.setPadding(new Insets(14, 22, 18, 22));
        content.setFillWidth(true);
        content.setMaxWidth(1120);
        content.setAlignment(Pos.TOP_CENTER);
        content.setStyle(
                "-fx-background-color: linear-gradient(to bottom, rgba(7, 18, 34, 0.97), rgba(4, 12, 23, 0.98));" +
                "-fx-background-radius: 6;"
        );

        content.getChildren().add(buildHeader());
        content.getChildren().add(buildGameCardSection());
        content.getChildren().add(buildLeaderboardSection());
        return content;
    }

    private HBox buildHeader() {
        // top row with the logo on the left and profile text on the right
        ImageView logoBlock = buildLogoBlock();
        Label profileLink = buildProfileLink();

        // this just pushes the profile text over to the right side
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // header row spacing is a bit loose rn so it feels more like the mockup
        HBox header = new HBox(18, logoBlock, spacer, profileLink);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(2, 4, 8, 4));
        return header;
    }

    private ImageView buildLogoBlock() {
        // using the real logo here too so the dashboard feels closer to the mockup
        Image image = new Image(getClass().getResourceAsStream("/ca/ucalgary/seng300/images/sgp-logo.png"));
        ImageView logo = new ImageView(image);
        logo.setPreserveRatio(true);
        logo.setFitWidth(210);
        logo.setSmooth(true);
        return logo;
    }

    private Label buildProfileLink() {
        // simple profile text for now not making it clickable yet
        Label profile = new Label("Profile");
        profile.setTextFill(Color.web("#3ed3ff"));
        profile.setFont(Font.font("Arial", FontWeight.NORMAL, 12));
        profile.setStyle(
                "-fx-background-color: rgba(10, 32, 54, 0.45);" +
                "-fx-background-radius: 12;" +
                "-fx-padding: 4 10 4 10;"
        );
        return profile;
    }

    private HBox buildGameCardSection() {
        // this row will hold the two game cards side by side
        VBox ticTacToeCard = createGameCard("Tic Tac Toe", "Take on the classic game of Tic Tac Toe!", buildTicTacToePreview());
        VBox connectFourCard = createGameCard("Connect 4", "Outsmart your opponent in a game of Connect 4", buildConnectFourPreview());

        ticTacToeCard.setPrefWidth(355);
        connectFourCard.setPrefWidth(355);

        HBox row = new HBox(18, ticTacToeCard, connectFourCard);
        HBox.setHgrow(ticTacToeCard, Priority.ALWAYS);
        HBox.setHgrow(connectFourCard, Priority.ALWAYS);
        row.setMaxWidth(740);
        row.setAlignment(Pos.CENTER);
        return row;
    }

    private VBox createGameCard(String titleText, String subtitleText, Region preview) {
        // one reusable card so both game boxes stay styled the same
        preview.setMinHeight(120);
        preview.setMaxWidth(Double.MAX_VALUE);
        preview.setStyle(
                "-fx-background-color: linear-gradient(to bottom, rgba(31, 50, 102, 0.72), rgba(11, 20, 40, 0.96));" +
                "-fx-background-radius: 14 14 0 0;" +
                "-fx-padding: 22 18 18 18;"
        );

        Button playButton = createPlayButton();
        VBox textBlock = buildGameTextBlock(titleText, subtitleText);

        VBox card = new VBox(10, preview, textBlock, playButton);
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

    private Region buildTicTacToePreview() {
        // quick mini tic tac toe board so the card looks more like the design
        GridPane board = new GridPane();
        board.setHgap(7);
        board.setVgap(7);
        board.setAlignment(Pos.CENTER);

        String[][] marks = {
                {"X", "O", ""},
                {"X", "", ""},
                {"X", "O", ""}
        };

        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 3; column++) {
                StackPane cell = new StackPane();
                cell.setPrefSize(28, 28);
                cell.setStyle(
                        "-fx-background-color: rgba(9, 25, 48, 0.98);" +
                        "-fx-border-color: #39cfff;" +
                        "-fx-border-radius: 4;" +
                        "-fx-background-radius: 4;"
                );

                Label mark = new Label(marks[row][column]);
                mark.setTextFill(Color.web("#38cfff"));
                mark.setFont(Font.font("Arial", FontWeight.BOLD, 14));
                cell.getChildren().add(mark);
                board.add(cell, column, row);
            }
        }

        return board;
    }

    private Region buildConnectFourPreview() {
        // same idea here but with circles so it feels closer to connect 4
        GridPane board = new GridPane();
        board.setHgap(6);
        board.setVgap(6);
        board.setAlignment(Pos.CENTER);
        Color[][] tokens = {
                {Color.web("#ff4b4b"), Color.web("#ffd93d"), Color.TRANSPARENT, Color.web("#4ea1ff"), Color.TRANSPARENT, Color.web("#ff4b4b"), Color.web("#ffd93d")},
                {Color.web("#4ea1ff"), Color.TRANSPARENT, Color.web("#ff4b4b"), Color.web("#ffd93d"), Color.web("#4ea1ff"), Color.TRANSPARENT, Color.web("#ff4b4b")},
                {Color.TRANSPARENT, Color.web("#ff4b4b"), Color.web("#ffd93d"), Color.TRANSPARENT, Color.web("#ff4b4b"), Color.web("#ffd93d"), Color.TRANSPARENT},
                {Color.web("#ffd93d"), Color.web("#4ea1ff"), Color.TRANSPARENT, Color.web("#ff4b4b"), Color.TRANSPARENT, Color.web("#4ea1ff"), Color.web("#ffd93d")},
                {Color.TRANSPARENT, Color.web("#ffd93d"), Color.web("#4ea1ff"), Color.TRANSPARENT, Color.web("#ffd93d"), Color.web("#ff4b4b"), Color.TRANSPARENT},
                {Color.web("#ff4b4b"), Color.TRANSPARENT, Color.web("#ffd93d"), Color.web("#4ea1ff"), Color.TRANSPARENT, Color.web("#ffd93d"), Color.web("#4ea1ff")}
        };

        for (int row = 0; row < tokens.length; row++) {
            for (int column = 0; column < tokens[row].length; column++) {
                Circle token = new Circle(7);
                token.setFill(tokens[row][column]);
                token.setStroke(Color.web("#31c6ff"));
                token.setStrokeWidth(1);
                board.add(token, column, row);
            }
        }

        return board;
    }

    private Button createPlayButton() {
        // leaving the button action empty till we hook up navigation later
        Button playButton = new Button("PLAY NOW");
        playButton.setMaxWidth(Double.MAX_VALUE);
        playButton.setOnAction(event -> {
        });
        stylePlayButton(playButton);
        return playButton;
    }

    private VBox buildLeaderboardSection() {
        // leaderboard block under the game cards
        TableView<LeaderboardRow> table = buildLeaderboardTable();

        VBox section = new VBox(14, buildLeaderboardHeading(), table);
        section.setPadding(new Insets(10, 0, 0, 0));
        section.setMaxWidth(980);
        section.setAlignment(Pos.CENTER_LEFT);
        section.setStyle(
                "-fx-background-color: linear-gradient(to bottom, rgba(9, 19, 36, 0.88), rgba(7, 15, 28, 0.96));" +
                "-fx-background-radius: 12;" +
                "-fx-border-color: rgba(53, 84, 127, 0.38);" +
                "-fx-border-radius: 12;" +
                "-fx-padding: 12 12 14 12;"
        );
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
                new LeaderboardRow(3, "Muhammad Ahmad", 2510, 39, "Tic Tac Toe"),
                new LeaderboardRow(4, "npc", 2340, 35, "Connect 4"),
                new LeaderboardRow(5, "npc", 2190, 31, "Tic Tac Toe"),
                new LeaderboardRow(6, "npc", 2050, 28, "Connect 4"),
                new LeaderboardRow(7, "npc", 1830, 25, "Tic Tac Toe"),
                new LeaderboardRow(8, "haris Naveed", 1792, 23, "Connect 4")
        ));
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        table.setMaxWidth(Double.MAX_VALUE);
        table.setPrefHeight(248);
        styleLeaderboardTable(table);
        return table;
    }

    private VBox buildGameTextBlock(String titleText, String subtitleText) {
        // keeping the card text grouped so spacing is easier to tweak
        Label title = new Label(titleText);
        title.setTextFill(Color.WHITE);
        title.setFont(Font.font("Arial", FontWeight.BOLD, 14));

        Label subtitle = new Label(subtitleText);
        subtitle.setWrapText(true);
        subtitle.setTextFill(Color.web("#89a4c6"));
        subtitle.setFont(Font.font("Arial", 10.5));

        VBox textBlock = new VBox(5, title, subtitle);
        textBlock.setPadding(new Insets(0, 12, 0, 12));
        return textBlock;
    }

    private void stylePlayButton(Button playButton) {
        // button is meant to feel like a thin outlined action bar
        playButton.setStyle(
                "-fx-background-color: transparent;" +
                "-fx-text-fill: #66dbff;" +
                "-fx-font-size: 10.5px;" +
                "-fx-font-weight: bold;" +
                "-fx-border-color: #2eaed8;" +
                "-fx-border-width: 1.1;" +
                "-fx-border-radius: 3;" +
                "-fx-background-radius: 3;" +
                "-fx-padding: 7 0 7 0;" +
                "-fx-cursor: hand;"
        );
    }

    private HBox buildLeaderboardHeading() {
        // little heading row so the leaderboard doesnt just start plain
        Label icon = new Label("♛");
        icon.setTextFill(Color.web("#42d7ff"));
        icon.setFont(Font.font("Arial", FontWeight.BOLD, 13));

        Label heading = new Label("LEADERBOARD");
        heading.setTextFill(Color.WHITE);
        heading.setFont(Font.font("Arial", FontWeight.BOLD, 15));

        HBox headingRow = new HBox(8, icon, heading);
        headingRow.setAlignment(Pos.CENTER_LEFT);
        headingRow.setPadding(new Insets(0, 0, 2, 2));
        return headingRow;
    }

    private void styleLeaderboardTable(TableView<LeaderboardRow> table) {
        // keeping the table dark so it fits the rest of the dashboard better
        table.setStyle(
                "-fx-background-color: rgba(10, 19, 37, 0.98);" +
                "-fx-control-inner-background: rgba(10, 19, 37, 0.98);" +
                "-fx-control-inner-background-alt: rgba(8, 16, 31, 0.98);" +
                "-fx-table-cell-border-color: rgba(75, 105, 150, 0.18);" +
                "-fx-table-header-border-color: transparent;" +
                "-fx-selection-bar: rgba(31, 110, 170, 0.30);" +
                "-fx-selection-bar-non-focused: rgba(31, 110, 170, 0.20);" +
                "-fx-background-radius: 10;" +
                "-fx-border-color: rgba(63, 97, 145, 0.32);" +
                "-fx-border-radius: 10;"
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
