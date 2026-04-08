package ca.ucalgary.seng300.screens;

import ca.ucalgary.seng300.model.GameType;
import ca.ucalgary.seng300.service.UserManager;
import ca.ucalgary.seng300.network.GameServer;
import ca.ucalgary.seng300.network.LobbyManager;
import ca.ucalgary.seng300.network.LobbySession;
import javafx.animation.ScaleTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;

import java.io.IOException;
import java.util.function.BiConsumer;

/**
 * Host lobby screen - starts a TCP GameServer, displays the lobby code,
 * shows a terminal log and player list, and waits for a real guest
 * to connect over the network before enabling the Start Game button.
 * 
 * @author Muhammad Ahmad
 * @email muham.ahmad@ucalgary.ca
 */
public class HostLobby extends StackPane {

    private final String gameName;
    private final GameType gameType;
    private final Runnable onBack;
    private final BiConsumer<String, String> onStartGame;

    private LobbySession session;
    private GameServer server;
    private VBox logBox;
    private VBox playerListBox;
    private Label playerCountLabel;
    private Button startBtn;
    private String guestUsername;

    // Constructor - sets up the lobby session, UI, and starts the server
    // @author Muhammad Ahmad
    public HostLobby(String gameName, GameType gameType, Runnable onBack, BiConsumer<String, String> onStartGame) {
        this.gameName = gameName;
        this.gameType = gameType;
        this.onBack = onBack;
        this.onStartGame = onStartGame;
        createLobbySession();
        buildUI();
        startServer();
    }

    // Creates a new lobby session with the host's username from UserManager
    // @author Muhammad Ahmad
    private void createLobbySession() {
        String hostName = "Player 1";
        if (UserManager.getCurrentUser() != null) {
            hostName = UserManager.getCurrentUser().getUsername();
        }
        session = LobbyManager.createLobby(gameType, gameName, hostName);
    }

    // Starts the TCP server with automatic port scanning, then waits for a client
    // @author Muhammad Ahmad
    private void startServer() {
        try {
            server = GameServer.createWithPortScan();
            addLogEntry("> Server started on port " + server.getPort(), "#00e676");
            addLogEntry("> Waiting for players...", "#89a4c6");
            server.setMessageHandler(this::handleMessage);
            server.waitForClient();
        } catch (IOException e) {
            addLogEntry("> ERROR: No available ports (" + GameServer.DEFAULT_PORT + "-" + GameServer.MAX_PORT + ")", "#ff5555");
            addLogEntry("> Close other hosted lobbies and try again", "#ff5555");
        }
    }

    // Handles incoming messages from the connected client (JOIN requests, disconnects, etc.)
    // @author Muhammad Ahmad
    private void handleMessage(String msg) {
        if (msg.startsWith("JOIN:")) {
            String[] parts = msg.split(":", 3);
            if (parts.length < 3) {
                server.send("ERROR:Invalid join message");
                return;
            }
            String code = parts[1];
            String username = parts[2];

            if (!code.equalsIgnoreCase(session.getLobbyCode())) {
                server.send("ERROR:Invalid lobby code");
                return;
            }

            if (session.isFull()) {
                server.send("ERROR:Lobby is full");
                return;
            }

            session.addPlayer(username);
            guestUsername = username;
            server.send("JOINED:" + session.getHostUsername());

            addLogEntry("> " + username + " joined the lobby", "#00c8ff");
            addLogEntry("> Lobby is full - ready to start!", "#00e676");
            refreshPlayerList();

            // Enable the start button now that a guest has joined
            startBtn.setDisable(false);
            startBtn.setStyle(
                    "-fx-background-color: linear-gradient(to right, #1d4ed8, #2563eb);" +
                    "-fx-text-fill: white;" +
                    "-fx-font-size: 16px;" +
                    "-fx-font-weight: bold;" +
                    "-fx-background-radius: 10;" +
                    "-fx-cursor: hand;"
            );
        } else if (msg.equals("DISCONNECTED")) {
            if (guestUsername != null) {
                addLogEntry("> " + guestUsername + " left the lobby", "#ff5555");
                session.removePlayer(guestUsername);
                guestUsername = null;
                refreshPlayerList();
                startBtn.setDisable(true);
                // Dim the start button style since we lost the guest
                startBtn.setStyle(
                        "-fx-background-color: linear-gradient(to right, #1d4ed8, #2563eb);" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 16px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 10;" +
                        "-fx-cursor: hand;" +
                        "-fx-opacity: 0.4;"
                );
                addLogEntry("> Waiting for players...", "#89a4c6");
            }
            server.resetClient();
            server.waitForClient();
        }
    }

    // UI Building

    // Builds the main UI layout with top bar, title, content panels, and start button
    // @author Muhammad Ahmad
    private void buildUI() {
        VBox mainLayout = new VBox(0);
        mainLayout.setStyle("-fx-background-color: linear-gradient(to bottom, #020b1c, #0f172b);");
        mainLayout.setPadding(new Insets(30));
        mainLayout.setFillWidth(true);

        HBox topBar = createTopBar();

        Label title = new Label(gameName + " Lobby");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 38));
        title.setTextFill(Color.WHITE);

        Label subtitle = new Label("Invite friends and start the game");
        subtitle.setFont(Font.font("Arial", 15));
        subtitle.setTextFill(Color.web("#89a4c6"));

        VBox titleBox = new VBox(6, title, subtitle);
        titleBox.setAlignment(Pos.CENTER);
        titleBox.setPadding(new Insets(10, 0, 24, 0));

        HBox mainContent = createMainContent();

        startBtn = createStartButton();

        VBox content = new VBox(20, topBar, titleBox, mainContent, startBtn);
        content.setAlignment(Pos.TOP_CENTER);

        mainLayout.getChildren().add(content);
        VBox.setVgrow(content, Priority.ALWAYS);

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
            if (server != null) {
                server.send("LOBBY_CLOSED");
                server.shutdown();
            }
            LobbyManager.removeLobby(session.getLobbyCode());
            if (onBack != null) onBack.run();
        });

        HBox bar = new HBox(back);
        bar.setAlignment(Pos.CENTER_LEFT);
        return bar;
    }

    // Creates the main content area with left panel (code + log) and right panel (players)
    // @author Muhammad Ahmad
    private HBox createMainContent() {
        VBox leftPanel = createLeftPanel();
        VBox rightPanel = createRightPanel();

        HBox content = new HBox(20, leftPanel, rightPanel);
        content.setAlignment(Pos.TOP_CENTER);
        HBox.setHgrow(leftPanel, Priority.ALWAYS);
        leftPanel.setMaxWidth(Double.MAX_VALUE);
        rightPanel.setMinWidth(220);
        rightPanel.setPrefWidth(240);

        return content;
    }

    // Creates the left panel containing the lobby code card and terminal log
    // @author Muhammad Ahmad
    private VBox createLeftPanel() {
        VBox codeSection = createCodeSection();
        VBox terminalSection = createTerminalSection();

        VBox panel = new VBox(18, codeSection, terminalSection);
        VBox.setVgrow(terminalSection, Priority.ALWAYS);
        return panel;
    }

    // Creates the lobby code display card with a copy-to-clipboard button
    // @author Muhammad Ahmad
    private VBox createCodeSection() {
        Label heading = new Label("Lobby Code");
        heading.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        heading.setTextFill(Color.WHITE);

        Label codeLabel = new Label(session.getLobbyCode());
        codeLabel.setFont(Font.font("Consolas", FontWeight.BOLD, 22));
        codeLabel.setTextFill(Color.web("#00e676"));
        codeLabel.setPadding(new Insets(4, 12, 4, 12));
        codeLabel.setStyle(
                "-fx-background-color: rgba(0,230,118,0.08);" +
                "-fx-background-radius: 6;"
        );

        // Copy button styling
        String copyBase =
                "-fx-background-color: rgba(0,200,255,0.12);" +
                "-fx-text-fill: #00c8ff;" +
                "-fx-font-size: 13px;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 6;" +
                "-fx-cursor: hand;" +
                "-fx-padding: 4 14 4 14;";
        String copyHover =
                "-fx-background-color: rgba(0,200,255,0.22);" +
                "-fx-text-fill: #33e0ff;" +
                "-fx-font-size: 13px;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 6;" +
                "-fx-cursor: hand;" +
                "-fx-padding: 4 14 4 14;";

        Button copyBtn = new Button("Copy");
        copyBtn.setStyle(copyBase);
        copyBtn.setOnMouseEntered(e -> copyBtn.setStyle(copyHover));
        copyBtn.setOnMouseExited(e -> copyBtn.setStyle(copyBase));
        copyBtn.setOnAction(e -> {
            Clipboard clipboard = Clipboard.getSystemClipboard();
            ClipboardContent cc = new ClipboardContent();
            cc.putString(session.getLobbyCode());
            clipboard.setContent(cc);
            copyBtn.setText("Copied!");
            javafx.animation.PauseTransition reset = new javafx.animation.PauseTransition(Duration.seconds(2));
            reset.setOnFinished(ev -> copyBtn.setText("Copy"));
            reset.play();
        });

        HBox codeRow = new HBox(12, codeLabel, copyBtn);
        codeRow.setAlignment(Pos.CENTER_LEFT);

        // Card container styling
        VBox card = new VBox(10, heading, codeRow);
        card.setPadding(new Insets(18));
        card.setStyle(
                "-fx-background-color: linear-gradient(to bottom, rgba(34,54,107,0.92), rgba(10,20,40,0.96));" +
                "-fx-background-radius: 10;" +
                "-fx-border-color: rgba(72,114,170,0.4);" +
                "-fx-border-radius: 10;"
        );
        card.setOnMouseEntered(e -> card.setEffect(new DropShadow(15, Color.web("#00c8ff", 0.3))));
        card.setOnMouseExited(e -> card.setEffect(null));

        return card;
    }

    // Creates the terminal-style log section that shows server events
    // @author Muhammad Ahmad
    private VBox createTerminalSection() {
        playerCountLabel = new Label("Players (" + session.getPlayerCount() + "/" + session.getMaxPlayers() + ")");
        playerCountLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        playerCountLabel.setTextFill(Color.WHITE);

        logBox = new VBox(4);
        logBox.setPadding(new Insets(12));
        logBox.setStyle(
                "-fx-background-color: #0a0e18;" +
                "-fx-background-radius: 8;"
        );
        logBox.setMinHeight(200);

        ScrollPane scrollPane = new ScrollPane(logBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle(
                "-fx-background: #0a0e18;" +
                "-fx-background-color: #0a0e18;" +
                "-fx-border-color: transparent;"
        );
        scrollPane.setPrefHeight(250);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        addLogEntry("> You are hosting...", "#00e676");

        // Card container styling
        VBox card = new VBox(10, playerCountLabel, scrollPane);
        card.setPadding(new Insets(18));
        card.setStyle(
                "-fx-background-color: linear-gradient(to bottom, rgba(34,54,107,0.92), rgba(10,20,40,0.96));" +
                "-fx-background-radius: 10;" +
                "-fx-border-color: rgba(72,114,170,0.4);" +
                "-fx-border-radius: 10;"
        );
        card.setOnMouseEntered(e -> card.setEffect(new DropShadow(15, Color.web("#00c8ff", 0.3))));
        card.setOnMouseExited(e -> card.setEffect(null));
        VBox.setVgrow(card, Priority.ALWAYS);

        return card;
    }

    // Adds a colored log entry label to the terminal log box
    // @author Muhammad Ahmad
    private void addLogEntry(String message, String color) {
        Label entry = new Label(message);
        entry.setFont(Font.font("Consolas", 14));
        entry.setTextFill(Color.web(color));
        entry.setWrapText(true);
        logBox.getChildren().add(entry);
    }

    // Creates the right panel showing the list of connected players
    // @author Muhammad Ahmad
    private VBox createRightPanel() {
        Label heading = new Label("Players");
        heading.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        heading.setTextFill(Color.WHITE);

        playerListBox = new VBox(8);
        playerListBox.setPadding(new Insets(10, 0, 0, 0));
        refreshPlayerList();

        // Card container styling
        VBox card = new VBox(14, heading, playerListBox);
        card.setPadding(new Insets(18));
        card.setStyle(
                "-fx-background-color: linear-gradient(to bottom, rgba(34,54,107,0.92), rgba(10,20,40,0.96));" +
                "-fx-background-radius: 10;" +
                "-fx-border-color: rgba(72,114,170,0.4);" +
                "-fx-border-radius: 10;"
        );
        card.setOnMouseEntered(e -> card.setEffect(new DropShadow(15, Color.web("#00c8ff", 0.3))));
        card.setOnMouseExited(e -> card.setEffect(null));

        return card;
    }

    // Refreshes the player list UI and updates the player count label
    // @author Muhammad Ahmad
    private void refreshPlayerList() {
        playerListBox.getChildren().clear();
        for (int i = 0; i < session.getPlayers().size(); i++) {
            String name = session.getPlayers().get(i);
            String tag = (i == 0) ? " (host)" : "";
            Label playerLabel = new Label("\u2022 " + name + tag);
            playerLabel.setFont(Font.font("Arial", 15));
            playerLabel.setTextFill(Color.web("#cad5e2"));
            playerListBox.getChildren().add(playerLabel);
        }
        playerCountLabel.setText("Players (" + session.getPlayerCount() + "/" + session.getMaxPlayers() + ")");
    }

    // Creates the start game button (disabled until a guest joins)
    // @author Muhammad Ahmad
    private Button createStartButton() {
        // Button styling for disabled, enabled, hover, and pressed states
        String disabledStyle =
                "-fx-background-color: linear-gradient(to right, #1d4ed8, #2563eb);" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 16px;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 10;" +
                "-fx-cursor: hand;" +
                "-fx-opacity: 0.4;";
        String enabledBase =
                "-fx-background-color: linear-gradient(to right, #1d4ed8, #2563eb);" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 16px;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 10;" +
                "-fx-cursor: hand;";
        String enabledHover =
                "-fx-background-color: linear-gradient(to right, #2563eb, #3b82f6);" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 16px;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 10;" +
                "-fx-cursor: hand;";
        String enabledPress =
                "-fx-background-color: #1e40af;" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 16px;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 10;" +
                "-fx-cursor: hand;";

        Button btn = new Button("Start Game");
        btn.setPrefWidth(220);
        btn.setPrefHeight(44);
        btn.setStyle(disabledStyle);
        btn.setDisable(true);

        btn.setOnMouseEntered(e -> {
            if (!btn.isDisabled()) {
                btn.setStyle(enabledHover);
                ScaleTransition st = new ScaleTransition(Duration.millis(150), btn);
                st.setToX(1.04); st.setToY(1.04); st.play();
            }
        });
        btn.setOnMouseExited(e -> {
            if (!btn.isDisabled()) {
                btn.setStyle(enabledBase);
                ScaleTransition st = new ScaleTransition(Duration.millis(150), btn);
                st.setToX(1.0); st.setToY(1.0); st.play();
            }
        });
        btn.setOnMousePressed(e -> {
            if (!btn.isDisabled()) {
                btn.setStyle(enabledPress);
                ScaleTransition st = new ScaleTransition(Duration.millis(80), btn);
                st.setToX(0.96); st.setToY(0.96); st.play();
            }
        });
        btn.setOnMouseReleased(e -> {
            if (!btn.isDisabled()) {
                btn.setStyle(enabledHover);
                ScaleTransition st = new ScaleTransition(Duration.millis(80), btn);
                st.setToX(1.04); st.setToY(1.04); st.play();
            }
        });
        btn.setOnAction(e -> {
            session.startGame();
            server.send("START");
            String p1 = session.getHostUsername();
            String p2 = guestUsername != null ? guestUsername : "Guest";
            if (onStartGame != null) onStartGame.accept(p1, p2);
        });

        return btn;
    }
}
