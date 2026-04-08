package ca.ucalgary.seng300.screens;
import ca.ucalgary.seng300.model.GameType;
import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;

/**
 * WinScreen is a universal win/lose/draw overlay that displays on top
 * of the actual gameboard (which stays visible but dimmed behind).
 *
 * Layer architecture (bottom to top):
 *   Layer 0: Semi-transparent dark scrim (dims the gameboard behind)
 *   Layer 1: Centered navy result card with icon, message, and RETURN button
 *
 * Usage:
 *   WinScreen.showWin(rootStackPane, onDismiss, GameType.C4);
 *   WinScreen.showLose(rootStackPane, onDismiss, GameType.TTT);
 *   WinScreen.showDraw(rootStackPane, onDismiss, GameType.C4);
 *
 * @author Muhammad Ahmad
 * @email muham.ahmad@ucalgary.ca
 */
public class WinScreen extends StackPane {

    public enum ResultType { WIN, LOSS, DRAW }

    private final ResultType resultType;
    private final Runnable onDismiss;

    // Constructor - takes result type, game type, and a callback for when the user dismisses
    // @author Muhammad Ahmad
    public WinScreen(ResultType resultType, GameType gameType, Runnable onDismiss) {
        this.resultType = resultType;
        this.onDismiss = onDismiss;
        initializeOverlay();
    }

    // Fades in the overlay so the result is shown to the player
    // @author Muhammad Ahmad
    public void displayWinner() {
        FadeTransition fadeIn = new FadeTransition(Duration.millis(300), this);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.play();
    }

    // Sets up the scrim background and the result card on top of it
    // @author Muhammad Ahmad
    private void initializeOverlay() {
        setAlignment(Pos.CENTER);

        Pane scrim = new Pane();
        scrim.setStyle("-fx-background-color: rgba(0, 0, 0, 0.62);");
        scrim.setMaxWidth(Double.MAX_VALUE);
        scrim.setMaxHeight(Double.MAX_VALUE);

        VBox card = buildResultCard();

        getChildren().addAll(scrim, card);
        setOpacity(0);
    }

    // Builds the result card with icon, win/lose/draw message, and return button
    // @author Muhammad Ahmad
    private VBox buildResultCard() {
        String icon;
        String message;
        Color iconColor;

        switch (resultType) {
            case WIN:
                icon = "\uD83D\uDC51";
                message = "You\nWin!";
                iconColor = Color.web("#FFD700");
                break;
            case LOSS:
                icon = "\uD83D\uDE1E";
                message = "You\nLost!";
                iconColor = Color.web("#e7000b");
                break;
            case DRAW:
            default:
                icon = "\uD83E\uDD1D";
                message = "Game was\na Draw!";
                iconColor = Color.web("#00c8ff");
                break;
        }

        Label iconLabel = new Label(icon);
        iconLabel.setFont(Font.font("Arial", FontWeight.BOLD, 60));
        iconLabel.setTextFill(iconColor);
        iconLabel.setAlignment(Pos.CENTER);

        Label resultText = new Label(message);
        resultText.setTextFill(Color.WHITE);
        resultText.setFont(Font.font("Arial", FontWeight.BOLD, 40));
        resultText.setAlignment(Pos.CENTER);
        resultText.setStyle("-fx-text-alignment: center;");

        Button returnBtn = buildReturnButton();

        VBox card = new VBox(22, iconLabel, resultText, returnBtn);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(46, 60, 46, 60));

        double cardWidth = (resultType == ResultType.DRAW) ? 380 : 300;
        card.setMinWidth(cardWidth);
        card.setMaxWidth(cardWidth);
        card.setMinHeight(400);
        card.setMaxHeight(400);

        // Card styling - dark navy gradient with border
        card.setStyle(
                "-fx-background-color: linear-gradient(to bottom, #0d1f40, #162d55);" +
                "-fx-background-radius: 18;" +
                "-fx-border-color: rgba(80, 140, 220, 0.55);" +
                "-fx-border-radius: 18;" +
                "-fx-border-width: 1.5;"
        );

        // Pop-in scale animation when the card appears
        ScaleTransition pop = new ScaleTransition(Duration.millis(300), card);
        pop.setFromX(0.78);
        pop.setFromY(0.78);
        pop.setToX(1.0);
        pop.setToY(1.0);
        pop.play();

        return card;
    }

    // Builds the red RETURN button with hover and press animations
    // @author Muhammad Ahmad
    private Button buildReturnButton() {
        Button btn = new Button("RETURN");
        btn.setPrefWidth(188);
        btn.setPrefHeight(44);

        // Return button styling - base, hover, pressed states
        String base =
                "-fx-background-color: #e7000b;" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 15px;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 8;" +
                "-fx-cursor: hand;";
        String hover =
                "-fx-background-color: #ff1a1a;" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 15px;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 8;" +
                "-fx-cursor: hand;";
        String press =
                "-fx-background-color: #b80000;" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 15px;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 8;" +
                "-fx-cursor: hand;";

        btn.setStyle(base);
        btn.setOnMouseEntered(e -> {
            btn.setStyle(hover);
            ScaleTransition st = new ScaleTransition(Duration.millis(150), btn);
            st.setToX(1.05); st.setToY(1.05); st.play();
        });
        btn.setOnMouseExited(e -> {
            btn.setStyle(base);
            ScaleTransition st = new ScaleTransition(Duration.millis(150), btn);
            st.setToX(1.0); st.setToY(1.0); st.play();
        });
        btn.setOnMousePressed(e -> {
            btn.setStyle(press);
            ScaleTransition st = new ScaleTransition(Duration.millis(80), btn);
            st.setToX(0.96); st.setToY(0.96); st.play();
        });
        btn.setOnMouseReleased(e -> {
            btn.setStyle(hover);
            ScaleTransition st = new ScaleTransition(Duration.millis(80), btn);
            st.setToX(1.05); st.setToY(1.05); st.play();
        });
        btn.setOnAction(e -> dismiss());
        return btn;
    }

    // Fades out the overlay, removes it from the parent, and runs the dismiss callback
    // @author Muhammad Ahmad
    private void dismiss() {
        FadeTransition fadeOut = new FadeTransition(Duration.millis(220), this);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);
        fadeOut.setOnFinished(e -> {
            if (getParent() instanceof Pane parent) {
                parent.getChildren().remove(this);
            }
            if (onDismiss != null) {
                onDismiss.run();
            }
        });
        fadeOut.play();
    }

    // Static helper methods

    // Shows a WIN overlay on the given parent pane
    // @author Muhammad Ahmad
    public static void showWin(StackPane parentPane, Runnable onDismiss, GameType gameType) {
        WinScreen overlay = new WinScreen(ResultType.WIN, gameType, onDismiss);
        parentPane.getChildren().add(overlay);
        overlay.displayWinner();
    }

    // Shows a LOSE overlay on the given parent pane
    // @author Muhammad Ahmad
    public static void showLose(StackPane parentPane, Runnable onDismiss, GameType gameType) {
        WinScreen overlay = new WinScreen(ResultType.LOSS, gameType, onDismiss);
        parentPane.getChildren().add(overlay);
        overlay.displayWinner();
    }

    // Shows a DRAW overlay on the given parent pane
    // @author Muhammad Ahmad
    public static void showDraw(StackPane parentPane, Runnable onDismiss, GameType gameType) {
        WinScreen overlay = new WinScreen(ResultType.DRAW, gameType, onDismiss);
        parentPane.getChildren().add(overlay);
        overlay.displayWinner();
    }

}
