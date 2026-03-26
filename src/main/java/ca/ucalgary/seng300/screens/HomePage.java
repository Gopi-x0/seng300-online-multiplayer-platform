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

}