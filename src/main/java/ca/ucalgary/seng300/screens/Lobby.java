package ca.ucalgary.seng300.screens;

import ca.ucalgary.seng300.model.GameType;
import javafx.animation.ScaleTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;

/**
 * Lobby selection screen - lets the player choose to Host or Join a game.
 * Shown after pressing PLAY on the instructions screen.
 * 
 * @author Muhammad Ahmad
 * @email muham.ahmad@ucalgary.ca
 */
public class Lobby extends StackPane {

    private final String gameName;
    private final Runnable onHost;
    private final Runnable onJoin;
    private final Runnable onBack;

    // Constructor - sets up the lobby selection screen for the given game
    // @author Muhammad Ahmad
    public Lobby(String gameName, GameType gameType, Runnable onHost, Runnable onJoin, Runnable onBack) {
        this.gameName = gameName;
        this.onHost = onHost;
        this.onJoin = onJoin;
        this.onBack = onBack;
        buildUI();
    }

    // Builds the main layout with title, subtitle, and host/join cards
    // @author Muhammad Ahmad
    private void buildUI() {
        VBox mainLayout = new VBox(0);
        mainLayout.setStyle("-fx-background-color: linear-gradient(to bottom, #020b1c, #0f172b);");
        mainLayout.setPadding(new Insets(40));
        mainLayout.setFillWidth(true);

        HBox topBar = createTopBar();

        Label title = new Label(gameName);
        title.setFont(Font.font("Arial", FontWeight.BOLD, 48));
        title.setTextFill(Color.WHITE);

        Label subtitle = new Label("Choose how you want to play");
        subtitle.setFont(Font.font("Arial", 16));
        subtitle.setTextFill(Color.web("#89a4c6"));

        VBox titleBox = new VBox(8, title, subtitle);
        titleBox.setAlignment(Pos.CENTER);
        titleBox.setPadding(new Insets(60, 0, 60, 0));

        HBox cardRow = createCardRow();

        VBox content = new VBox(0, topBar, titleBox, cardRow);
        content.setAlignment(Pos.TOP_CENTER);

        mainLayout.getChildren().add(content);
        VBox.setVgrow(content, Priority.ALWAYS);

        getChildren().add(mainLayout);
    }

    // Creates the top navigation bar with a styled back button
    // @author Muhammad Ahmad
    private HBox createTopBar() {
        // Back button styling - base, hover, pressed states
        String baseStyle =
                "-fx-text-fill: #00c8ff;" +
                "-fx-font-size: 14px;" +
                "-fx-cursor: hand;" +
                "-fx-background-color: rgba(0,200,255,0.07);" +
                "-fx-background-radius: 10;" +
                "-fx-padding: 4 14 4 14;";
        String hoverStyle =
                "-fx-text-fill: #33d6ff;" +
                "-fx-font-size: 14px;" +
                "-fx-cursor: hand;" +
                "-fx-background-color: rgba(0,200,255,0.15);" +
                "-fx-background-radius: 10;" +
                "-fx-padding: 4 14 4 14;";
        String pressedStyle =
                "-fx-text-fill: #66e0ff;" +
                "-fx-font-size: 14px;" +
                "-fx-cursor: hand;" +
                "-fx-background-color: rgba(0,200,255,0.2);" +
                "-fx-background-radius: 10;" +
                "-fx-padding: 4 14 4 14;";

        Button back = new Button("\u2190 Back to Home");
        back.setStyle(baseStyle);
        back.setOnMouseEntered(e -> {
            back.setStyle(hoverStyle);
            ScaleTransition st = new ScaleTransition(Duration.millis(150), back);
            st.setToX(1.05); st.setToY(1.05); st.play();
        });
        back.setOnMouseExited(e -> {
            back.setStyle(baseStyle);
            ScaleTransition st = new ScaleTransition(Duration.millis(150), back);
            st.setToX(1.0); st.setToY(1.0); st.play();
        });
        back.setOnMousePressed(e -> {
            back.setStyle(pressedStyle);
            ScaleTransition st = new ScaleTransition(Duration.millis(80), back);
            st.setToX(0.96); st.setToY(0.96); st.play();
        });
        back.setOnMouseReleased(e -> {
            back.setStyle(hoverStyle);
            ScaleTransition st = new ScaleTransition(Duration.millis(80), back);
            st.setToX(1.05); st.setToY(1.05); st.play();
        });
        back.setOnAction(e -> { if (onBack != null) onBack.run(); });

        HBox bar = new HBox(back);
        bar.setAlignment(Pos.CENTER_LEFT);
        return bar;
    }

    // Creates the row of Host and Join cards side by side
    // @author Muhammad Ahmad
    private HBox createCardRow() {
        VBox hostCard = createRoleCard("\uD83D\uDCBB", "Host Game",
                "Create a new lobby and\nshare the code with a friend",
                onHost);
        VBox joinCard = createRoleCard("\uD83C\uDF10", "Join Game",
                "Enter a lobby code to\njoin an existing game",
                onJoin);

        HBox row = new HBox(30, hostCard, joinCard);
        row.setAlignment(Pos.CENTER);
        row.setMaxWidth(660);

        hostCard.prefWidthProperty().bind(row.widthProperty().subtract(30).divide(2));
        joinCard.prefWidthProperty().bind(row.widthProperty().subtract(30).divide(2));
        hostCard.setMinWidth(0);
        joinCard.setMinWidth(0);

        return row;
    }

    // Creates a single role card (Host or Join) with icon, title, description, and select button
    // @author Muhammad Ahmad
    private VBox createRoleCard(String icon, String title, String description, Runnable action) {
        Label iconLabel = new Label(icon);
        iconLabel.setFont(Font.font(52));

        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        titleLabel.setTextFill(Color.WHITE);

        Label descLabel = new Label(description);
        descLabel.setFont(Font.font("Arial", 14));
        descLabel.setTextFill(Color.web("#89a4c6"));
        descLabel.setWrapText(true);
        descLabel.setStyle("-fx-text-alignment: center;");

        // Select button styling - base, hover, pressed
        String btnBase =
                "-fx-background-color: linear-gradient(to right, #1d4ed8, #2563eb);" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 15px;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 10;" +
                "-fx-cursor: hand;";
        String btnHover =
                "-fx-background-color: linear-gradient(to right, #2563eb, #3b82f6);" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 15px;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 10;" +
                "-fx-cursor: hand;";
        String btnPress =
                "-fx-background-color: #1e40af;" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 15px;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 10;" +
                "-fx-cursor: hand;";

        Button selectBtn = new Button("Select");
        selectBtn.setStyle(btnBase);
        selectBtn.setPrefWidth(160);
        selectBtn.setPrefHeight(40);
        selectBtn.setOnMouseEntered(e -> {
            selectBtn.setStyle(btnHover);
            ScaleTransition st = new ScaleTransition(Duration.millis(150), selectBtn);
            st.setToX(1.04); st.setToY(1.04); st.play();
        });
        selectBtn.setOnMouseExited(e -> {
            selectBtn.setStyle(btnBase);
            ScaleTransition st = new ScaleTransition(Duration.millis(150), selectBtn);
            st.setToX(1.0); st.setToY(1.0); st.play();
        });
        selectBtn.setOnMousePressed(e -> {
            selectBtn.setStyle(btnPress);
            ScaleTransition st = new ScaleTransition(Duration.millis(80), selectBtn);
            st.setToX(0.96); st.setToY(0.96); st.play();
        });
        selectBtn.setOnMouseReleased(e -> {
            selectBtn.setStyle(btnHover);
            ScaleTransition st = new ScaleTransition(Duration.millis(80), selectBtn);
            st.setToX(1.04); st.setToY(1.04); st.play();
        });
        selectBtn.setOnAction(e -> { if (action != null) action.run(); });

        // Card container styling
        VBox card = new VBox(18, iconLabel, titleLabel, descLabel, selectBtn);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(40, 28, 40, 28));
        card.setStyle(
                "-fx-background-color: linear-gradient(to bottom, rgba(34,54,107,0.92), rgba(10,20,40,0.96));" +
                "-fx-background-radius: 14;" +
                "-fx-border-color: rgba(72,114,170,0.4);" +
                "-fx-border-radius: 14;"
        );

        card.setOnMouseEntered(e -> card.setEffect(new DropShadow(20, Color.web("#00c8ff", 0.5))));
        card.setOnMouseExited(e -> card.setEffect(null));

        return card;
    }
}
