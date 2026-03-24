package ca.ucalgary.seng300.screens;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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
        // pulling each piece separate so this section stays easier to read
        ImageView logo = buildLogo();
        Label title = buildTitle();
        Label subtitle = buildSubtitle();
        HBox buttonRow = buildButtonRow();

        // these spacers help keep the card feeling centered even when resized
        Region spacerTop = new Region();
        Region spacerBottom = new Region();
        VBox.setVgrow(spacerTop, Priority.ALWAYS);
        VBox.setVgrow(spacerBottom, Priority.ALWAYS);

        VBox content = new VBox(18, spacerTop, logo, title, subtitle, buttonRow, spacerBottom);
        content.setAlignment(Pos.CENTER);
        styleMainContent(content);

        return content;
    }

    private Label buildTitle() {
        // keeping the heading seperate so its easier to tweak later
        Label title = new Label("Welcome to");
        title.setTextFill(Color.web("#5f87b7"));
        title.setFont(Font.font("Arial", FontWeight.NORMAL, 34));
        return title;
    }

    private Label buildSubtitle() {
        // smaller text under the welcome heading
        Label subtitle = new Label("Student Gaming Platform");
        subtitle.setTextFill(Color.web("#6f86a7"));
        subtitle.setFont(Font.font("Arial", 12));
        return subtitle;
    }

    private void styleMainContent(VBox content) {
        // main card in the middle of the welcome page
        content.setMaxWidth(560);
        content.setPadding(new Insets(42, 40, 42, 40));
        content.setStyle(
                "-fx-background-color: linear-gradient(to bottom, rgba(5, 11, 24, 0.62), rgba(3, 9, 20, 0.78));" +
                "-fx-background-radius: 30;" +
                "-fx-border-color: rgba(53, 94, 145, 0.28);" +
                "-fx-border-radius: 30;"
        );
    }

    private HBox buildButtonRow() {
        // putting both buttons in one row so they stay centered together
        Button loginButton = createLoginButton();
        Button signUpButton = createSignUpButton();

        HBox buttonRow = new HBox(14, loginButton, signUpButton);
        buttonRow.setAlignment(Pos.CENTER);
        return buttonRow;
    }

    private ImageView buildLogo() {
        // using the real logo now and still keeping a safe fallback size
        Image image = new Image(getClass().getResourceAsStream("/ca/ucalgary/seng300/images/sgp-logo.png"));
        ImageView logo = new ImageView(image);
        logo.setPreserveRatio(true);
        logo.setFitWidth(300);
        logo.setSmooth(true);
        return logo;
    }

    private Button createLoginButton() {
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
        return loginButton;
    }

    private Button createSignUpButton() {
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
        return signUpButton;
    }
}
