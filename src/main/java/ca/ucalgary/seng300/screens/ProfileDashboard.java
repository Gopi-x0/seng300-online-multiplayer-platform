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

    private HBox createProfileHeader() {
        // Creates label displaying the player's name
        Label name = new Label("Name Name");
        name.setStyle("-fx-text-fill:white; -fx-font-size:32px; -fx-font-weight:bold;");// Applies styling to the name text

        Label member = new Label("Member since Month Year"); // Creates label showing membership information
        member.setStyle("-fx-text-fill:#90a1b9;");// Applies styling for secondary information text

        // Creates a vertical container holding name and membership info
        VBox left = new VBox(name, member);
        left.setSpacing(5); // Sets spacing between the labels
        Label rank = new Label("Rank #Number"); // Creates label for player rank
        rank.setStyle("-fx-text-fill:white; -fx-font-size:20px;"); // Styles the rank text

        // Creates label for total points
        Label points = new Label("0000 Points");
        points.setStyle("-fx-text-fill:#00d3f3;"); // Applies highlight styling to points

        // Creates vertical layout for rank and points, and deals with allignment and spacing
        VBox right = new VBox(rank, points);
        right.setAlignment(Pos.CENTER_RIGHT);
        HBox header = new HBox(left, right);
        header.setSpacing(40);
        header.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(left, Priority.ALWAYS);
        header.setPadding(new Insets(20));
        header.setStyle(
                "-fx-background-color: linear-gradient(#0f172b,#1d293d);" +
                        "-fx-background-radius:10;"
        );

        // Returns the constructed header
        return header;
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