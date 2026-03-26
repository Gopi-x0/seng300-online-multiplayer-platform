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

}