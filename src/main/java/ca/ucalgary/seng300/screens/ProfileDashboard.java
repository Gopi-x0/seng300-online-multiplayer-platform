package ca.ucalgary.seng300.screens;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

import javafx.scene.layout.VBox;  // VBox is a vertical layout container from JavaFX

/**
 * ProfileDashboard represents the profile screen where player stats are shown.
 * This class follows the UI navigation UML design.
 */
public class ProfileDashboard extends VBox {

    public ProfileDashboard() {
        // Basic container setup
        setSpacing(20);
        setStyle("-fx-background-color: #0a0f1e;");
        setSpacing(20);  // Sets vertical spacing between components in the layout
        setPadding(new Insets(30)); // Adds padding around the entire screen content
        setStyle("-fx-background-color: #0a0f1e;");// Sets the dark theme background color for the profile page
        getChildren().add(createProfileHeader()); // Adds the profile header section to the screen
    }

    /**
     * Displays the profile view.
     * This method will later render the full profile UI.
     */
    public void displayProfileView() {
        // UI rendering logic will go here
    }

    /**
     * Displays statistics for a specific game type.
     *
     * @param type the game type (e.g., TicTacToe, Connect4)
     */
    public void displayGameStats(String type) {
        // Game-specific stats logic will go here
    }
}