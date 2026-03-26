package ca.ucalgary.seng300.screens;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * AuthenticationScreen displays the login and registration UI side by side.
 * Implements the AuthenticationScreen contract from the UI & Navigation Layer UML (Diagram 04).
 *
 * @author Muhammad Ahmad
 * @email muham.ahmad@ucalgary.ca
 */
public class AuthenticationScreen extends StackPane {

    public AuthenticationScreen() {
        initializeLayout();
    }

    // Layout Setup

    private void initializeLayout() {
        // Dark background matching platform style
        setStyle("-fx-background-color: #020b1c;");
        setPadding(new Insets(40));

        VBox root = new VBox(36);
        root.setAlignment(Pos.CENTER);
        root.setFillWidth(true);

        root.getChildren().add(buildLogoBlock());
        root.getChildren().add(buildCardRow());

        getChildren().add(root);
    }



}