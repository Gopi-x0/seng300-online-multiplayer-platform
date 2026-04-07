package ca.ucalgary.seng300.screens;

import ca.ucalgary.seng300.model.UserData;
import ca.ucalgary.seng300.service.UserManager;
import javafx.animation.ScaleTransition;
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
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

/**
 * HomePage displays the main game selection hub and recent match records.
 *
 * @author Muhammad Ahmad
 * @email muham.ahmad@ucalgary.ca
 */
public class HomePage extends BorderPane {

    private final Runnable onSettings;
    private final Runnable onPlayTTT;
    private final Runnable onPlayC4;
    private final Runnable onProfile;

    private TableView<RecordRow> recordTable;

    // Constructor - takes callbacks for navigating to settings, TTT, C4, and profile
    // @author Muhammad Ahmad
    public HomePage(Runnable onSettings, Runnable onPlayTTT, Runnable onPlayC4, Runnable onProfile) {
        this.onSettings = onSettings;
        this.onPlayTTT = onPlayTTT;
        this.onPlayC4 = onPlayC4;
        this.onProfile = onProfile;
        initializeLayout();
    }

    // Builds the main page layout - top bar, game cards, record buttons, record table
    // @author Muhammad Ahmad
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

        loadSelfRecords();
    }

    // Top Bar

    // Builds the top bar with logo on the left and username/profile/settings on the right
    // @author Muhammad Ahmad
    private HBox buildTopBar() {
        ImageView logo = buildLogo();

        UserData user = UserManager.getCurrentUser();
        String displayName = (user != null) ? user.getUsername() : "Guest";

        Label usernameLabel = new Label(displayName);
        usernameLabel.setStyle(
                "-fx-text-fill: white;" +
                "-fx-font-size: 15px;" +
                "-fx-font-weight: bold;"
        );
        usernameLabel.setMaxWidth(200);
        usernameLabel.setEllipsisString("...");
        usernameLabel.setTextOverrun(javafx.scene.control.OverrunStyle.ELLIPSIS);

        // Profile link styling with hover animation
        Label profileLink = new Label("Profile");
        profileLink.setStyle(
                "-fx-text-fill: #00c8ff;" +
                "-fx-font-size: 14px;" +
                "-fx-cursor: hand;" +
                "-fx-background-color: rgba(0,200,255,0.07);" +
                "-fx-background-radius: 10;" +
                "-fx-padding: 4 14 4 14;"
        );
        profileLink.setOnMouseEntered(e -> {
                profileLink.setStyle(
                        "-fx-text-fill: #33e0ff;" +
                        "-fx-font-size: 14px;" +
                        "-fx-cursor: hand;" +
                        "-fx-background-color: rgba(0,200,255,0.15);" +
                        "-fx-background-radius: 10;" +
                        "-fx-padding: 4 14 4 14;"
                );
                ScaleTransition st = new ScaleTransition(Duration.millis(150), profileLink);
                st.setToX(1.08); st.setToY(1.08); st.play();
        });
        profileLink.setOnMouseExited(e -> {
                profileLink.setStyle(
                        "-fx-text-fill: #00c8ff;" +
                        "-fx-font-size: 14px;" +
                        "-fx-cursor: hand;" +
                        "-fx-background-color: rgba(0,200,255,0.07);" +
                        "-fx-background-radius: 10;" +
                        "-fx-padding: 4 14 4 14;"
                );
                ScaleTransition st = new ScaleTransition(Duration.millis(150), profileLink);
                st.setToX(1.0); st.setToY(1.0); st.play();
        });
        profileLink.setOnMouseClicked(e -> {
            if (onProfile != null) onProfile.run();
        });

        // Settings link styling with hover animation
        Label settingsLink = new Label("Settings");
        settingsLink.setStyle(
                "-fx-text-fill: #00c8ff;" +
                "-fx-font-size: 14px;" +
                "-fx-cursor: hand;" +
                "-fx-background-color: rgba(0,200,255,0.07);" +
                "-fx-background-radius: 10;" +
                "-fx-padding: 4 14 4 14;"
        );
        settingsLink.setOnMouseEntered(e -> {
                settingsLink.setStyle(
                        "-fx-text-fill: #33e0ff;" +
                        "-fx-font-size: 14px;" +
                        "-fx-cursor: hand;" +
                        "-fx-background-color: rgba(0,200,255,0.15);" +
                        "-fx-background-radius: 10;" +
                        "-fx-padding: 4 14 4 14;"
                );
                ScaleTransition st = new ScaleTransition(Duration.millis(150), settingsLink);
                st.setToX(1.08); st.setToY(1.08); st.play();
        });
        settingsLink.setOnMouseExited(e -> {
                settingsLink.setStyle(
                        "-fx-text-fill: #00c8ff;" +
                        "-fx-font-size: 14px;" +
                        "-fx-cursor: hand;" +
                        "-fx-background-color: rgba(0,200,255,0.07);" +
                        "-fx-background-radius: 10;" +
                        "-fx-padding: 4 14 4 14;"
                );
                ScaleTransition st = new ScaleTransition(Duration.millis(150), settingsLink);
                st.setToX(1.0); st.setToY(1.0); st.play();
        });
        settingsLink.setOnMouseClicked(e -> viewSettings());

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox rightGroup = new HBox(12, usernameLabel, profileLink, settingsLink);
        rightGroup.setAlignment(Pos.CENTER_RIGHT);

        HBox bar = new HBox(logo, spacer, rightGroup);
        bar.setAlignment(Pos.CENTER_LEFT);
        return bar;
    }

    // Loads the app logo image from resources
    // @author Muhammad Ahmad
    private ImageView buildLogo() {
        ImageView logo = new ImageView();
        java.io.InputStream stream = getClass().getResourceAsStream("/ca/ucalgary/seng300/images/sgp-logo.png");
        if (stream != null) {
            logo.setImage(new Image(stream));
        }
        logo.setPreserveRatio(true);
        logo.setFitWidth(180);
        logo.setSmooth(true);
        return logo;
    }

    // Game Cards

    // Builds the row of game cards (Tic Tac Toe and Connect 4) side by side
    // @author Muhammad Ahmad
    private HBox buildGameCardSection() {
        VBox tttCard = createGameCard(
                "Tic Tac Toe",
                "Take on the classic game of Tic Tac Toe!",
                buildTTTPreview(),
                onPlayTTT
        );
        VBox c4Card = createGameCard(
                "Connect 4",
                "Outsmart your opponent in a game of Connect 4!",
                buildC4Preview(),
                onPlayC4
        );

        HBox row = new HBox(16, tttCard, c4Card);
        row.setFillHeight(true);
        row.setAlignment(Pos.CENTER);
        row.setMaxWidth(Double.MAX_VALUE);

        tttCard.prefWidthProperty().bind(row.widthProperty().subtract(16).divide(2));
        c4Card.prefWidthProperty().bind(row.widthProperty().subtract(16).divide(2));
        tttCard.setMinWidth(0);
        c4Card.setMinWidth(0);
        tttCard.setMaxWidth(Double.MAX_VALUE);
        c4Card.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(tttCard, Priority.ALWAYS);
        HBox.setHgrow(c4Card, Priority.ALWAYS);

        return row;
    }

    // Creates a single game card with a preview, title, subtitle, and play button
    // @author Muhammad Ahmad
    private VBox createGameCard(String title, String subtitle, Region preview, Runnable onPlay) {
        // Preview area styling at the top of the card
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

        // Play button with outline style and hover effects
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
        playBtn.setOnMouseEntered(e -> {
            playBtn.setStyle(playHover);
            ScaleTransition st = new ScaleTransition(Duration.millis(150), playBtn);
            st.setToX(1.04); st.setToY(1.04); st.play();
        });
        playBtn.setOnMouseExited(e -> {
            playBtn.setStyle(playBase);
            ScaleTransition st = new ScaleTransition(Duration.millis(150), playBtn);
            st.setToX(1.0); st.setToY(1.0); st.play();
        });
        playBtn.setOnMousePressed(e -> {
            ScaleTransition st = new ScaleTransition(Duration.millis(80), playBtn);
            st.setToX(0.96); st.setToY(0.96); st.play();
        });
        playBtn.setOnMouseReleased(e -> {
            ScaleTransition st = new ScaleTransition(Duration.millis(80), playBtn);
            st.setToX(1.04); st.setToY(1.04); st.play();
        });
        playBtn.setOnAction(e -> {
            if (onPlay != null) onPlay.run();
        });
        VBox.setMargin(playBtn, new Insets(0, 12, 10, 12));

        // Card container styling
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

    // Builds a small Tic Tac Toe board preview with some X and O marks
    // @author Muhammad Ahmad
    private Region buildTTTPreview() {
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
                String m = marks[r][c];
                Label mark = new Label(m);
                if ("X".equals(m)) {
                    mark.setTextFill(Color.web("#ff4b4b"));
                } else if ("O".equals(m)) {
                    mark.setTextFill(Color.web("#4ea1ff"));
                } else {
                    mark.setTextFill(Color.TRANSPARENT);
                }
                mark.setFont(Font.font("Arial", FontWeight.BOLD, 13));
                cell.getChildren().add(mark);
                board.add(cell, c, r);
            }
        }
        return board;
    }

    // Builds a small Connect 4 board preview with colored tokens
    // @author Muhammad Ahmad
    private Region buildC4Preview() {
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

    // Record Buttons

    // Builds the row with "View Self Records" and "All Player Records" buttons
    // @author Muhammad Ahmad
    private HBox buildRecordButtons() {
        Button selfBtn = outlineButton("View Self Records");
        selfBtn.setOnAction(e -> loadSelfRecords());

        Button allBtn = outlineButton("All Player Records");
        allBtn.setOnAction(e -> loadAllRecords());

        HBox row = new HBox(16, selfBtn, allBtn);
        row.setFillHeight(true);
        row.setAlignment(Pos.CENTER);
        row.setMaxWidth(Double.MAX_VALUE);

        selfBtn.prefWidthProperty().bind(row.widthProperty().subtract(16).divide(2));
        allBtn.prefWidthProperty().bind(row.widthProperty().subtract(16).divide(2));
        selfBtn.setMinWidth(0);
        allBtn.setMinWidth(0);
        selfBtn.setMaxWidth(Double.MAX_VALUE);
        allBtn.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(selfBtn, Priority.ALWAYS);
        HBox.setHgrow(allBtn, Priority.ALWAYS);

        return row;
    }

    // Creates a transparent outline-style button with hover/press animations
    // @author Muhammad Ahmad
    private Button outlineButton(String text) {
        Button btn = new Button(text);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setPrefHeight(38);
        // Outline button styling - base and hover states
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
        btn.setOnMouseEntered(e -> {
            btn.setStyle(hover);
            ScaleTransition st = new ScaleTransition(Duration.millis(150), btn);
            st.setToX(1.03); st.setToY(1.03); st.play();
        });
        btn.setOnMouseExited(e -> {
            btn.setStyle(base);
            ScaleTransition st = new ScaleTransition(Duration.millis(150), btn);
            st.setToX(1.0); st.setToY(1.0); st.play();
        });
        btn.setOnMousePressed(e -> {
            ScaleTransition st = new ScaleTransition(Duration.millis(80), btn);
            st.setToX(0.97); st.setToY(0.97); st.play();
        });
        btn.setOnMouseReleased(e -> {
            ScaleTransition st = new ScaleTransition(Duration.millis(80), btn);
            st.setToX(1.03); st.setToY(1.03); st.play();
        });
        return btn;
    }

    // Record Table

    // Loads only the current user's records into the table
    // @author Muhammad Ahmad
    private void loadSelfRecords() {
        if (recordTable == null) return;
        UserManager.loadUsers();
        recordTable.getItems().clear();
        UserData user = UserManager.getCurrentUser();
        if (user == null) return;
        addUserRows(user);
    }

    // Loads all users' records into the table
    // @author Muhammad Ahmad
    private void loadAllRecords() {
        if (recordTable == null) return;
        UserManager.loadUsers();
        recordTable.getItems().clear();
        for (UserData user : UserManager.getAllUsers()) {
            addUserRows(user);
        }
    }

    // Adds TTT and C4 record rows for a single user to the table
    // @author Muhammad Ahmad
    private void addUserRows(UserData user) {
        String tttWinRate = String.format("%.1f%%", user.getTttWinRate());
        String c4WinRate = String.format("%.1f%%", user.getC4WinRate());

        recordTable.getItems().add(new RecordRow(
                user.getUsername(), "Tic Tac Toe",
                user.getTttWins(), user.getTttLosses(), user.getTttDraws(), tttWinRate,
                user.getTttCurrentStreak(), user.getTttPoints()
        ));
        recordTable.getItems().add(new RecordRow(
                user.getUsername(), "Connect 4",
                user.getC4Wins(), user.getC4Losses(), user.getC4Draws(), c4WinRate,
                user.getC4CurrentStreak(), user.getC4Points()
        ));
    }

    // Builds the record section with heading and the styled table
    // @author Muhammad Ahmad
    private VBox buildRecordSection() {
        Label icon = new Label("\u27E1");
        icon.setTextFill(Color.web("#00c8ff"));
        icon.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        Label heading = new Label("RECORD");
        heading.setTextFill(Color.WHITE);
        heading.setFont(Font.font("Arial", FontWeight.BOLD, 15));
        HBox headingRow = new HBox(8, icon, heading);
        headingRow.setAlignment(Pos.CENTER_LEFT);

        recordTable = buildRecordTable();

        Rectangle clip = new Rectangle();
        clip.arcWidthProperty().set(20);
        clip.arcHeightProperty().set(20);
        clip.widthProperty().bind(recordTable.widthProperty());
        clip.heightProperty().bind(recordTable.heightProperty());
        recordTable.setClip(clip);

        // Section container styling
        VBox section = new VBox(10, headingRow, recordTable);
        section.setPadding(new Insets(14));
        section.setStyle(
                "-fx-background-color: linear-gradient(to bottom, rgba(9,19,36,0.88), rgba(7,15,28,0.96));" +
                "-fx-background-radius: 12;" +
                "-fx-border-color: rgba(53,84,127,0.38);" +
                "-fx-border-radius: 12;"
        );
        return section;
    }

    // Builds and configures the record TableView with all columns
    // @author Muhammad Ahmad
    @SuppressWarnings("unchecked")
    private TableView<RecordRow> buildRecordTable() {
        TableView<RecordRow> table = new TableView<>();

        TableColumn<RecordRow, String> playerCol = new TableColumn<>("PLAYER");
        playerCol.setCellValueFactory(d -> d.getValue().playerProperty());

        TableColumn<RecordRow, String> gameCol = new TableColumn<>("GAME");
        gameCol.setCellValueFactory(d -> d.getValue().gameProperty());

        TableColumn<RecordRow, Number> winsCol = new TableColumn<>("WINS");
        winsCol.setCellValueFactory(d -> d.getValue().winsProperty());

        TableColumn<RecordRow, Number> lossesCol = new TableColumn<>("LOSSES");
        lossesCol.setCellValueFactory(d -> d.getValue().lossesProperty());

        TableColumn<RecordRow, Number> drawsCol = new TableColumn<>("DRAWS");
        drawsCol.setCellValueFactory(d -> d.getValue().drawsProperty());

        TableColumn<RecordRow, String> winRateCol = new TableColumn<>("WIN RATE");
        winRateCol.setCellValueFactory(d -> d.getValue().winRateProperty());

        TableColumn<RecordRow, Number> streakCol = new TableColumn<>("STREAK");
        streakCol.setCellValueFactory(d -> d.getValue().streakProperty());

        TableColumn<RecordRow, Number> pointsCol = new TableColumn<>("POINTS");
        pointsCol.setCellValueFactory(d -> d.getValue().pointsProperty());

        for (TableColumn<RecordRow, ?> col : new TableColumn[]{playerCol, gameCol, winsCol, lossesCol, drawsCol, winRateCol, streakCol, pointsCol}) {
            col.setSortable(false);
            col.setReorderable(false);
            col.setStyle("-fx-alignment: CENTER-LEFT;");
        }

        playerCol.setPrefWidth(220);
        gameCol.setPrefWidth(180);
        winsCol.setPrefWidth(75);
        lossesCol.setPrefWidth(75);
        drawsCol.setPrefWidth(75);
        winRateCol.setPrefWidth(95);
        streakCol.setPrefWidth(75);
        pointsCol.setPrefWidth(85);

        playerCol.setCellFactory(col -> createStyledCell(false));
        gameCol.setCellFactory(col -> createStyledCell(false));
        winsCol.setCellFactory(col -> createStyledCell(false));
        lossesCol.setCellFactory(col -> createStyledCell(false));
        drawsCol.setCellFactory(col -> createStyledCell(false));
        winRateCol.setCellFactory(col -> createStyledCell(true));
        streakCol.setCellFactory(col -> createStyledCell(true));
        pointsCol.setCellFactory(col -> createStyledCell(true));

        table.getColumns().addAll(playerCol, gameCol, winsCol, lossesCol, drawsCol, winRateCol, streakCol, pointsCol);

        // Table dark theme styling
        table.setStyle(
                "-fx-background-color: #06152b;" +
                "-fx-background-insets: 0;" +
                "-fx-control-inner-background: #07162d;" +
                "-fx-control-inner-background-alt: #061226;" +
                "-fx-table-cell-border-color: rgba(0,200,255,0.08);" +
                "-fx-selection-bar: rgba(16,52,95,0.95);" +
                "-fx-selection-bar-non-focused: rgba(16,52,95,0.95);" +
                "-fx-border-color: rgba(22,54,95,0.95);" +
                "-fx-border-radius: 10;" +
                "-fx-background-radius: 10;" +
                "-fx-padding: 0;"
        );

        // Custom row factory for dark row styling
        table.setRowFactory(tv -> new TableRow<>() {
            @Override
            protected void updateItem(RecordRow item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setStyle(
                            "-fx-background-color: transparent;" +
                            "-fx-border-color: transparent;"
                    );
                } else {
                    setStyle(
                            "-fx-background-color: #06152b;" +
                            "-fx-border-color: transparent transparent rgba(0,200,255,0.08) transparent;" +
                            "-fx-table-cell-border-color: rgba(0,200,255,0.05);"
                    );
                }
            }
        });

        Platform.runLater(() -> styleTableInternals(table));

        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        table.setFixedCellSize(34);
        table.setPrefHeight(28 + (34 * 6));
        table.setMinHeight(28 + (34 * 6));
        table.setMaxHeight(28 + (34 * 6));
        table.setMaxWidth(Double.MAX_VALUE);

        return table;
    }

    // Navigates to the settings screen
    // @author Muhammad Ahmad
    private void viewSettings() {
        if (onSettings != null) onSettings.run();
    }

    // RecordRow data model

    // Simple data class that holds one row of record data for the table
    private static class RecordRow {
        private final SimpleStringProperty player;
        private final SimpleStringProperty game;
        private final SimpleIntegerProperty wins;
        private final SimpleIntegerProperty losses;
        private final SimpleIntegerProperty draws;
        private final SimpleStringProperty winRate;
        private final SimpleIntegerProperty streak;
        private final SimpleIntegerProperty points;

        RecordRow(String player, String game, int wins, int losses, int draws, String winRate, int streak, int points) {
            this.player  = new SimpleStringProperty(player);
            this.game    = new SimpleStringProperty(game);
            this.wins    = new SimpleIntegerProperty(wins);
            this.losses  = new SimpleIntegerProperty(losses);
            this.draws   = new SimpleIntegerProperty(draws);
            this.winRate = new SimpleStringProperty(winRate);
            this.streak  = new SimpleIntegerProperty(streak);
            this.points  = new SimpleIntegerProperty(points);
        }

        // Getters and setters
        SimpleStringProperty playerProperty()   { return player; }
        SimpleStringProperty gameProperty()     { return game; }
        SimpleIntegerProperty winsProperty()    { return wins; }
        SimpleIntegerProperty lossesProperty()  { return losses; }
        SimpleIntegerProperty drawsProperty()   { return draws; }
        SimpleStringProperty winRateProperty()  { return winRate; }
        SimpleIntegerProperty streakProperty()  { return streak; }
        SimpleIntegerProperty pointsProperty()  { return points; }
    }

    // Table styling helpers

    // Applies dark theme CSS to table headers, scrollbars, and internal components
    // @author Muhammad Ahmad
    private void styleTableInternals(TableView<RecordRow> table) {
        table.applyCss();
        table.layout();

        for (Node node : table.lookupAll(".column-header-background")) {
            node.setStyle("-fx-background-color: #10284a; -fx-background-radius: 8 8 0 0;");
        }
        for (Node node : table.lookupAll(".filler")) {
            node.setStyle("-fx-background-color: #10284a;");
        }
        for (Node node : table.lookupAll(".corner")) {
            node.setStyle("-fx-background-color: #10284a;");
        }
        for (Node node : table.lookupAll(".column-header")) {
            node.setStyle("-fx-background-color: transparent; -fx-border-color: transparent rgba(111,147,200,0.18) transparent transparent;");
        }
        for (Node node : table.lookupAll(".column-header .label")) {
            node.setStyle("-fx-text-fill: #6f93c8; -fx-font-weight: bold; -fx-font-size: 12px; -fx-alignment: CENTER-LEFT; -fx-padding: 0 0 0 8;");
        }
        for (Node node : table.lookupAll(".virtual-flow .clipped-container")) {
            node.setStyle("-fx-background-color: #06152b;");
        }
        for (Node node : table.lookupAll(".scroll-bar")) {
            node.setStyle("-fx-background-color: #06152b;");
        }
        for (Node node : table.lookupAll(".scroll-bar .track")) {
            node.setStyle("-fx-background-color: #0b1d36;");
        }
        for (Node node : table.lookupAll(".scroll-bar .thumb")) {
            node.setStyle("-fx-background-color: #174a7a; -fx-background-radius: 8;");
        }
        for (Node node : table.lookupAll(".scroll-bar .increment-button")) {
            node.setStyle("-fx-background-color: #06152b;");
        }
        for (Node node : table.lookupAll(".scroll-bar .decrement-button")) {
            node.setStyle("-fx-background-color: #06152b;");
        }
        for (Node node : table.lookupAll(".scroll-bar .increment-arrow")) {
            node.setStyle("-fx-background-color: #6f93c8;");
        }
        for (Node node : table.lookupAll(".scroll-bar .decrement-arrow")) {
            node.setStyle("-fx-background-color: #6f93c8;");
        }
    }

    // Creates a styled table cell - accent blue text for stats columns, white for others
    // @author Muhammad Ahmad
    private <T> TableCell<RecordRow, T> createStyledCell(boolean accentBlue) {
        return new TableCell<>() {
            @Override
            protected void updateItem(T item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                    setStyle(
                            "-fx-background-color: transparent;" +
                            "-fx-border-color: transparent rgba(0,200,255,0.05) transparent transparent;" +
                            "-fx-padding: 8 10 8 10;"
                    );
                } else {
                    Label label = new Label(String.valueOf(item));
                    label.setWrapText(true);
                    label.setMaxWidth(Double.MAX_VALUE);
                    label.setTextFill(Color.web(accentBlue ? "#00c8ff" : "white"));
                    if (accentBlue) {
                        label.setFont(Font.font("Arial", FontWeight.BOLD, 13));
                    } else {
                        label.setFont(Font.font("Arial", 13));
                    }

                    setGraphic(label);
                    setText(null);
                    setStyle(
                            "-fx-background-color: transparent;" +
                            "-fx-border-color: transparent rgba(0,200,255,0.05) transparent transparent;" +
                            "-fx-padding: 8 10 8 10;"
                    );
                }
            }
        };
    }
}
