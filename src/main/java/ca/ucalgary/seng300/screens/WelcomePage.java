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

import java.util.List;

public class WelcomePage extends StackPane {

    public WelcomePage() {
        // just calling the main layout stuff here so constructor stays simple
        initializeLayout();
    }

    private void initializeLayout() {
        // little bit of padding so the screen doesnt feel jammed to the edges
        setPadding(new Insets(32));
        setStyle(
                "-fx-background-color: #081221;" +
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

        VBox textGroup = new VBox(4, title, subtitle);
        textGroup.setAlignment(Pos.CENTER);
        textGroup.setTranslateY(-12);

        VBox headerGroup = new VBox(-28, logo, textGroup);
        headerGroup.setAlignment(Pos.CENTER);

        VBox content = new VBox(18, spacerTop, headerGroup, buttonRow, spacerBottom);
        content.setAlignment(Pos.CENTER);
        styleMainContent(content);

        return content;
    }

    private Label buildTitle() {
        // keeping the heading seperate so its easier to tweak later
        Label title = new Label("WELCOME TO");
        title.setTextFill(Color.web("#86b9ff"));
        title.setFont(resolveHeadingFont(FontWeight.BOLD, 30));
        title.setTranslateY(-4);
        return title;
    }

    private Label buildSubtitle() {
        // smaller text under the welcome heading
        Label subtitle = new Label("Student Gaming Platform");
        subtitle.setTextFill(Color.web("#d8e7ff"));
        subtitle.setFont(resolveBodyFont(FontWeight.MEDIUM, 14));
        subtitle.setTranslateY(-2);
        return subtitle;
    }

    private void styleMainContent(VBox content) {
        // main card in the middle of the welcome page
        content.setMaxWidth(560);
        content.setPadding(new Insets(22, 40, 34, 40));
        content.setStyle(
                "-fx-background-color: #081221;" +
                "-fx-background-radius: 30;"
        );
    }

    private HBox buildButtonRow() {
        // putting both buttons in one row so they stay centered together
        Button loginButton = createLoginButton();
        Button signUpButton = createSignUpButton();

        HBox buttonRow = new HBox(10, loginButton, signUpButton);
        buttonRow.setAlignment(Pos.CENTER);
        return buttonRow;
    }

    private ImageView buildLogo() {
        // using the real logo now and still keeping a safe fallback size
        Image image = new Image(getClass().getResourceAsStream("/ca/ucalgary/seng300/images/sgp-logo.png"));
        ImageView logo = new ImageView(image);
        logo.setPreserveRatio(true);
        logo.setFitWidth(500);
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
        loginButton.setFont(resolveBodyFont(FontWeight.SEMI_BOLD, 14));
        String defaultStyle =
                "-fx-background-color: linear-gradient(to right, #1d4ed8, #2563eb);" +
                "-fx-text-fill: white;" +
                "-fx-background-radius: 10;" +
                "-fx-cursor: hand;";
        String hoverStyle =
                "-fx-background-color: linear-gradient(to right, #2563eb, #3b82f6);" +
                "-fx-text-fill: white;" +
                "-fx-background-radius: 10;" +
                "-fx-cursor: hand;";
        loginButton.setStyle(defaultStyle);
        applyHoverStyle(loginButton, defaultStyle, hoverStyle);
        return loginButton;
    }

    private Button createSignUpButton() {
        // same thing here just styling for now no backend stuff
        Button signUpButton = new Button("Sign Up");
        signUpButton.setPrefWidth(140);
        signUpButton.setPrefHeight(40);
        signUpButton.setOnAction(event -> {
        });
        signUpButton.setFont(resolveBodyFont(FontWeight.SEMI_BOLD, 14));
        String defaultStyle =
                "-fx-background-color: transparent;" +
                "-fx-text-fill: #dce9ff;" +
                "-fx-border-color: #4b6b97;" +
                "-fx-border-width: 1.2;" +
                "-fx-border-radius: 10;" +
                "-fx-background-radius: 10;" +
                "-fx-cursor: hand;";
        String hoverStyle =
                "-fx-background-color: rgba(32, 58, 95, 0.45);" +
                "-fx-text-fill: white;" +
                "-fx-border-color: #6f9ad1;" +
                "-fx-border-width: 1.2;" +
                "-fx-border-radius: 10;" +
                "-fx-background-radius: 10;" +
                "-fx-cursor: hand;";
        signUpButton.setStyle(defaultStyle);
        applyHoverStyle(signUpButton, defaultStyle, hoverStyle);
        return signUpButton;
    }

    private void applyHoverStyle(Button button, String defaultStyle, String hoverStyle) {
        // small hover effect so the buttons feel a bit more alive
        button.setOnMouseEntered(event -> button.setStyle(hoverStyle));
        button.setOnMouseExited(event -> button.setStyle(defaultStyle));
    }

    private Font resolveHeadingFont(FontWeight weight, double size) {
        List<String> families = Font.getFamilies();
        if (families.contains("Rajdhani")) {
            return Font.font("Rajdhani", weight, size);
        }
        return Font.font("Trebuchet MS", weight, size);
    }

    private Font resolveBodyFont(FontWeight weight, double size) {
        List<String> families = Font.getFamilies();
        if (families.contains("Inter")) {
            return Font.font("Inter", weight, size);
        }
        return Font.font("Verdana", weight, size);
    }
}
