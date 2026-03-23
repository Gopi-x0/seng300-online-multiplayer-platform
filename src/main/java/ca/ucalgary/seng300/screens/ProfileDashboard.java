package ca.ucalgary.seng300.screens;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

import javafx.scene.layout.VBox;  // VBox is a vertical layout container from JavaFX
//
/**
 * ProfileDashboard represents the profile screen where player stats are shown.
 * This class follows the UI navigation UML design.
 */
public class ProfileDashboard extends VBox {

    public ProfileDashboard() {
        // Basic container setup
        // Sets vertical spacing between major UI sections
        setSpacing(24);
        // Adds padding around the entire dashboard container
        setPadding(new Insets(40));
        // Sets dark background color for the profile screen
        setStyle("-fx-background-color: #020b1c;");
        // Adds the profile header section to the screen
        getChildren().add(createProfileHeader());
        // Adds the player stat cards section
        getChildren().add(createStatCards());
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
        Label rank = new Label("\uD83C\uDFC6 Rank #"); // Creates label for player rank
        rank.setStyle("-fx-text-fill:#FFD700; -fx-font-size:20px;");//styles rank text

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
     * Creates the statistics card section displaying player stats.
     *
     * @return GridPane containing stat cards
     */
    private HBox createStatCards() {

        // Creates a horizontal layout for player statistics
        HBox row = new HBox();

        // Sets spacing between stat cards
        row.setSpacing(18);

        // Creates the individual stat cards
        VBox totalGamesCard = createStatCard("\uD83C\uDFAE Total Games", "#");
        VBox totalWinsCard = createStatCard("\uD83C\uDFAF Total Wins", "#");
        VBox winRateCard = createStatCard("\uD83E\uDD47 Win Rate", "%");
        VBox bestStreakCard = createStatCard("\uD83D\uDD25 Best Streak", "#");

        // Allows each stat card to grow equally across the row
        HBox.setHgrow(totalGamesCard, Priority.ALWAYS);
        HBox.setHgrow(totalWinsCard, Priority.ALWAYS);
        HBox.setHgrow(winRateCard, Priority.ALWAYS);
        HBox.setHgrow(bestStreakCard, Priority.ALWAYS);

        // Adds the cards to the row
        row.getChildren().addAll(totalGamesCard, totalWinsCard, winRateCard, bestStreakCard);

        return row;
    }
    /**
     * Creates a reusable stat card UI component.
     *
     * @param label the title of the stat
     * @param value the stat value
     * @return VBox representing the stat card
     */
    private VBox createStatCard(String label, String value) {
        // Creates label for stat title and styles it
        Label title = new Label(label);
        title.setStyle("-fx-text-fill:#90a1b9;");

        // Creates label for stat value and styles it
        Label val = new Label(value);
        //only make win stat green and rest white
        if(label.equals("\uD83C\uDFAF Total Wins")){
            val.setStyle("-fx-text-fill:#00e676; -fx-font-size:22px;");
        }else{
            val.setStyle("-fx-text-fill:white; -fx-font-size:22px;");
        }

        // Creates vertical layout container for the stat card
        VBox card = new VBox(title, val);

        // Adds spacing and padding
        card.setSpacing(8);
        card.setPadding(new Insets(20));
        card.setMaxWidth(Double.MAX_VALUE);
        card.setPrefHeight(120);
        // Apply background gradient and rounded corners
        card.setStyle(
                "-fx-background-color: linear-gradient(#0f172b,#1d293d);" +
                        "-fx-background-radius:10;"
        );

        return card;
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
