package ca.ucalgary.seng300.screens;

/**
 * This is the instructions screen for Connect 4, this prompts/plays beforehand
 * for users to familiarize themselves with the game.
 *
 * Pressing the PLAY button triggers the onPlay callback,
 * which MainApp uses to navigate to the GameboardC4 screen.
 */

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.animation.ScaleTransition;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Duration;

public class InstructionsC4 extends VBox {

    private final Runnable onPlay;
    private final Runnable onBack;

    // Stores the callbacks and builds the instructions UI
    public InstructionsC4(Runnable onPlay, Runnable onBack) {
        this.onPlay = onPlay;
        this.onBack = onBack;
        buildUI();
    }

    // Lays out the title, rule cards, and play button
    private void buildUI() {
        setSpacing(10);
        setPadding(new Insets(40));
        setAlignment(Pos.CENTER);
        setStyle("-fx-background-color: #020b1c;");

        HBox topBar = createTopBar();

        Label title = new Label("Connect 4");
        title.setStyle("-fx-text-fill : white; -fx-font-size: 48px; -fx-font-weight: bold;");

        Label subTitle = new Label("Instructions & Rules");
        subTitle.setStyle("-fx-text-fill : white; -fx-font-size: 32px;");

        VBox titleBox = new VBox(title, subTitle);
        titleBox.setAlignment(Pos.CENTER);
        titleBox.setSpacing(4);

        VBox rulesCard = createCard("Rules",
                "•  Each player takes turns dropping one disc at a time into the grid.",
                "•  Discs fall to the lowest available position in the chosen column.",
                "•  You may only place a disc in a column that is not full."
        );

        VBox objectiveCard = createCard("Objective",
                "• Be the first to connect FOUR of your tiles in a row.",
                "• Connections can be horizontal, vertical, or diagonal.",
                "• If there are no four consecutive connections by either sides, then the game ends in a draw."
        );

        VBox playCard = createCard("Play",
                " 1. Click on a column to drop your tile.",
                " 2. Your disc will fall to the lowest open spot.",
                " 3. Wait for your opponent to play their turn."
        );

        // Play button styling and hover/press animations
        Button playButton = new Button("PLAY");
        String playBase =
                "-fx-background-color: linear-gradient(to right, #1d4ed8, #2563eb);" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 16px;" +
                "-fx-font-weight: bold;" +
                "-fx-padding: 12 40;" +
                "-fx-background-radius: 10;" +
                "-fx-cursor: hand;";
        String playHover =
                "-fx-background-color: linear-gradient(to right, #2563eb, #3b82f6);" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 16px;" +
                "-fx-font-weight: bold;" +
                "-fx-padding: 12 40;" +
                "-fx-background-radius: 10;" +
                "-fx-cursor: hand;";
        playButton.setStyle(playBase);
        playButton.setOnMouseEntered(e -> {
            playButton.setStyle(playHover);
            ScaleTransition st = new ScaleTransition(Duration.millis(150), playButton);
            st.setToX(1.05); st.setToY(1.05); st.play();
        });
        playButton.setOnMouseExited(e -> {
            playButton.setStyle(playBase);
            ScaleTransition st = new ScaleTransition(Duration.millis(150), playButton);
            st.setToX(1.0); st.setToY(1.0); st.play();
        });
        playButton.setOnMousePressed(e -> {
            ScaleTransition st = new ScaleTransition(Duration.millis(80), playButton);
            st.setToX(0.96); st.setToY(0.96); st.play();
        });
        playButton.setOnMouseReleased(e -> {
            ScaleTransition st = new ScaleTransition(Duration.millis(80), playButton);
            st.setToX(1.05); st.setToY(1.05); st.play();
        });

        playButton.setOnAction(e -> {
            if (onPlay != null) onPlay.run();
        });

        HBox buttonBox = new HBox(playButton);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(10, 0, 0, 0));

        getChildren().addAll(topBar, titleBox, rulesCard, objectiveCard, playCard, buttonBox);
    }

    // Creates the back button bar at the top
    private HBox createTopBar() {
        // Button style variants for normal, hover, and pressed states
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
        back.setOnAction(e -> {
            if (onBack != null) onBack.run();
        });

        HBox bar = new HBox(back);
        bar.setAlignment(Pos.CENTER_LEFT);
        return bar;
    }

    // Builds a styled card with a title and bullet-point lines
    private VBox createCard(String title, String... lines) {
        Label mainTitle = new Label(title);
        mainTitle.setStyle("-fx-text-fill: #00d3f3; -fx-font-size: 18px; -fx-font-weight: bold;");

        VBox card = new VBox();
        card.setSpacing(8);
        card.setPadding(new Insets(20));
        card.setStyle("-fx-background-color: linear-gradient(to bottom, rgba(34,54,107,0.92), rgba(10,20,40,0.96));" + "-fx-background-radius: 10;");
        card.getChildren().add(mainTitle);

        for (String line : lines) {
            Label subtext = new Label(line);
            subtext.setStyle("-fx-text-fill: #c8d6e5; -fx-font-size: 14px;");
            subtext.setWrapText(true);
            card.getChildren().add(subtext);
        }

        card.setOnMouseEntered(e -> card.setEffect(new DropShadow(20, Color.web("#00c8ff", 0.5))));
        card.setOnMouseExited(e -> card.setEffect(null));

        return card;
    }
}
