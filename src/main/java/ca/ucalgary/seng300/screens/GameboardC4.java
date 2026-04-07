package ca.ucalgary.seng300.screens;

import ca.ucalgary.seng300.model.*;
import ca.ucalgary.seng300.service.GameSessionManager;
import ca.ucalgary.seng300.service.UserManager;
import ca.ucalgary.seng300.connect4engine.Connect4State;
import ca.ucalgary.seng300.network.GameClient;
import ca.ucalgary.seng300.network.GameServer;
import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

/**
 * GameboardC4 - playable Connect 4 board embedded in MainApp.
 *
 * Supports two modes:
 *   LOCAL:  Both players click on the same board (dev toolbar).
 *   NETWORKED: Host runs the engine, guest sends moves over TCP.
 *     - Host processes moves locally and sends STATE to guest.
 *     - Guest sends MOVE_C4 messages and renders received STATE.
 *
 * Network protocol:
 *   Guest->Host: MOVE_C4:<col>
 *   Host->Guest: STATE_C4:<grid>:<currentSymbol>:<status>:<lastCol>
 */
public class GameboardC4 extends StackPane {

    private static final int ROWS = 6;
    private static final int COLS = 7;
    private static final int CELLSPACING = 75;
    private static final int CIRCLERADIUS = 40;

    private final Circle[][] circles = new Circle[ROWS][COLS];
    private Label subTitle;
    private final Runnable onNavigateHome;
    private final String player1Name;
    private final String player2Name;
    private final boolean isHost;
    private boolean gameOver = false;
    private boolean myTurn;
    private StackPane gamePane;
    private ChatWidget chat;
    private boolean opponentLeft = false;

    // Initializes the game engine (if host) and builds the board UI
    public GameboardC4(Runnable onNavigateHome, String player1Name, String player2Name, boolean isHost) {
        this.onNavigateHome = onNavigateHome;
        this.player1Name = player1Name;
        this.player2Name = player2Name;
        this.isHost = isHost;
        this.myTurn = isHost;

        if (isHost || !isNetworked()) {
            initializeGame();
        }
        buildUI();

        if (isNetworked()) {
            setupNetworkHandlers();
        }
    }

    // Checks if there is an active server or client connection
    private boolean isNetworked() {
        return GameServer.getActive() != null || GameClient.getActive() != null;
    }

    // Creates player profiles and starts a new Connect 4 session
    private void initializeGame() {
        PlayerProfile p1 = new PlayerProfile("1", player1Name);
        PlayerProfile p2 = new PlayerProfile("2", player2Name);
        GameSessionManager.startGame("connect4", p1, p2);
        gameOver = false;
    }

    // Network

    // Registers the message handler on the active server or client
    private void setupNetworkHandlers() {
        if (isHost) {
            GameServer server = GameServer.getActive();
            if (server != null) {
                server.setMessageHandler(this::handleNetworkMessage);
            }
        } else {
            GameClient client = GameClient.getActive();
            if (client != null) {
                client.setMessageHandler(this::handleNetworkMessage);
            }
        }
    }

    // Routes incoming network messages to the appropriate handler
    private void handleNetworkMessage(String msg) {
        if (isHost && msg.startsWith("MOVE_C4:")) {
            if (myTurn) return;
            int col;
            try {
                col = Integer.parseInt(msg.substring(8));
            } catch (NumberFormatException e) {
                return;
            }
            processHostMove(col);
        } else if (!isHost && msg.startsWith("STATE_C4:")) {
            parseAndRenderState(msg.substring(9));
        } else if (msg.startsWith("CHAT:")) {
            String[] chatParts = msg.substring(5).split(":", 2);
            if (chatParts.length == 2 && chat != null) {
                chat.receiveMessage(chatParts[0], chatParts[1]);
            }
        } else if (msg.equals("PLAYER_LEFT")) {
            opponentLeft = true;
            if (chat != null) chat.notifyOpponentLeft();
        } else if (msg.equals("DISCONNECTED")) {
            handleDisconnect();
        }
    }

    /**
     * Host processes a move (either their own click or a guest's MOVE message),
     * updates the local board, and sends the resulting state to the guest.
     */
    private void processHostMove(int column) {
        if (gameOver) return;
        if (column < 0 || column >= COLS) return;

        GameState state = GameSessionManager.submitMove(column);
        if (state == null) return;

        updateBoard();
        animateNewPiece(column);
        sendStateToGuest(column);

        GameStatus status = state.getGameStatus();
        if (status != GameStatus.IN_PROGRESS && status != GameStatus.SETUP) {
            gameOver = true;
            handleGameEndHost(status);
        } else {
            myTurn = !myTurn;
        }
    }

    // Serializes the current engine state and sends it to the guest
    private void sendStateToGuest(int lastCol) {
        GameServer server = GameServer.getActive();
        if (server == null) return;

        Object engineState = GameSessionManager.getEngineState();
        if (!(engineState instanceof Connect4State)) return;
        Connect4State c4State = (Connect4State) engineState;

        char[][] grid = c4State.getBoard().getGrid();
        StringBuilder gridStr = new StringBuilder();
        for (int r = 0; r < ROWS; r++) {
            if (r > 0) gridStr.append('/');
            gridStr.append(new String(grid[r]));
        }

        char currentSymbol = c4State.getCurrentPlayer() != null
                ? c4State.getCurrentPlayer().getSymbol() : '.';
        GameStatus status = c4State.getStatus();

        server.send("STATE_C4:" + gridStr + ":" + currentSymbol + ":" + status + ":" + lastCol);
    }

    // Parses a state payload from the host and updates the board UI
    private void parseAndRenderState(String payload) {
        try {
            String[] parts = payload.split(":");
            if (parts.length < 4) return;

            String gridStr = parts[0];
            if (parts[1].isEmpty()) return;
            char currentSymbol = parts[1].charAt(0);
            GameStatus status = GameStatus.valueOf(parts[2]);
            int lastCol = parts.length > 3 ? Integer.parseInt(parts[3]) : -1;

            String[] rows = gridStr.split("/");
            if (rows.length < ROWS) return;

            for (int r = 0; r < ROWS; r++) {
                char[] rowChars = rows[r].toCharArray();
                if (rowChars.length < COLS) return;
                for (int c = 0; c < COLS; c++) {
                    if (rowChars[c] == 'R') {
                        circles[r][c].setFill(Color.web("#ff4b4b"));
                    } else if (rowChars[c] == 'Y') {
                        circles[r][c].setFill(Color.web("#ffd93d"));
                    } else {
                        circles[r][c].setFill(Color.TRANSPARENT);
                    }
                }
            }

            if (lastCol >= 0 && lastCol < COLS) {
                animateNewPieceFromGrid(rows, lastCol);
            }

            if (status == GameStatus.IN_PROGRESS) {
                boolean isP1Turn = currentSymbol == 'R';
                subTitle.setText(isP1Turn
                        ? player1Name + "'s turn (Red)"
                        : player2Name + "'s turn (Yellow)");
                myTurn = !isP1Turn;
            }

            if (status != GameStatus.IN_PROGRESS && status != GameStatus.SETUP) {
                gameOver = true;
                handleGameEndGuest(status);
            }
        } catch (IllegalArgumentException e) {
            System.err.println("GameboardC4: malformed state payload: " + e.getMessage());
        }
    }

    // Animates the newest piece in the given column using the raw grid data
    private void animateNewPieceFromGrid(String[] rows, int col) {
        for (int r = 0; r < ROWS; r++) {
            if (rows[r].charAt(col) != '.') {
                Circle circle = circles[r][col];
                circle.setOpacity(0);
                circle.setScaleX(0.3);
                circle.setScaleY(0.3);
                FadeTransition ft = new FadeTransition(Duration.millis(300), circle);
                ft.setToValue(1.0);
                ft.play();
                ScaleTransition st = new ScaleTransition(Duration.millis(300), circle);
                st.setToX(1.0);
                st.setToY(1.0);
                st.setInterpolator(javafx.animation.Interpolator.EASE_OUT);
                st.play();
                break;
            }
        }
    }

    // Handles opponent disconnecting mid-game
    private void handleDisconnect() {
        if (!opponentLeft && chat != null) {
            chat.addSystemMessage("Opponent has disconnected.");
        }
        if (!gameOver) {
            gameOver = true;
            subTitle.setText("Opponent disconnected");
            WinScreen.showWin(gamePane, wrappedNavigateHome(), GameType.C4);
        }
    }

    // Chat and Navigation

    // Sends a chat message to the other player over the network
    private void sendChatMessage(String text) {
        if (isNetworked()) {
            String myName = isHost ? player1Name : player2Name;
            if (isHost) {
                GameServer server = GameServer.getActive();
                if (server != null) server.send("CHAT:" + myName + ":" + text);
            } else {
                GameClient client = GameClient.getActive();
                if (client != null) client.send("CHAT:" + myName + ":" + text);
            }
        }
    }

    // Returns a Runnable that notifies the opponent and cleans up before going home
    private Runnable wrappedNavigateHome() {
        return () -> {
            if (isNetworked()) {
                if (isHost) {
                    GameServer server = GameServer.getActive();
                    if (server != null) server.send("PLAYER_LEFT");
                } else {
                    GameClient client = GameClient.getActive();
                    if (client != null) client.send("PLAYER_LEFT");
                }
            }
            GameState currentState = GameSessionManager.getCurrentState();
            if (currentState != null) {
                GameSessionManager.endGame(currentState);
            }
            onNavigateHome.run();
        };
    }

    // Click Handling

    // Called when a column is clicked - handles both local and networked play
    private void handleColumnClick(int column) {
        if (gameOver) return;

        if (isNetworked()) {
            if (!myTurn) return;

            if (isHost) {
                processHostMove(column);
            } else {
                GameClient client = GameClient.getActive();
                if (client != null) {
                    client.send("MOVE_C4:" + column);
                    myTurn = false;
                }
            }
        } else {
            GameState state = GameSessionManager.submitMove(column);
            if (state == null) return;

            updateBoard();
            animateNewPiece(column);

            GameStatus status = state.getGameStatus();
            if (status != GameStatus.IN_PROGRESS && status != GameStatus.SETUP) {
                gameOver = true;
                handleGameEndHost(status);
            }
        }
    }

    // Game End

    // Shows the win/lose/draw screen for the host based on game result
    private void handleGameEndHost(GameStatus status) {
        if (status == GameStatus.P1_WINS) {
            UserManager.recordGameResult("Connect 4", true, false);
            WinScreen.showWin(gamePane, wrappedNavigateHome(), GameType.C4);
        } else if (status == GameStatus.P2_WINS) {
            UserManager.recordGameResult("Connect 4", false, false);
            WinScreen.showLose(gamePane, wrappedNavigateHome(), GameType.C4);
        } else if (status == GameStatus.DRAW) {
            UserManager.recordGameResult("Connect 4", false, true);
            WinScreen.showDraw(gamePane, wrappedNavigateHome(), GameType.C4);
        }
    }

    // Shows the win/lose/draw screen for the guest (results are flipped)
    private void handleGameEndGuest(GameStatus status) {
        if (status == GameStatus.P1_WINS) {
            UserManager.recordGameResult("Connect 4", false, false);
            WinScreen.showLose(gamePane, wrappedNavigateHome(), GameType.C4);
        } else if (status == GameStatus.P2_WINS) {
            UserManager.recordGameResult("Connect 4", true, false);
            WinScreen.showWin(gamePane, wrappedNavigateHome(), GameType.C4);
        } else if (status == GameStatus.DRAW) {
            UserManager.recordGameResult("Connect 4", false, true);
            WinScreen.showDraw(gamePane, wrappedNavigateHome(), GameType.C4);
        }
    }

    // Board Rendering

    // Reads the engine state and updates all circle colors on the grid
    private void updateBoard() {
        Object engineState = GameSessionManager.getEngineState();
        if (!(engineState instanceof Connect4State)) return;
        Connect4State c4State = (Connect4State) engineState;

        char[][] grid = c4State.getBoard().getGrid();

        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                if (grid[r][c] == 'R') {
                    circles[r][c].setFill(Color.web("#ff4b4b"));
                } else if (grid[r][c] == 'Y') {
                    circles[r][c].setFill(Color.web("#ffd93d"));
                } else {
                    circles[r][c].setFill(Color.TRANSPARENT);
                }
            }
        }

        if (c4State.getStatus() == GameStatus.IN_PROGRESS
                && c4State.getCurrentPlayer() != null) {
            boolean isPlayer1 = c4State.getCurrentPlayer().getSymbol() == 'R';
            subTitle.setText(isPlayer1
                    ? player1Name + "'s turn (Red)"
                    : player2Name + "'s turn (Yellow)");
        }
    }

    // Plays a fade+scale animation on the most recently placed piece
    private void animateNewPiece(int column) {
        Object engineState = GameSessionManager.getEngineState();
        if (!(engineState instanceof Connect4State)) return;
        Connect4State c4State = (Connect4State) engineState;
        char[][] grid = c4State.getBoard().getGrid();
        for (int r = 0; r < ROWS; r++) {
            if (grid[r][column] != '.') {
                Circle circle = circles[r][column];
                circle.setOpacity(0);
                circle.setScaleX(0.3);
                circle.setScaleY(0.3);
                FadeTransition ft = new FadeTransition(Duration.millis(300), circle);
                ft.setToValue(1.0);
                ft.play();
                ScaleTransition st = new ScaleTransition(Duration.millis(300), circle);
                st.setToX(1.0);
                st.setToY(1.0);
                st.setInterpolator(javafx.animation.Interpolator.EASE_OUT);
                st.play();
                break;
            }
        }
    }

    // UI Build

    // Builds the main layout with title, grid, side streaks, and chat panel
    private void buildUI() {
        Label title = new Label("Connect 4");
        title.setStyle("-fx-text-fill: white; -fx-font-size: 48px; -fx-font-weight: bold;");

        subTitle = new Label(player1Name + "'s turn (Red)");
        subTitle.setStyle("-fx-text-fill: white; -fx-font-size: 32px;");

        VBox titleBox = new VBox(title, subTitle);
        titleBox.setAlignment(Pos.CENTER);
        titleBox.setSpacing(5);

        VBox grid = createGrid();

        VBox streakLeft = createPlayerStreak(Color.web("#ff4b4b"));
        streakLeft.setAlignment(Pos.BOTTOM_LEFT);
        VBox streakRight = createPlayerStreak(Color.web("#ffd93d"));
        streakRight.setAlignment(Pos.BOTTOM_RIGHT);

        BorderPane mainLayout = new BorderPane();
        mainLayout.setTop(titleBox);
        BorderPane.setAlignment(titleBox, Pos.CENTER);

        mainLayout.setLeft(streakLeft);
        BorderPane.setAlignment(streakLeft, Pos.BOTTOM_RIGHT);

        mainLayout.setRight(streakRight);
        BorderPane.setAlignment(streakRight, Pos.BOTTOM_CENTER);

        mainLayout.setCenter(grid);
        mainLayout.setPadding(new Insets(20));
        mainLayout.setStyle("-fx-background-color: linear-gradient(to bottom, #020b1c, #0f172b);");

        grid.setStyle("-fx-background-color: rgba(9,25,48,0.98); -fx-background-radius: 15;");
        grid.setPadding(new Insets(CELLSPACING));
        grid.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);

        gamePane = new StackPane(mainLayout);
        HBox.setHgrow(gamePane, Priority.ALWAYS);

        String myName = isHost ? player1Name : player2Name;
        chat = new ChatWidget(myName, this::sendChatMessage);

        HBox rootLayout = new HBox(gamePane, chat);
        rootLayout.setMaxWidth(Double.MAX_VALUE);
        rootLayout.setMaxHeight(Double.MAX_VALUE);

        getChildren().add(rootLayout);
    }

    // Creates the 6x7 grid of clickable circles
    private VBox createGrid() {
        VBox outer = new VBox();
        float space = 9.50f;
        outer.setAlignment(Pos.CENTER);
        outer.setSpacing(space);

        for (int i = 0; i < ROWS; i++) {
            HBox row = new HBox(12);
            row.setAlignment(Pos.CENTER);
            for (int j = 0; j < COLS; j++) {
                Circle circle = new Circle(CIRCLERADIUS);
                circle.setFill(Color.TRANSPARENT);
                circle.setStroke(Color.web("#31c6ff"));
                circle.setStrokeWidth(2);
                circles[i][j] = circle;

                StackPane cell = new StackPane(circle);
                cell.setPrefSize(CELLSPACING, CELLSPACING);
                cell.setStyle("-fx-cursor: hand;");

                final int col = j;
                cell.setOnMouseClicked(e -> handleColumnClick(col));

                row.getChildren().add(cell);
            }
            outer.getChildren().add(row);
        }
        return outer;
    }

    // Creates the decorative side streak of faded circles for a player color
    private VBox createPlayerStreak(Color playerColor) {
        VBox streak = new VBox();
        double size = CIRCLERADIUS * 2.0 + 4;
        streak.setSpacing(8);

        for (int j = 0; j < 4; j++) {
            Circle circle = new Circle(CIRCLERADIUS);
            circle.setFill(playerColor);
            circle.setOpacity(0.35);
            StackPane cell = new StackPane(circle);
            cell.setPrefSize(size, size);
            cell.setMinSize(size, size);
            cell.setMaxSize(size, size);
            streak.getChildren().add(cell);
        }
        return streak;
    }
}
