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


}