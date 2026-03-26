package ca.ucalgary.seng300.screens;

import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;

/**
 * WinScreen is a universal win/lose overlay for both TicTacToe and Connect 4.
 *
 * Layer architecture (bottom to top):
 *   Layer 0: Enlarged game board preview (TTT grid or C4 circles) - blurred
 *   Layer 1: Semi-transparent dark scrim over the blur
 *   Layer 2: Centered navy result card - completely separate, never blurred
 *
 * Usage:
 *   WinScreen.showWin(rootStackPane, this::showHomePage);
 *   WinScreen.showWin(rootStackPane, this::showHomePage, WinScreen.GameType.C4);
 *   WinScreen.showLose(rootStackPane, this::showHomePage);
 *   WinScreen.showLose(rootStackPane, this::showHomePage, WinScreen.GameType.C4);
 *
 * @author Muhammad Ahmad
 * @email muham.ahmad@ucalgary.ca
 */
public class WinScreen extends StackPane {

    public enum GameType {TTT, C4}

    private final boolean isWin;
    private final GameType gameType;
    private final Runnable onDismiss;

    public WinScreen(boolean isWin, GameType gameType, Runnable onDismiss) {
        this.isWin = isWin;
        this.gameType = gameType;
        this.onDismiss = onDismiss;
        initializeOverlay();
    }

    // UML Method: displayWinner()

    /**
     * Triggers the overlay fade-in animation.
     * Called from PlayGame.displayWinner() per UML contract (Diagram 04).
     *
     * @author Muhammad Ahmad
     * @email muham.ahmad@ucalgary.ca
     */
    public void displayWinner() {
        FadeTransition fadeIn = new FadeTransition(Duration.millis(300), this);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.play();
    }

    // Overlay construction - three clearly separated layers

    private void initializeOverlay() {
        setAlignment(Pos.CENTER);

        // Layer 0: Blurred background preview
        Region bgPreview = gameType == GameType.TTT
                ? buildTTTBackground()
                : buildC4Background();
        bgPreview.setEffect(new GaussianBlur(12));
        bgPreview.setMaxWidth(Double.MAX_VALUE);
        bgPreview.setMaxHeight(Double.MAX_VALUE);

        // Layer 1: Dark scrim
        Pane scrim = new Pane();
        scrim.setStyle("-fx-background-color: rgba(0, 0, 0, 0.58);");
        scrim.setMaxWidth(Double.MAX_VALUE);
        scrim.setMaxHeight(Double.MAX_VALUE);

        // Layer 2: Result card - never blurred
        VBox card = buildResultCard();

        getChildren().addAll(bgPreview, scrim, card);
        setOpacity(0);
    }

    // Layer 0: Backgrounds

    private Region buildTTTBackground() {
        javafx.scene.layout.GridPane board = new javafx.scene.layout.GridPane();
        board.setHgap(14);
        board.setVgap(14);
        board.setAlignment(Pos.CENTER);
        board.setStyle("-fx-background-color: #020b1c;");
        board.setMaxWidth(Double.MAX_VALUE);
        board.setMaxHeight(Double.MAX_VALUE);
        board.setPadding(new Insets(50));

        String[][] marks = {
                {"X", "O", ""},
                {"X", "O", ""},
                {"X", "",  "O"}
        };

        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                StackPane cell = new StackPane();
                cell.setPrefSize(160, 160);
                cell.setStyle(
                        "-fx-background-color: rgba(9,25,48,0.98);" +
                                "-fx-border-color: #39cfff;" +
                                "-fx-border-radius: 8;" +
                                "-fx-background-radius: 8;"
                );
                Label mark = new Label(marks[r][c]);
                mark.setTextFill(Color.web("#38cfff"));
                mark.setFont(Font.font("Arial", FontWeight.BOLD, 72));
                cell.getChildren().add(mark);
                board.add(cell, c, r);
            }
        }
        return board;
    }

    private Region () {
        javafx.scene.layout.GridPane board = new javafx.scene.layout.GridPane();
        board.setHgap(18);
        board.setVgap(18);
        board.setAlignment(Pos.CENTER);
        board.setStyle("-fx-background-color: #020b1c;");
        board.setMaxWidth(Double.MAX_VALUE);
        board.setMaxHeight(Double.MAX_VALUE);
        board.setPadding(new Insets(50));

        Color[][] tokens = {
                {Color.TRANSPARENT,   Color.web("#ff4b4b"), Color.web("#ffd93d"), Color.TRANSPARENT,   Color.web("#4ea1ff")},
                {Color.web("#ffd93d"), Color.TRANSPARENT,   Color.web("#ff4b4b"), Color.web("#4ea1ff"), Color.TRANSPARENT  },
                {Color.web("#ff4b4b"), Color.web("#ffd93d"), Color.TRANSPARENT,   Color.web("#ff4b4b"), Color.web("#ffd93d")},
                {Color.web("#4ea1ff"), Color.web("#ff4b4b"), Color.web("#ffd93d"), Color.TRANSPARENT,   Color.web("#ff4b4b")}
        };

        for (int r = 0; r < tokens.length; r++) {
            for (int c = 0; c < tokens[r].length; c++) {
                Circle token = new Circle(46);
                token.setFill(tokens[r][c]);
                token.setStroke(Color.web("#31c6ff"));
                token.setStrokeWidth(2);
                board.add(token, c, r);
            }
        }
        return board;
    }


}