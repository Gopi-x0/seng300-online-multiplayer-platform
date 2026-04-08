package ca.ucalgary.seng300.screens;

import ca.ucalgary.seng300.model.UserData;
import ca.ucalgary.seng300.service.UserManager;
import javafx.animation.ScaleTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.VBox;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * ProfileDashboard displays the current user's profile and game stats.
 * All data is dynamically loaded from UserManager.getCurrentUser().
 */
public class ProfileDashboard extends VBox {

    private final Runnable onBackToHome;

    // Builds the profile page with header, stats, and per-game breakdowns
    public ProfileDashboard(Runnable onBackToHome) {
        this.onBackToHome = onBackToHome;
        setSpacing(24);
        setPadding(new Insets(40));
        setStyle("-fx-background-color: #020b1c;");
        setFillWidth(true);

        getChildren().add(createTopBar());
        getChildren().add(createProfileHeader());
        getChildren().add(createStatCards());
        getChildren().add(createGameCards());
    }

    // Returns the current user, or a guest fallback if not logged in
    private UserData getUser() {
        UserData user = UserManager.getCurrentUser();
        if (user == null) {
            return new UserData("guest@sgp.com", "Guest", "");
        }
        return user;
    }

    // Creates the back button bar at the top of the page
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
            System.out.println("ProfileDashboard: navigating to Home");
            if (onBackToHome != null) onBackToHome.run();
        });

        HBox bar = new HBox();
        bar.setAlignment(Pos.CENTER_LEFT);
        bar.getChildren().add(back);

        return bar;
    }

    // Creates the profile header card showing username, rank, and points
    private HBox createProfileHeader() {
        UserData user = getUser();

        Label name = new Label(user.getUsername());
        name.setStyle("-fx-text-fill:white; -fx-font-size:32px; -fx-font-weight:bold;");
        name.setMaxWidth(400);
        name.setWrapText(false);
        name.setEllipsisString("...");
        name.setTextOverrun(javafx.scene.control.OverrunStyle.ELLIPSIS);

        String memberDate = LocalDate.now().format(DateTimeFormatter.ofPattern("MMMM yyyy"));
        Label member = new Label("Member since " + memberDate);
        member.setStyle("-fx-text-fill:#90a1b9;");

        VBox left = new VBox(name, member);
        left.setSpacing(5);

        int rank = UserManager.getUserRank(user);
        Label rankLabel = new Label("\uD83C\uDFC6 Rank #" + rank);
        rankLabel.setStyle("-fx-text-fill:#FFD700; -fx-font-size:20px;");

        Label points = new Label(user.getTotalPoints() + " Points");
        points.setStyle("-fx-text-fill:#00d3f3;");

        VBox right = new VBox(rankLabel, points);
        right.setAlignment(Pos.CENTER_RIGHT);
        HBox header = new HBox(left, right);
        header.setSpacing(40);
        header.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(left, Priority.ALWAYS);
        header.setPadding(new Insets(20));
        header.setStyle(
                "-fx-background-color: linear-gradient(to bottom, rgba(34,54,107,0.92), rgba(10,20,40,0.96));" +
                "-fx-background-radius:10;"
        );
        header.setOnMouseEntered(e -> header.setEffect(new DropShadow(20, Color.web("#00c8ff", 0.5))));
        header.setOnMouseExited(e -> header.setEffect(null));

        return header;
    }

    // Creates the row of overall stat cards (total games, wins, win rate, streak)
    private HBox createStatCards() {
        UserData user = getUser();

        HBox row = new HBox();
        row.setSpacing(18);

        VBox totalGamesCard = createStatCard("\uD83C\uDFAE Total Games", String.valueOf(user.getTotalGames()), false);
        VBox totalWinsCard = createStatCard("\uD83C\uDFAF Total Wins", String.valueOf(user.getTotalWins()), true);
        VBox winRateCard = createStatCard("\uD83E\uDD47 Win Rate", String.format("%.1f%%", user.getOverallWinRate()), false);
        VBox bestStreakCard = createStatCard("\uD83D\uDD25 Best Streak", String.valueOf(user.getBestStreak()), false);

        HBox.setHgrow(totalGamesCard, Priority.ALWAYS);
        HBox.setHgrow(totalWinsCard, Priority.ALWAYS);
        HBox.setHgrow(winRateCard, Priority.ALWAYS);
        HBox.setHgrow(bestStreakCard, Priority.ALWAYS);

        row.getChildren().addAll(totalGamesCard, totalWinsCard, winRateCard, bestStreakCard);
        return row;
    }

    // Creates a single stat card with a label and value
    private VBox createStatCard(String label, String value, boolean isWins) {
        Label title = new Label(label);
        title.setStyle("-fx-text-fill:#90a1b9;");

        Label val = new Label(value);
        if (isWins) {
            val.setStyle("-fx-text-fill:#00e676; -fx-font-size:22px;");
        } else {
            val.setStyle("-fx-text-fill:white; -fx-font-size:22px;");
        }

        VBox card = new VBox(title, val);
        card.setSpacing(8);
        card.setPadding(new Insets(20));
        card.setMaxWidth(Double.MAX_VALUE);
        card.setPrefHeight(120);
        card.setStyle(
                "-fx-background-color: linear-gradient(to bottom, rgba(34,54,107,0.92), rgba(10,20,40,0.96));" +
                "-fx-background-radius:10;"
        );
        card.setOnMouseEntered(e -> card.setEffect(new DropShadow(20, Color.web("#00c8ff", 0.5))));
        card.setOnMouseExited(e -> card.setEffect(null));
        return card;
    }

    // Creates the per-game breakdown cards for TTT and Connect 4
    private HBox createGameCards() {
        UserData user = getUser();

        VBox ticTacToe = createGameCard(
                "Tic Tac Toe",
                user.getTttGamesPlayed(), user.getTttWins(), user.getTttLosses(), user.getTttDraws(),
                String.format("%.1f%%", user.getTttWinRate()),
                user.getTttCurrentStreak() + " wins",
                user.getTttBestStreak() + " wins",
                String.valueOf(user.getTttPoints())
        );

        VBox connect4 = createGameCard(
                "Connect 4",
                user.getC4GamesPlayed(), user.getC4Wins(), user.getC4Losses(), user.getC4Draws(),
                String.format("%.1f%%", user.getC4WinRate()),
                user.getC4CurrentStreak() + " wins",
                user.getC4BestStreak() + " wins",
                String.valueOf(user.getC4Points())
        );

        HBox container = new HBox(24, ticTacToe, connect4);
        container.setAlignment(Pos.CENTER_LEFT);
        container.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(ticTacToe, Priority.ALWAYS);
        HBox.setHgrow(connect4, Priority.ALWAYS);
        return container;
    }

    // Creates a detailed game card showing W/L/D, win rate bar, streaks, and points
    private VBox createGameCard(
            String title,
            int games, int wins, int losses, int draws,
            String winRate, String currentStreak, String bestStreak, String points
    ) {
        Label t = new Label(title);
        t.setStyle("-fx-text-fill:white; -fx-font-size:18px; -fx-font-weight:bold;");

        VBox labelsLeft = new VBox(
                statLabel("Games Played"),
                statLabel("Wins"),
                statLabel("Losses"),
                statLabel("Draws")
        );
        labelsLeft.setSpacing(8);

        VBox valuesRight = new VBox(
                statValue(String.valueOf(games), "#90a1b9"),
                statValue(String.valueOf(wins), "#00e676"),
                statValue(String.valueOf(losses), "#ff5252"),
                statValue(String.valueOf(draws), "#90a1b9")
        );
        valuesRight.setSpacing(8);
        valuesRight.setAlignment(Pos.CENTER_RIGHT);

        HBox statsRow = new HBox(labelsLeft, valuesRight);
        statsRow.setSpacing(20);
        HBox.setHgrow(labelsLeft, Priority.ALWAYS);

        double winRateValue = 0;
        try {
            winRateValue = Double.parseDouble(winRate.replace("%", "")) / 100.0;
        } catch (Exception e) {
            winRateValue = 0;
        }

        Label wrLabel = statLabel("Win Rate");
        ProgressBar winRateProgress = new ProgressBar(winRateValue);
        winRateProgress.setPrefWidth(Double.MAX_VALUE);
        winRateProgress.setStyle(
                "-fx-accent: #00c8ff;" +
                "-fx-control-inner-background: #1d293d;"
        );

        VBox winRateBox = new VBox(wrLabel, winRateProgress);
        winRateBox.setSpacing(6);

        VBox bottomLeft = new VBox(
                statLabel("Current Streak"),
                statLabel("Best Streak"),
                statLabel("Points Earned")
        );
        bottomLeft.setSpacing(6);

        VBox bottomRight = new VBox(
                statValue(currentStreak, "#90a1b9"),
                statValue(bestStreak, "#ffeb3b"),
                statValue(points, "#00c8ff")
        );
        bottomRight.setSpacing(6);
        bottomRight.setAlignment(Pos.CENTER_RIGHT);

        HBox bottomRow = new HBox(bottomLeft, bottomRight);
        bottomRow.setSpacing(20);
        HBox.setHgrow(bottomLeft, Priority.ALWAYS);

        VBox gameCard = new VBox(t, statsRow, winRateBox, bottomRow);
        gameCard.setSpacing(16);
        gameCard.setPadding(new Insets(20));
        gameCard.setPrefWidth(300);
        gameCard.setMaxWidth(Double.MAX_VALUE);
        VBox.setVgrow(gameCard, Priority.NEVER);
        gameCard.setStyle(
                "-fx-background-color: linear-gradient(to bottom, rgba(34,54,107,0.92), rgba(10,20,40,0.96));" +
                "-fx-background-radius:12;" +
                "-fx-border-radius:12;"
        );
        gameCard.setOnMouseEntered(e -> gameCard.setEffect(new DropShadow(20, Color.web("#00c8ff", 0.5))));
        gameCard.setOnMouseExited(e -> gameCard.setEffect(null));
        return gameCard;
    }

    // Helper to create a grey stat label
    private Label statLabel(String text) {
        Label l = new Label(text);
        l.setStyle("-fx-text-fill:#90a1b9; -fx-font-size:13px;");
        return l;
    }

    // Helper to create a colored stat value label
    private Label statValue(String value, String color) {
        Label l = new Label(value);
        l.setStyle("-fx-text-fill:" + color + "; -fx-font-size:14px; -fx-font-weight:bold;");
        return l;
    }

}
