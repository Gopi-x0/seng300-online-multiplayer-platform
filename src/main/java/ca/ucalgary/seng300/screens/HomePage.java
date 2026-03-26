package ca.ucalgary.seng300.screens;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * HomePage displays the main game selection hub and recent match records.
 * Implements the HomePage contract from the UI & Navigation Layer UML (Diagram 04).
 *
 * UML methods:
 *   + viewYourOwnRecords(): void
 *   + viewOtherPlayerRecords(): void
 *   + viewSettings(): Settings
 *   + chooseGame(): void
 *
 * Per TA feedback: displayGames() is also included since users must see
 * games before choosing - consistent with MainGameDashboard.
 *
 * Navigation: HomePage --> Settings
 *
 * @author Muhammad Ahmad
 * @email muham.ahmad@ucalgary.ca
 */
public class HomePage extends BorderPane {

    public HomePage() {
        initializeLayout();
    }

    // Layout Setup

    private void initializeLayout() {
        setStyle("-fx-background-color: #020b1c;");
        setPadding(new Insets(28));

        VBox mainContent = new VBox(22);
        mainContent.setFillWidth(true);
        mainContent.setPadding(new Insets(10, 18, 18, 18));

        mainContent.getChildren().add(buildTopBar());
        mainContent.getChildren().add(buildGameCardSection());
        mainContent.getChildren().add(buildRecordButtons());
        mainContent.getChildren().add(buildRecordSection());

        setCenter(mainContent);
    }

    // Top Bar - Logo + Settings link

    private HBox buildTopBar() {
        ImageView logo = buildLogo();

        // Settings link - calls viewSettings()
        Label settingsLink = new Label("Settings");
        settingsLink.setStyle(
                "-fx-text-fill: #00c8ff;" +
                        "-fx-font-size: 14px;" +
                        "-fx-cursor: hand;" +
                        "-fx-background-color: rgba(0,200,255,0.07);" +
                        "-fx-background-radius: 10;" +
                        "-fx-padding: 4 14 4 14;"
        );
        settingsLink.setOnMouseEntered(e ->
                settingsLink.setStyle(
                        "-fx-text-fill: #33e0ff;" +
                                "-fx-font-size: 14px;" +
                                "-fx-cursor: hand;" +
                                "-fx-background-color: rgba(0,200,255,0.15);" +
                                "-fx-background-radius: 10;" +
                                "-fx-padding: 4 14 4 14;"
                )
        );
        settingsLink.setOnMouseExited(e ->
                settingsLink.setStyle(
                        "-fx-text-fill: #00c8ff;" +
                                "-fx-font-size: 14px;" +
                                "-fx-cursor: hand;" +
                                "-fx-background-color: rgba(0,200,255,0.07);" +
                                "-fx-background-radius: 10;" +
                                "-fx-padding: 4 14 4 14;"
                )
        );
        settingsLink.setOnMouseClicked(e -> viewSettings());

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox bar = new HBox(spacer, settingsLink);
        bar.setAlignment(Pos.CENTER_LEFT);
        // Add logo on the left
        bar.getChildren().add(0, logo);
        return bar;
    }

    private ImageView buildLogo() {
        Image image = new Image(
                getClass().getResourceAsStream("/ca/ucalgary/seng300/images/sgp-logo.png")
        );
        ImageView logo = new ImageView(image);
        logo.setPreserveRatio(true);
        logo.setFitWidth(180);
        logo.setSmooth(true);
        return logo;
    }

    // UML Method: displayGames()
    // Builds the game card section (per TA: must show games before choosing)

    /**
     * Displays the available games for selection.
     * Per TA feedback: displayGames() precedes chooseGame() for correct flow.
     *
     * @author Muhammad Ahmad
     * @email muham.ahmad@ucalgary.ca
     */
    public void displayGames() {
        // Stub TODO: will pull available game list from PlatformCore in future iteration
        System.out.println("HomePage: displayGames() called");
    }

    private HBox buildGameCardSection() {
        displayGames();
        VBox tttCard = createGameCard(
                "Tic Tac Toe",
                "Take on the classic game of Tic Tac Toe!",
                buildTTTPreview()
        );
        VBox c4Card = createGameCard(
                "Connect 4",
                "Outsmart your opponent in a game of Connect 4!",
                buildC4Preview()
        );

        HBox.setHgrow(tttCard, Priority.ALWAYS);
        HBox.setHgrow(c4Card, Priority.ALWAYS);

        HBox row = new HBox(18, tttCard, c4Card);
        row.setFillHeight(true);
        return row;
    }

    private VBox createGameCard(String title, String subtitle, Region preview) {
        // Mini preview area
        preview.setMinHeight(130);
        preview.setMaxWidth(Double.MAX_VALUE);
        preview.setStyle(
                "-fx-background-color: linear-gradient(to bottom, rgba(31,50,102,0.72), rgba(11,20,40,0.96));" +
                        "-fx-background-radius: 12 12 0 0;" +
                        "-fx-padding: 18 14 14 14;"
        );

        Label titleLabel = new Label(title);
        titleLabel.setTextFill(Color.WHITE);
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));

        Label subtitleLabel = new Label(subtitle);
        subtitleLabel.setTextFill(Color.web("#89a4c6"));
        subtitleLabel.setFont(Font.font("Arial", 11));
        subtitleLabel.setWrapText(true);

        VBox textBlock = new VBox(4, titleLabel, subtitleLabel);
        textBlock.setPadding(new Insets(8, 12, 4, 12));

        // PLAY NOW button - calls chooseGame()
        Button playBtn = new Button("PLAY NOW");
        playBtn.setMaxWidth(Double.MAX_VALUE);
        playBtn.setPrefHeight(36);
        String playBase =
                "-fx-background-color: transparent;" +
                        "-fx-text-fill: #00c8ff;" +
                        "-fx-font-size: 12px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-border-color: #0096b8;" +
                        "-fx-border-width: 1.2;" +
                        "-fx-border-radius: 4;" +
                        "-fx-background-radius: 4;" +
                        "-fx-cursor: hand;";
        String playHover =
                "-fx-background-color: rgba(0,200,255,0.08);" +
                        "-fx-text-fill: #33e0ff;" +
                        "-fx-font-size: 12px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-border-color: #00c8ff;" +
                        "-fx-border-width: 1.2;" +
                        "-fx-border-radius: 4;" +
                        "-fx-background-radius: 4;" +
                        "-fx-cursor: hand;";
        playBtn.setStyle(playBase);
        playBtn.setOnMouseEntered(e -> playBtn.setStyle(playHover));
        playBtn.setOnMouseExited(e -> playBtn.setStyle(playBase));
        playBtn.setOnAction(e -> chooseGame());
        VBox.setMargin(playBtn, new Insets(0, 12, 10, 12));

        VBox card = new VBox(preview, textBlock, playBtn);
        card.setMaxWidth(Double.MAX_VALUE);
        card.setStyle(
                "-fx-background-color: linear-gradient(to bottom, rgba(34,54,107,0.92), rgba(10,20,40,0.96));" +
                        "-fx-background-radius: 14;" +
                        "-fx-border-color: rgba(72,114,170,0.4);" +
                        "-fx-border-radius: 14;"
        );
        return card;
    }

    // Mini board previews

    private Region buildTTTPreview() {
        // 3x3 grid of cells with X/O placeholders
        javafx.scene.layout.GridPane board = new javafx.scene.layout.GridPane();
        board.setHgap(6);
        board.setVgap(6);
        board.setAlignment(Pos.CENTER);

        String[][] marks = {
                {"X", "O", ""},
                {"X", "O", ""},
                {"X", "", "O"}
        };

        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                StackPane cell = new StackPane();
                cell.setPrefSize(30, 30);
                cell.setStyle(
                        "-fx-background-color: rgba(9,25,48,0.98);" +
                                "-fx-border-color: #39cfff;" +
                                "-fx-border-radius: 4;" +
                                "-fx-background-radius: 4;"
                );
                Label mark = new Label(marks[r][c]);
                mark.setTextFill(Color.web("#38cfff"));
                mark.setFont(Font.font("Arial", FontWeight.BOLD, 13));
                cell.getChildren().add(mark);
                board.add(cell, c, r);
            }
        }
        return board;
    }

    private Region buildC4Preview() {
        // 4x5 mini connect 4 grid with colored circles
        javafx.scene.layout.GridPane board = new javafx.scene.layout.GridPane();
        board.setHgap(5);
        board.setVgap(5);
        board.setAlignment(Pos.CENTER);

        Color[][] tokens = {
                {Color.TRANSPARENT, Color.web("#ff4b4b"), Color.web("#ffd93d"), Color.TRANSPARENT, Color.web("#4ea1ff")},
                {Color.web("#ffd93d"), Color.TRANSPARENT, Color.web("#ff4b4b"), Color.web("#4ea1ff"), Color.TRANSPARENT},
                {Color.web("#ff4b4b"), Color.web("#ffd93d"), Color.TRANSPARENT, Color.web("#ff4b4b"), Color.web("#ffd93d")},
                {Color.web("#4ea1ff"), Color.web("#ff4b4b"), Color.web("#ffd93d"), Color.TRANSPARENT, Color.web("#ff4b4b")}
        };

        for (int r = 0; r < tokens.length; r++) {
            for (int c = 0; c < tokens[r].length; c++) {
                Circle token = new Circle(8);
                token.setFill(tokens[r][c]);
                token.setStroke(Color.web("#31c6ff"));
                token.setStrokeWidth(0.8);
                board.add(token, c, r);
            }
        }
        return board;
    }

    // Record Buttons - View Self / View Player Records

    private HBox buildRecordButtons() {
        Button selfBtn = outlineButton("View Self Records");
        selfBtn.setOnAction(e -> viewYourOwnRecords());

        Button playerBtn = outlineButton("View Player Records  ▾");
        playerBtn.setOnAction(e -> viewOtherPlayerRecords());

        HBox.setHgrow(selfBtn, Priority.ALWAYS);
        HBox.setHgrow(playerBtn, Priority.ALWAYS);

        HBox row = new HBox(14, selfBtn, playerBtn);
        row.setFillHeight(true);
        return row;
    }

    private Button outlineButton(String text) {
        Button btn = new Button(text);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setPrefHeight(38);
        String base =
                "-fx-background-color: transparent;" +
                        "-fx-text-fill: #00c8ff;" +
                        "-fx-font-size: 13px;" +
                        "-fx-border-color: #0096b8;" +
                        "-fx-border-width: 1.2;" +
                        "-fx-border-radius: 6;" +
                        "-fx-background-radius: 6;" +
                        "-fx-cursor: hand;";
        String hover =
                "-fx-background-color: rgba(0,200,255,0.08);" +
                        "-fx-text-fill: #33e0ff;" +
                        "-fx-font-size: 13px;" +
                        "-fx-border-color: #00c8ff;" +
                        "-fx-border-width: 1.2;" +
                        "-fx-border-radius: 6;" +
                        "-fx-background-radius: 6;" +
                        "-fx-cursor: hand;";
        btn.setStyle(base);
        btn.setOnMouseEntered(e -> btn.setStyle(hover));
        btn.setOnMouseExited(e -> btn.setStyle(base));
        return btn;
    }

    // Record Table Section

    private VBox buildRecordSection() {
        // Heading row
        Label icon = new Label("🏆");
        icon.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        Label heading = new Label("RECORD");
        heading.setTextFill(Color.WHITE);
        heading.setFont(Font.font("Arial", FontWeight.BOLD, 15));
        HBox headingRow = new HBox(8, icon, heading);
        headingRow.setAlignment(Pos.CENTER_LEFT);

        TableView<RecordRow> table = buildRecordTable();

        VBox section = new VBox(10, headingRow, table);
        section.setPadding(new Insets(14));
        section.setStyle(
                "-fx-background-color: linear-gradient(to bottom, rgba(9,19,36,0.88), rgba(7,15,28,0.96));" +
                        "-fx-background-radius: 12;" +
                        "-fx-border-color: rgba(53,84,127,0.38);" +
                        "-fx-border-radius: 12;"
        );
        return section;
    }

    private TableView<RecordRow> buildRecordTable() {
        TableView<RecordRow> table = new TableView<>();

        TableColumn<RecordRow, Number> kdCol = new TableColumn<>("KD");
        kdCol.setCellValueFactory(d -> d.getValue().kdProperty());

        TableColumn<RecordRow, String> playerCol = new TableColumn<>("PLAYER");
        playerCol.setCellValueFactory(d -> d.getValue().playerProperty());

        TableColumn<RecordRow, Number> acsCol = new TableColumn<>("ACS");
        acsCol.setCellValueFactory(d -> d.getValue().acsProperty());

        TableColumn<RecordRow, Number> hsCol = new TableColumn<>("Headshot Count");
        hsCol.setCellValueFactory(d -> d.getValue().headshotProperty());

        TableColumn<RecordRow, String> gameCol = new TableColumn<>("Game");
        gameCol.setCellValueFactory(d -> d.getValue().gameProperty());

        for (TableColumn<RecordRow, ?> col : new TableColumn[]{kdCol, playerCol, acsCol, hsCol, gameCol}) {
            col.setSortable(false);
            col.setResizable(false);
            col.setStyle("-fx-alignment: CENTER-LEFT;");
        }

        table.getColumns().addAll(kdCol, playerCol, acsCol, hsCol, gameCol);

        // Stub data TODO - will be replaced by PlatformCore query in future iteration
        table.setItems(javafx.collections.FXCollections.observableArrayList(
                new RecordRow(1.78, "Muhammad Ahmad", 2850, 47, "Tic Tac Toe"),
                new RecordRow(1.24, "Muhammad Ahmad", 2620, 42, "Connect 4")
        ));

        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        table.setMaxWidth(Double.MAX_VALUE);
        table.setPrefHeight(110);
        table.setStyle(
                "-fx-background-color: rgba(10,19,37,0.98);" +
                        "-fx-control-inner-background: rgba(10,19,37,0.98);" +
                        "-fx-control-inner-background-alt: rgba(8,16,31,0.98);" +
                        "-fx-table-cell-border-color: rgba(75,105,150,0.18);" +
                        "-fx-table-header-border-color: transparent;" +
                        "-fx-selection-bar: rgba(31,110,170,0.30);" +
                        "-fx-background-radius: 8;" +
                        "-fx-border-color: rgba(63,97,145,0.32);" +
                        "-fx-border-radius: 8;"
        );
        return table;
    }

    // UML Methods

    /**
     * Displays the current user's own match records.
     * Will query PlatformCore for player history in a future iteration.
     *
     * @author Muhammad Ahmad
     * @email muham.ahmad@ucalgary.ca
     */
    public void viewYourOwnRecords() {
        System.out.println("HomePage: viewYourOwnRecords() called");
        // TODO: fetch and display this player's match history from PlatformCore
    }

    /**
     * Displays other players' records - e.g. for profile browsing or rivalry.
     * Will query PlatformCore in a future iteration.
     *
     * @author Muhammad Ahmad
     * @email muham.ahmad@ucalgary.ca
     */
    public void viewOtherPlayerRecords() {
        System.out.println("HomePage: viewOtherPlayerRecords() called");
        // TODO: open player search or leaderboard view via PlatformCore
    }

    /**
     * Navigates to the Settings screen.
     * Returns a Settings instance per UML contract.
     *
     * @author Muhammad Ahmad
     * @email muham.ahmad@ucalgary.ca
     */
    public void viewSettings() {
        System.out.println("HomePage: viewSettings() called - navigating to SettingsScreen");
        // TODO: wire actual screen navigation to SettingsScreen
    }

    /**
     * Handles game selection - triggered by PLAY NOW button on a game card.
     * Will route to PlayGame via PlatformCore joinQueue() in a future iteration.
     *
     * @author Muhammad Ahmad
     * @email muham.ahmad@ucalgary.ca
     */
    public void chooseGame() {
        System.out.println("HomePage: chooseGame() called - routing to PlayGame");
        // TODO: call PlatformCore.joinQueue(gameType) and navigate to PlayGame
    }

    // RecordRow data model for the table

    private static class RecordRow {
        private final javafx.beans.property.SimpleDoubleProperty kd;
        private final SimpleStringProperty player;
        private final SimpleIntegerProperty acs;
        private final SimpleIntegerProperty headshot;
        private final SimpleStringProperty game;

        RecordRow(double kd, String player, int acs, int headshot, String game) {
            this.kd       = new javafx.beans.property.SimpleDoubleProperty(kd);
            this.player   = new SimpleStringProperty(player);
            this.acs      = new SimpleIntegerProperty(acs);
            this.headshot = new SimpleIntegerProperty(headshot);
            this.game     = new SimpleStringProperty(game);
        }

        javafx.beans.property.SimpleDoubleProperty kdProperty()  { return kd; }
        SimpleStringProperty playerProperty()                     { return player; }
        SimpleIntegerProperty acsProperty()                       { return acs; }
        SimpleIntegerProperty headshotProperty()                  { return headshot; }
        SimpleStringProperty gameProperty()                       { return game; }
    }
}