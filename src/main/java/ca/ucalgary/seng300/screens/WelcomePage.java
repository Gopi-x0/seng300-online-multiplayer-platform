package ca.ucalgary.seng300.screens;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class WelcomePage extends StackPane {

    public WelcomePage() {
        // just calling the main layout stuff here so constructor stays simple
        initializeLayout();
    }

    private void initializeLayout() {
        // little bit of padding so the screen doesnt feel jammed to the edges
        setPadding(new Insets(32));
        setStyle(
                "-fx-background-color: radial-gradient(center 50% 35%, radius 85%, #102749 0%, #08172e 45%, #040b19 100%);" +
                "-fx-background-insets: 0;"
        );

        // this is the main centered box with logo text and buttons
        VBox centeredContent = buildCenteredContent();
        centeredContent.maxWidthProperty().bind(widthProperty().multiply(0.6));

        getChildren().setAll(centeredContent);
    }

    private VBox buildCenteredContent() {
        // using a text logo for now till we hook up the real one properlly
        Label logo = new Label("SGP");
        logo.setTextFill(Color.web("#f4f8ff"));
        logo.setFont(Font.font("Arial", FontWeight.EXTRA_BOLD, 54));
        logo.setStyle(
                "-fx-background-color: linear-gradient(to bottom, rgba(37, 122, 214, 0.24), rgba(4, 10, 25, 0.08));" +
                "-fx-border-color: #2dbdff;" +
                "-fx-border-radius: 20;" +
                "-fx-background-radius: 20;" +
                "-fx-padding: 12 34 12 34;"
        );

        Label title = new Label("Welcome to");
        title.setTextFill(Color.web("#6f95c5"));
        title.setFont(Font.font("Arial", FontWeight.NORMAL, 36));

        Label subtitle = new Label("Student Gaming Platform");
        subtitle.setTextFill(Color.web("#7f94b4"));
        subtitle.setFont(Font.font("Arial", 14));

        // click handler stays empty rn cause no nav logic yet
        Button loginButton = new Button("Login");
        loginButton.setPrefWidth(140);
        loginButton.setPrefHeight(40);
        loginButton.setOnAction(event -> {
        });
        loginButton.setStyle(
                "-fx-background-color: linear-gradient(to right, #1d4ed8, #2563eb);" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 14px;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 10;" +
                "-fx-cursor: hand;"
        );

        // same thing here just styling for now no backend stuff
        Button signUpButton = new Button("Sign Up");
        signUpButton.setPrefWidth(140);
        signUpButton.setPrefHeight(40);
        signUpButton.setOnAction(event -> {
        });
        signUpButton.setStyle(
                "-fx-background-color: transparent;" +
                "-fx-text-fill: #dce9ff;" +
                "-fx-font-size: 14px;" +
                "-fx-font-weight: bold;" +
                "-fx-border-color: #4b6b97;" +
                "-fx-border-width: 1.2;" +
                "-fx-border-radius: 10;" +
                "-fx-background-radius: 10;" +
                "-fx-cursor: hand;"
        );

        HBox buttonRow = new HBox(14, loginButton, signUpButton);
        buttonRow.setAlignment(Pos.CENTER);

        // these spacers help keep the card feeling centered even when resized
        Region spacerTop = new Region();
        Region spacerBottom = new Region();
        VBox.setVgrow(spacerTop, Priority.ALWAYS);
        VBox.setVgrow(spacerBottom, Priority.ALWAYS);

        VBox content = new VBox(18, spacerTop, logo, title, subtitle, buttonRow, spacerBottom);
        content.setAlignment(Pos.CENTER);
        content.setMaxWidth(520);
        content.setPadding(new Insets(36));
        content.setStyle(
                "-fx-background-color: rgba(5, 11, 24, 0.35);" +
                "-fx-background-radius: 28;"
        );

        return content;
    }
}
