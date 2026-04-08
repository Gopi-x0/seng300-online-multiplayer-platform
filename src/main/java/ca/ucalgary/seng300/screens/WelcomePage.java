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
import javafx.animation.ScaleTransition;
import javafx.util.Duration;

import java.util.List;

// Landing page shown when the app first opens
public class WelcomePage extends StackPane {

    private final Runnable onContinue;

    // Stores the continue callback and builds the welcome layout
    public WelcomePage(Runnable onContinue) {
        this.onContinue = onContinue;
        initializeLayout();
    }

    // Sets padding, background, and adds the centered content
    private void initializeLayout() {
        setPadding(new Insets(32));
        setStyle(
                "-fx-background-color: #081221;" +
                "-fx-background-insets: 0;"
        );

        VBox centeredContent = buildCenteredContent();
        centeredContent.maxWidthProperty().bind(widthProperty().multiply(0.6));

        getChildren().setAll(centeredContent);
    }

    // Assembles the logo, title, subtitle, and continue button in a vertical layout
    private VBox buildCenteredContent() {
        ImageView logo = buildLogo();
        Label title = buildTitle();
        Label subtitle = buildSubtitle();
        Button continueBtn = buildContinueButton();

        Region spacerTop = new Region();
        Region spacerBottom = new Region();
        VBox.setVgrow(spacerTop, Priority.ALWAYS);
        VBox.setVgrow(spacerBottom, Priority.ALWAYS);

        VBox textGroup = new VBox(4, title, subtitle);
        textGroup.setAlignment(Pos.CENTER);
        textGroup.setTranslateY(-12);

        VBox headerGroup = new VBox(-28, logo, textGroup);
        headerGroup.setAlignment(Pos.CENTER);

        HBox buttonRow = new HBox(continueBtn);
        buttonRow.setAlignment(Pos.CENTER);

        VBox content = new VBox(18, spacerTop, headerGroup, buttonRow, spacerBottom);
        content.setAlignment(Pos.CENTER);
        styleMainContent(content);

        return content;
    }

    // Creates the "WELCOME TO" heading label
    private Label buildTitle() {
        Label title = new Label("WELCOME TO");
        title.setTextFill(Color.web("#86b9ff"));
        title.setFont(resolveHeadingFont(FontWeight.BOLD, 30));
        title.setTranslateY(-4);
        return title;
    }

    // Creates the "Student Gaming Platform" subtitle label
    private Label buildSubtitle() {
        Label subtitle = new Label("Student Gaming Platform");
        subtitle.setTextFill(Color.web("#d8e7ff"));
        subtitle.setFont(resolveBodyFont(FontWeight.MEDIUM, 14));
        subtitle.setTranslateY(-2);
        return subtitle;
    }

    // Applies styling to the main content container
    private void styleMainContent(VBox content) {
        content.setMaxWidth(560);
        content.setPadding(new Insets(22, 40, 34, 40));
        content.setStyle(
                "-fx-background-color: #081221;" +
                "-fx-background-radius: 30;"
        );
    }

    // Creates the continue button with hover and press animations
    private Button buildContinueButton() {
        Button btn = new Button("Continue to Play!");
        btn.setPrefWidth(220);
        btn.setPrefHeight(44);
        btn.setFont(resolveBodyFont(FontWeight.SEMI_BOLD, 15));
        btn.setOnAction(event -> {
            if (onContinue != null) onContinue.run();
        });

        // Button style variants for default and hover
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
        btn.setStyle(defaultStyle);
        btn.setOnMouseEntered(event -> {
            btn.setStyle(hoverStyle);
            ScaleTransition st = new ScaleTransition(Duration.millis(150), btn);
            st.setToX(1.04); st.setToY(1.04); st.play();
        });
        btn.setOnMouseExited(event -> {
            btn.setStyle(defaultStyle);
            ScaleTransition st = new ScaleTransition(Duration.millis(150), btn);
            st.setToX(1.0); st.setToY(1.0); st.play();
        });
        btn.setOnMousePressed(event -> {
            ScaleTransition st = new ScaleTransition(Duration.millis(80), btn);
            st.setToX(0.96); st.setToY(0.96); st.play();
        });
        btn.setOnMouseReleased(event -> {
            ScaleTransition st = new ScaleTransition(Duration.millis(80), btn);
            st.setToX(1.04); st.setToY(1.04); st.play();
        });
        return btn;
    }

    // Loads the SGP logo image from resources
    private ImageView buildLogo() {
        ImageView logo = new ImageView();
        java.io.InputStream stream = getClass().getResourceAsStream("/ca/ucalgary/seng300/images/sgp-logo.png");
        if (stream != null) {
            logo.setImage(new Image(stream));
        }
        logo.setPreserveRatio(true);
        logo.setFitWidth(500);
        logo.setSmooth(true);
        return logo;
    }

    // Tries to use Rajdhani font for headings, falls back to Trebuchet MS
    private Font resolveHeadingFont(FontWeight weight, double size) {
        List<String> families = Font.getFamilies();
        if (families.contains("Rajdhani")) {
            return Font.font("Rajdhani", weight, size);
        }
        return Font.font("Trebuchet MS", weight, size);
    }

    // Tries to use Inter font for body text, falls back to Verdana
    private Font resolveBodyFont(FontWeight weight, double size) {
        List<String> families = Font.getFamilies();
        if (families.contains("Inter")) {
            return Font.font("Inter", weight, size);
        }
        return Font.font("Verdana", weight, size);
    }
}
