package ca.ucalgary.seng300.screens;

import ca.ucalgary.seng300.model.GameType;
import ca.ucalgary.seng300.service.UserManager;
import ca.ucalgary.seng300.network.GameClient;
import ca.ucalgary.seng300.network.GameServer;
import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;

import java.io.IOException;
import java.util.function.BiConsumer;

/**
 * Join lobby screen - lets the player enter an 8-character lobby code,
 * connects to the host's GameServer over TCP on localhost, validates the code,
 * and waits for the host to start the game.
 * 
 * @author Muhammad Ahmad
 * @email muham.ahmad@ucalgary.ca
 */
public class JoinLobby extends StackPane {

    private final String gameName;
    private final GameType gameType;
    private final Runnable onBack;
    private final BiConsumer<String, String> onJoinSuccess;

    private String hostName;
    private String joinerName;
    private int nextPortToTry;
    private String pendingCode;
    private Label pendingErrorLabel;

    // Constructor - sets up the join lobby screen for the given game
    // @author Muhammad Ahmad
    public JoinLobby(String gameName, GameType gameType, Runnable onBack, BiConsumer<String, String> onJoinSuccess) {
        this.gameName = gameName;
        this.gameType = gameType;
        this.onBack = onBack;
        this.onJoinSuccess = onJoinSuccess;
        buildUI();
    }

    // Builds the main UI with top bar, title, and the join card
    // @author Muhammad Ahmad
    private void buildUI() {
        VBox mainLayout = new VBox(0);
        mainLayout.setStyle("-fx-background-color: linear-gradient(to bottom, #020b1c, #0f172b);");
        mainLayout.setPadding(new Insets(40));
        mainLayout.setFillWidth(true);

        HBox topBar = createTopBar();

        Label title = new Label("Join Lobby");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 42));
        title.setTextFill(Color.WHITE);
        title.setStyle("-fx-font-style: italic;");

        Label gameLabel = new Label(gameName);
        gameLabel.setFont(Font.font("Arial", 18));
        gameLabel.setTextFill(Color.web("#89a4c6"));

        VBox titleBox = new VBox(8, title, gameLabel);
        titleBox.setAlignment(Pos.CENTER);
        titleBox.setPadding(new Insets(40, 0, 40, 0));

        VBox joinCard = createJoinCard();

        VBox content = new VBox(0, topBar, titleBox, joinCard);
        content.setAlignment(Pos.TOP_CENTER);

        mainLayout.getChildren().add(content);

        getChildren().add(mainLayout);
    }

    // Creates the top navigation bar with a styled back button
    // @author Muhammad Ahmad
    private HBox createTopBar() {
        // Back button styling - base, hover, pressed states
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

        Button back = new Button("\u2190 Back to Lobby");
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
            GameClient.shutdownActive();
            if (onBack != null) onBack.run();
        });

        HBox bar = new HBox(back);
        bar.setAlignment(Pos.CENTER_LEFT);
        return bar;
    }

    // Creates the join card with lobby code input field, join button, and error label
    // @author Muhammad Ahmad
    private VBox createJoinCard() {
        Label heading = new Label("Lobby Code");
        heading.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        heading.setTextFill(Color.WHITE);

        TextField codeField = new TextField();
        codeField.setPromptText("Enter 8-character code...");
        codeField.setFont(Font.font("Consolas", 16));
        codeField.setPrefHeight(44);
        codeField.setMaxWidth(320);
        // Input field styling
        codeField.setStyle(
                "-fx-background-color: #020618;" +
                "-fx-text-fill: white;" +
                "-fx-prompt-text-fill: #90a1b9;" +
                "-fx-background-radius: 8;" +
                "-fx-border-color: #314158;" +
                "-fx-border-radius: 8;" +
                "-fx-font-size: 16px;"
        );
        codeField.setOnMouseClicked(e -> codeField.setStyle(
                "-fx-background-color: #020618;" +
                "-fx-text-fill: white;" +
                "-fx-prompt-text-fill: #90a1b9;" +
                "-fx-background-radius: 8;" +
                "-fx-border-color: #00c8ff;" +
                "-fx-border-radius: 8;" +
                "-fx-font-size: 16px;"
        ));

        Label errorLabel = new Label();
        errorLabel.setFont(Font.font("Arial", 13));
        errorLabel.setTextFill(Color.web("#ff5555"));
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);

        // Join button styling - base, disabled, hover, pressed
        String btnBase =
                "-fx-background-color: linear-gradient(to right, #1d4ed8, #2563eb);" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 15px;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 8;" +
                "-fx-cursor: hand;";
        String btnDisabled =
                "-fx-background-color: #2a3a5c;" +
                "-fx-text-fill: #5a6a8c;" +
                "-fx-font-size: 15px;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 8;";
        String btnHover =
                "-fx-background-color: linear-gradient(to right, #2563eb, #3b82f6);" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 15px;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 8;" +
                "-fx-cursor: hand;";
        String btnPress =
                "-fx-background-color: #1e40af;" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 15px;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 8;" +
                "-fx-cursor: hand;";

        Button joinBtn = new Button("Join");
        joinBtn.setPrefWidth(80);
        joinBtn.setPrefHeight(44);
        joinBtn.setStyle(btnDisabled);
        joinBtn.setDisable(true);

        // Enable/disable join button based on whether the code field has text
        codeField.textProperty().addListener((obs, old, val) -> {
            boolean empty = val.trim().isEmpty();
            joinBtn.setDisable(empty);
            joinBtn.setStyle(empty ? btnDisabled : btnBase);
            errorLabel.setVisible(false);
            errorLabel.setManaged(false);
        });

        joinBtn.setOnMouseEntered(e -> {
            if (!joinBtn.isDisabled()) {
                joinBtn.setStyle(btnHover);
                ScaleTransition st = new ScaleTransition(Duration.millis(150), joinBtn);
                st.setToX(1.04); st.setToY(1.04); st.play();
            }
        });
        joinBtn.setOnMouseExited(e -> {
            if (!joinBtn.isDisabled()) {
                joinBtn.setStyle(btnBase);
                ScaleTransition st = new ScaleTransition(Duration.millis(150), joinBtn);
                st.setToX(1.0); st.setToY(1.0); st.play();
            }
        });
        joinBtn.setOnMousePressed(e -> {
            if (!joinBtn.isDisabled()) {
                joinBtn.setStyle(btnPress);
                ScaleTransition st = new ScaleTransition(Duration.millis(80), joinBtn);
                st.setToX(0.96); st.setToY(0.96); st.play();
            }
        });
        joinBtn.setOnMouseReleased(e -> {
            if (!joinBtn.isDisabled()) {
                joinBtn.setStyle(btnHover);
                ScaleTransition st = new ScaleTransition(Duration.millis(80), joinBtn);
                st.setToX(1.04); st.setToY(1.04); st.play();
            }
        });
        joinBtn.setOnAction(e -> handleJoin(codeField.getText().trim(), errorLabel));

        HBox inputRow = new HBox(12, codeField, joinBtn);
        inputRow.setAlignment(Pos.CENTER);

        Label hint = new Label("Enter the lobby code from the host.");
        hint.setFont(Font.font("Arial", 13));
        hint.setTextFill(Color.web("#6b7fa0"));

        // Card container styling
        VBox card = new VBox(18, heading, inputRow, errorLabel, hint);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(36, 48, 36, 48));
        card.setMaxWidth(520);
        card.setStyle(
                "-fx-background-color: linear-gradient(to bottom, rgba(34,54,107,0.92), rgba(10,20,40,0.96));" +
                "-fx-background-radius: 14;" +
                "-fx-border-color: rgba(72,114,170,0.4);" +
                "-fx-border-radius: 14;"
        );
        card.setOnMouseEntered(e -> card.setEffect(new DropShadow(15, Color.web("#00c8ff", 0.3))));
        card.setOnMouseExited(e -> card.setEffect(null));

        return card;
    }

    // Validates the lobby code length and kicks off the port scanning connection attempt
    // @author Muhammad Ahmad
    private void handleJoin(String code, Label errorLabel) {
        if (code.length() != 8) {
            showError(errorLabel, "Lobby code must be exactly 8 characters.");
            return;
        }

        joinerName = "Guest";
        if (UserManager.getCurrentUser() != null) {
            joinerName = UserManager.getCurrentUser().getUsername();
        }

        pendingCode = code;
        pendingErrorLabel = errorLabel;
        nextPortToTry = GameServer.DEFAULT_PORT;
        tryNextPort();
    }

    // Tries connecting to the next port in the scan range until we find the host's server
    // @author Muhammad Ahmad
    private void tryNextPort() {
        while (nextPortToTry <= GameServer.MAX_PORT) {
            int port = nextPortToTry++;
            try {
                GameClient client = new GameClient();
                client.connect("localhost", port);
                client.setMessageHandler(this::handleServerMessage);
                client.send("JOIN:" + pendingCode + ":" + joinerName);
                return;
            } catch (IOException e) {
                // port not listening, try next
            }
        }
        showError(pendingErrorLabel, "Could not connect to host. Is a lobby hosted?");
    }

    // Handles messages from the host server (JOINED, ERROR, START, LOBBY_CLOSED, etc.)
    // @author Muhammad Ahmad
    private void handleServerMessage(String msg) {
        if (msg.startsWith("JOINED:")) {
            hostName = msg.substring(7);
            showWaitingState();
        } else if (msg.startsWith("ERROR:")) {
            String error = msg.substring(6);
            GameClient.shutdownActive();
            if (error.equals("Invalid lobby code")) {
                tryNextPort();
            } else {
                showError(pendingErrorLabel, error);
            }
        } else if (msg.equals("START")) {
            if (onJoinSuccess != null) {
                onJoinSuccess.accept(hostName, joinerName);
            }
        } else if (msg.equals("LOBBY_CLOSED") || msg.equals("DISCONNECTED")) {
            GameClient.shutdownActive();
            showLobbyClosedPopup();
        }
    }

    // Replaces the UI with a "connected, waiting for host to start" screen
    // @author Muhammad Ahmad
    private void showWaitingState() {
        getChildren().clear();

        VBox mainLayout = new VBox(0);
        mainLayout.setStyle("-fx-background-color: linear-gradient(to bottom, #020b1c, #0f172b);");
        mainLayout.setPadding(new Insets(40));
        mainLayout.setFillWidth(true);
        mainLayout.setAlignment(Pos.CENTER);

        Label checkmark = new Label("\u2713");
        checkmark.setFont(Font.font("Arial", FontWeight.BOLD, 60));
        checkmark.setTextFill(Color.web("#00e676"));

        Label connectedLabel = new Label("Connected to " + hostName + "'s lobby");
        connectedLabel.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        connectedLabel.setTextFill(Color.web("#00e676"));

        Label gameLabel = new Label(gameName);
        gameLabel.setFont(Font.font("Arial", 16));
        gameLabel.setTextFill(Color.web("#89a4c6"));

        Label waitingLabel = new Label("Waiting for host to start the game...");
        waitingLabel.setFont(Font.font("Arial", 16));
        waitingLabel.setTextFill(Color.web("#89a4c6"));
        waitingLabel.setPadding(new Insets(20, 0, 0, 0));

        Button exitBtn = new Button("Exit Lobby");
        exitBtn.setPrefWidth(200);
        exitBtn.setPrefHeight(40);

        // Exit button styling - red theme
        String exitBase =
                "-fx-background-color: #e7000b;" +
                "-fx-text-fill: white;" +
                "-fx-background-radius: 6;" +
                "-fx-cursor: hand;" +
                "-fx-font-size: 15px;" +
                "-fx-font-weight: bold;";

        exitBtn.setStyle(exitBase);
        exitBtn.setOnMouseEntered(e -> {
            exitBtn.setStyle(exitBase + "-fx-background-color: #ff1a1a;");
            ScaleTransition st = new ScaleTransition(Duration.millis(150), exitBtn);
            st.setToX(1.03); st.setToY(1.03); st.play();
        });
        exitBtn.setOnMouseExited(e -> {
            exitBtn.setStyle(exitBase);
            ScaleTransition st = new ScaleTransition(Duration.millis(150), exitBtn);
            st.setToX(1.0); st.setToY(1.0); st.play();
        });
        exitBtn.setOnMousePressed(e -> {
            exitBtn.setStyle(exitBase + "-fx-background-color: #b80000;");
            ScaleTransition st = new ScaleTransition(Duration.millis(80), exitBtn);
            st.setToX(0.97); st.setToY(0.97); st.play();
        });
        exitBtn.setOnMouseReleased(e -> {
            exitBtn.setStyle(exitBase + "-fx-background-color: #ff1a1a;");
            ScaleTransition st = new ScaleTransition(Duration.millis(80), exitBtn);
            st.setToX(1.03); st.setToY(1.03); st.play();
        });
        exitBtn.setOnAction(e -> {
            GameClient.shutdownActive();
            if (onBack != null) onBack.run();
        });

        // Waiting state card styling
        VBox card = new VBox(16, checkmark, connectedLabel, gameLabel, waitingLabel, exitBtn);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(50, 60, 50, 60));
        card.setMaxWidth(520);
        card.setStyle(
                "-fx-background-color: linear-gradient(to bottom, rgba(34,54,107,0.92), rgba(10,20,40,0.96));" +
                "-fx-background-radius: 14;" +
                "-fx-border-color: rgba(0,230,118,0.4);" +
                "-fx-border-radius: 14;"
        );

        VBox content = new VBox(0, card);
        content.setAlignment(Pos.CENTER);
        VBox.setVgrow(content, Priority.ALWAYS);

        mainLayout.getChildren().add(content);
        VBox.setVgrow(mainLayout, Priority.ALWAYS);

        getChildren().add(mainLayout);
    }

    // Shows an error message on the given label
    // @author Muhammad Ahmad
    private void showError(Label errorLabel, String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
        errorLabel.setManaged(true);
    }

    // Shows a popup overlay when the host closes the lobby unexpectedly
    // @author Muhammad Ahmad
    private void showLobbyClosedPopup() {
        Pane scrim = new Pane();
        scrim.setStyle("-fx-background-color: rgba(0, 0, 0, 0.58);");
        scrim.setMaxWidth(Double.MAX_VALUE);
        scrim.setMaxHeight(Double.MAX_VALUE);

        Label icon = new Label("\u26A0");
        icon.setFont(Font.font("Arial", FontWeight.BOLD, 48));
        icon.setTextFill(Color.web("#ff5555"));
        icon.setAlignment(Pos.CENTER);

        Label message = new Label("Host has closed the Lobby!");
        message.setTextFill(Color.WHITE);
        message.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        message.setAlignment(Pos.CENTER);
        message.setStyle("-fx-text-alignment: center;");

        Button dismissBtn = new Button("Dismiss");
        dismissBtn.setPrefWidth(160);
        dismissBtn.setPrefHeight(40);

        // Dismiss button styling
        String dismissBase =
                "-fx-background-color: linear-gradient(to right, #1d4ed8, #2563eb);" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 14px;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 8;" +
                "-fx-cursor: hand;";
        String dismissHover =
                "-fx-background-color: linear-gradient(to right, #2563eb, #3b82f6);" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 14px;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 8;" +
                "-fx-cursor: hand;";
        String dismissPress =
                "-fx-background-color: #1e40af;" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 14px;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 8;" +
                "-fx-cursor: hand;";

        dismissBtn.setStyle(dismissBase);
        dismissBtn.setOnMouseEntered(e -> {
            dismissBtn.setStyle(dismissHover);
            ScaleTransition st = new ScaleTransition(Duration.millis(150), dismissBtn);
            st.setToX(1.05); st.setToY(1.05); st.play();
        });
        dismissBtn.setOnMouseExited(e -> {
            dismissBtn.setStyle(dismissBase);
            ScaleTransition st = new ScaleTransition(Duration.millis(150), dismissBtn);
            st.setToX(1.0); st.setToY(1.0); st.play();
        });
        dismissBtn.setOnMousePressed(e -> {
            dismissBtn.setStyle(dismissPress);
            ScaleTransition st = new ScaleTransition(Duration.millis(80), dismissBtn);
            st.setToX(0.96); st.setToY(0.96); st.play();
        });
        dismissBtn.setOnMouseReleased(e -> {
            dismissBtn.setStyle(dismissHover);
            ScaleTransition st = new ScaleTransition(Duration.millis(80), dismissBtn);
            st.setToX(1.05); st.setToY(1.05); st.play();
        });

        // Popup card styling
        VBox card = new VBox(24, icon, message, dismissBtn);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(36, 50, 36, 50));
        card.setPrefWidth(500);
        card.setMinWidth(500);
        card.setMaxWidth(500);
        card.setPrefHeight(280);
        card.setMinHeight(280);
        card.setMaxHeight(280);
        card.setStyle(
                "-fx-background-color: linear-gradient(to bottom, #0d1f40, #162d55);" +
                "-fx-background-radius: 18;" +
                "-fx-border-color: rgba(80, 140, 220, 0.55);" +
                "-fx-border-radius: 18;" +
                "-fx-border-width: 1.5;"
        );

        StackPane overlay = new StackPane(scrim, card);
        overlay.setAlignment(Pos.CENTER);
        overlay.setOpacity(0);

        dismissBtn.setOnAction(e -> {
            FadeTransition fadeOut = new FadeTransition(Duration.millis(200), overlay);
            fadeOut.setFromValue(1);
            fadeOut.setToValue(0);
            fadeOut.setOnFinished(ev -> {
                getChildren().remove(overlay);
                if (onBack != null) onBack.run();
            });
            fadeOut.play();
        });

        getChildren().add(overlay);
        FadeTransition fadeIn = new FadeTransition(Duration.millis(250), overlay);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.play();
    }
}
