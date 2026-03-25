package ca.ucalgary.seng300.screens;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.Region;

//
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
        setFillWidth(true);
        //adds profile and back buttons on top of the screen
        getChildren().add(createTopBar());
        // Adds the profile header section to the screen
        getChildren().add(createProfileHeader());
        // Adds the player stat cards section
        getChildren().add(createStatCards());
        //adds game card stats sections
        getChildren().add(createGameCards());
    }

    //creates buttons for back to home and profile
    private HBox createTopBar() {

        //styles for base hover and pressed
        String baseStyle =
                "-fx-background-color: transparent;" +
                        "-fx-text-fill: #00c8ff;" +
                        "-fx-font-size: 14px;" +
                        "-fx-cursor: hand;";

        String hoverStyle =
                "-fx-background-color: rgba(0,200,255,0.1);" +
                        "-fx-text-fill: #33d6ff;" +
                        "-fx-font-size: 14px;" +
                        "-fx-cursor: hand;";

        String pressedStyle =
                "-fx-background-color: rgba(0,200,255,0.2);" +
                        "-fx-text-fill: #66e0ff;" +
                        "-fx-font-size: 14px;" +
                        "-fx-cursor: hand;";

        //makes buttons
        Button back = new Button("← Back to Home");
        Button profile = new Button("Profile");

        //apply base style
        back.setStyle(baseStyle);
        profile.setStyle(baseStyle);

        //interactions for the back button
        back.setOnMouseEntered(e -> back.setStyle(hoverStyle));
        back.setOnMouseExited(e -> back.setStyle(baseStyle));
        back.setOnMousePressed(e -> back.setStyle(pressedStyle));
        back.setOnMouseReleased(e -> back.setStyle(hoverStyle));

        //interaction for profile button
        profile.setOnMouseEntered(e -> profile.setStyle(hoverStyle));
        profile.setOnMouseExited(e -> profile.setStyle(baseStyle));
        profile.setOnMousePressed(e -> profile.setStyle(pressedStyle));
        profile.setOnMouseReleased(e -> profile.setStyle(hoverStyle));

        //button actions, will implement real actions later
        back.setOnAction(e -> System.out.println("Go to Home"));
        profile.setOnAction(e -> System.out.println("Go to Profile"));

        //positions back to home and profile correctly
        HBox bar = new HBox();
        bar.setAlignment(Pos.CENTER_LEFT);
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        bar.getChildren().addAll(back, spacer, profile);

        return bar;
    }

    private HBox createProfileHeader() {
        // Creates label displaying the player's name
        Label name = new Label("Name Name");
        name.setStyle("-fx-text-fill:white; -fx-font-size:32px; -fx-font-weight:bold;");// Applies styling to the name text

        Label member = new Label("Member since Month Year"); // Creates label showing membership information
        member.setStyle("-fx-text-fill:#90a1b9;");// Applies styling for secondary information text

        //creates a vertical container holding name and membership info
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

    private HBox createGameCards() {
        //game card for Tic Tac Toe
        VBox ticTacToe = createGameCard(
                "Tic Tac Toe",
                0, 0, 0, 0,
                "0%", "0 wins", "0 wins", "0"
        );

        //game card for Connect 4
        VBox connect4 = createGameCard(
                "Connect 4",
                0, 0, 0, 0,
                "10%", "0 wins", "0 wins", "0"
        );
        //container for both game cards
        HBox container = new HBox(24, ticTacToe, connect4);
        container.setAlignment(Pos.CENTER_LEFT);
        container.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(ticTacToe, Priority.ALWAYS);
        HBox.setHgrow(connect4, Priority.ALWAYS);
        return container;
    }


    private VBox createGameCard(
            String title,
            int games,
            int wins,
            int losses,
            int draws,
            String winRate,
            String currentStreak,
            String bestStreak,
            String points
    ) {

        // Created label displaying game title and styles it
        Label t = new Label(title);
        t.setStyle("-fx-text-fill:white; -fx-font-size:18px; -fx-font-weight:bold;");

        /*
         * LEFT COLUMN (labels)
         * This holds the names of each stat (Games Played, Wins, etc.)
         * Styled with muted color to match Figma hierarchy
         */        VBox labelsLeft = new VBox(
                statLabel("Games Played"),
                statLabel("Wins"),
                statLabel("Losses"),
                statLabel("Draws")
        );
        labelsLeft.setSpacing(8);

        /*
         * RIGHT COLUMN (values)
         * This holds the actual numeric values corresponding to each stat
         * Each stat has its own color (green = wins, red = losses, etc.)
         */        VBox valuesRight = new VBox(
                statValue(String.valueOf(games), "#90a1b9"),
                statValue(String.valueOf(wins), "#00e676"),
                statValue(String.valueOf(losses), "#ff5252"),
                statValue(String.valueOf(draws), "#90a1b9")
        );
        valuesRight.setSpacing(8);
        valuesRight.setAlignment(Pos.CENTER_RIGHT); // Align values to the right edge

        //Combines left and right columns into a single row
        //creates the clean "label → value" layout like the Figma design
        HBox statsRow = new HBox(labelsLeft, valuesRight);
        statsRow.setSpacing(20);
        HBox.setHgrow(labelsLeft, Priority.ALWAYS); // Push values to the far right

        // Check if winRate is empty and set default value
        double winRateValue = 0;
        try {
            winRateValue = Double.parseDouble(winRate.replace("%", "")) / 100.0;
        } catch (Exception e) {
            winRateValue = 0;
        }

        //win rate label
        Label wrLabel = statLabel("Win Rate");

        //make a progress bar for win rate
        ProgressBar winRateProgress = new ProgressBar(winRateValue);
        winRateProgress.setPrefWidth(Double.MAX_VALUE);
        winRateProgress.setStyle(
                "-fx-accent: #00c8ff;" +
                        "-fx-control-inner-background: #1d293d;"
        );

        VBox winRateBox = new VBox(wrLabel, winRateProgress);
        winRateBox.setSpacing(6);

        //bottom stats layout
        VBox bottomLeft = new VBox(
                statLabel("Current Streak"),
                statLabel("Best Streak"),
                statLabel("Points Earned")
        );
        bottomLeft.setSpacing(6);

        //BOTTOM SECTION (additional stats)
        //Same structure as above: labels on left, values on right
        VBox bottomRight = new VBox(
                statValue(currentStreak, "#90a1b9"),
                statValue(bestStreak, "#ffeb3b"),
                statValue(points, "#00c8ff")
        );
        bottomRight.setSpacing(6);
        bottomRight.setAlignment(Pos.CENTER_RIGHT);

        // Combine everything into a single game card layout
        HBox bottomRow = new HBox(bottomLeft, bottomRight);
        bottomRow.setSpacing(20);
        HBox.setHgrow(bottomLeft, Priority.ALWAYS);

        /*
         * MAIN CARD LAYOUT
         * Stacks:
         * 1. Title
         * 2. Stats row
         * 3. Win rate section
         * 4. Bottom stats
         */
        VBox gameCard = new VBox(
                t,
                statsRow,
                winRateBox,
                bottomRow
        );

        // Spacing between sections
        gameCard.setSpacing(16);
        gameCard.setPadding(new Insets(20));
        gameCard.setPrefWidth(300);
        gameCard.setMaxWidth(Double.MAX_VALUE);
        VBox.setVgrow(gameCard, Priority.NEVER);
        //add gradient styling
        gameCard.setStyle(
                "-fx-background-color: linear-gradient(#0f172b,#1d293d);" +
                        "-fx-background-radius:12;" +
                        "-fx-border-radius:12;"
        );

        return gameCard;
    }

    /**
     * Creates a styled label for stat titles (left column).
     *
     * @param text The label name (e.g., "Wins", "Losses")
     * @return Styled Label for UI consistency
     */    private Label statLabel(String text) {
        Label l = new Label(text);
        // Muted gray color for hierarchy
        l.setStyle("-fx-text-fill:#90a1b9; -fx-font-size:13px;");
        return l;
    }

    /**
     * Creates a styled label for stat values (right column).
     *
     * @param value The numeric/stat value
     * @param color The color used to highlight the stat
     * @return Styled Label with emphasis
     */
    private Label statValue(String value, String color) {
        Label l = new Label(value);
        l.setStyle("-fx-text-fill:" + color + "; -fx-font-size:14px; -fx-font-weight:bold;");

        return l;
    }

    private Label stat(String label, String value) {

        //a label combining stat title and value and applies styling to text
        Label stat = new Label(label + ": " + value);
        stat.setStyle("-fx-text-fill:#90a1b9;");
        return stat;
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
//