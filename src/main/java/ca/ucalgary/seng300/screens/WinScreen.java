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


}