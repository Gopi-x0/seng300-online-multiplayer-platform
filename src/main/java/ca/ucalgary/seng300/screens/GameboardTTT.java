package ca.ucalgary.seng300.screens;

import ca.ucalgary.seng300.model.*;
import ca.ucalgary.seng300.service.GameSessionManager;
import ca.ucalgary.seng300.service.UserManager;
import ca.ucalgary.seng300.tictactoeengine.GameStatusTicTacToeEngine;
import ca.ucalgary.seng300.tictactoeengine.Symbol;
import ca.ucalgary.seng300.network.GameClient;
import ca.ucalgary.seng300.network.GameServer;

import java.util.List;
import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.util.Duration;

/**
 * GameboardTTT - playable Tic-Tac-Toe board embedded in MainApp.
 *
 * Supports two modes:
 *   LOCAL:  Both players click on the same board (dev toolbar).
 *   NETWORKED: Host runs the engine, guest sends moves over TCP.
 *     - Host processes moves locally and sends STATE to guest.
 *     - Guest sends MOVE_TTT messages and renders received STATE.
 *
 * Network protocol:
 *   Guest->Host: MOVE_TTT:<row>:<col>
 *   Host->Guest: STATE_TTT:<grid>:<activePlayerId>:<status>:<lastRow>:<lastCol>
 */
public class GameboardTTT extends StackPane {

    private static final int ROWS = 3;
    private static final int COLS = 3;
    private static final int CIRCLERADIUS = 60;

    private final StackPane[][] cells = new StackPane[ROWS][COLS];
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
    public GameboardTTT(Runnable onNavigateHome, String player1Name, String player2Name, boolean isHost) {
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

    // Creates player profiles and starts a new Tic-Tac-Toe session
    private void initializeGame() {
        PlayerProfile p1 = new PlayerProfile("1", player1Name);
        PlayerProfile p2 = new PlayerProfile("2", player2Name);
        GameSessionManager.startGame("tictactoe", p1, p2);
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
        if (isHost && msg.startsWith("MOVE_TTT:")) {
            if (myTurn) return;
            String[] parts = msg.substring(9).split(":");
            if (parts.length < 2) return;
            try {
                int row = Integer.parseInt(parts[0]);
                int col = Integer.parseInt(parts[1]);
                processHostMove(row, col);
            } catch (NumberFormatException e) {
                // ignore malformed
            }
        } else if (!isHost && msg.startsWith("STATE_TTT:")) {
            parseAndRenderState(msg.substring(10));
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

    // Host applies a move, updates the board, and sends state to the guest
    private void processHostMove(int row, int col) {
        if (gameOver) return;
        if (row < 0 || row >= ROWS || col < 0 || col >= COLS) return;

        boolean wasEmpty = cells[row][col].getChildren().isEmpty();

        ca.ucalgary.seng300.model.GameState state = GameSessionManager.submitMoveTTT(row, col);
        if (state == null) return;

        updateBoard();

        if (wasEmpty && !cells[row][col].getChildren().isEmpty()) {
            animateSymbol(cells[row][col]);
        }

        sendStateToGuest(row, col);

        GameStatus status = state.getGameStatus();
        if (status != GameStatus.IN_PROGRESS && status != GameStatus.SETUP) {
            gameOver = true;
            handleGameEndHost(status);
        } else {
            myTurn = !myTurn;
        }
    }

    // Serializes the current engine state and sends it to the guest
    private void sendStateToGuest(int lastRow, int lastCol) {
        GameServer server = GameServer.getActive();
        if (server == null) return;

        Object engineState = GameSessionManager.getEngineState();
        if (!(engineState instanceof ca.ucalgary.seng300.tictactoeengine.GameState)) return;
        ca.ucalgary.seng300.tictactoeengine.GameState tttState =
                (ca.ucalgary.seng300.tictactoeengine.GameState) engineState;

        StringBuilder gridStr = new StringBuilder();
        for (int r = 0; r < ROWS; r++) {
            if (r > 0) gridStr.append('/');
            for (int c = 0; c < COLS; c++) {
                Symbol sym = tttState.getCell(r, c);
                if (sym == Symbol.X) gridStr.append('X');
                else if (sym == Symbol.O) gridStr.append('O');
                else gridStr.append('.');
            }
        }

        String activePlayerId = tttState.getActivePlayerId() != null
                ? tttState.getActivePlayerId() : "1";

        GameStatus platformStatus = GameSessionManager.getCurrentState().getGameStatus();

        server.send("STATE_TTT:" + gridStr + ":" + activePlayerId + ":" + platformStatus
                + ":" + lastRow + ":" + lastCol);
    }

    // Parses a state payload from the host and updates the board UI
    private void parseAndRenderState(String payload) {
        try {
            String[] parts = payload.split(":");
            if (parts.length < 3) return;

            String gridStr = parts[0];
            String activePlayerId = parts[1];
            GameStatus status = GameStatus.valueOf(parts[2]);
            int lastRow = parts.length > 3 ? Integer.parseInt(parts[3]) : -1;
            int lastCol = parts.length > 4 ? Integer.parseInt(parts[4]) : -1;

            String[] rows = gridStr.split("/");
            if (rows.length < ROWS) return;

            for (int r = 0; r < ROWS; r++) {
                char[] rowChars = rows[r].toCharArray();
                if (rowChars.length < COLS) return;
                for (int c = 0; c < COLS; c++) {
                    cells[r][c].getChildren().clear();
                    if (rowChars[c] == 'X') {
                        cells[r][c].getChildren().add(createXSymbol());
                    } else if (rowChars[c] == 'O') {
                        cells[r][c].getChildren().add(createOSymbol());
                    }
                }
            }

            if (lastRow >= 0 && lastRow < ROWS && lastCol >= 0 && lastCol < COLS
                    && !cells[lastRow][lastCol].getChildren().isEmpty()) {
                animateSymbol(cells[lastRow][lastCol]);
            }

            if (status == GameStatus.IN_PROGRESS) {
                boolean isP1Turn = activePlayerId.equals("1");
                subTitle.setText(isP1Turn
                        ? player1Name + "'s turn (X)"
                        : player2Name + "'s turn (O)");
                myTurn = !isP1Turn;
            }

            if (status != GameStatus.IN_PROGRESS && status != GameStatus.SETUP) {
                gameOver = true;
                handleGameEndGuest(status);
            }
        } catch (IllegalArgumentException e) {
            System.err.println("GameboardTTT: malformed state payload: " + e.getMessage());
        }
    }

    // Plays a fade+scale animation on the given cell's symbol
    private void animateSymbol(StackPane cell) {
        if (cell.getChildren().isEmpty()) return;
        javafx.scene.Node symbol = cell.getChildren().get(0);
        symbol.setOpacity(0);
        symbol.setScaleX(0.3);
        symbol.setScaleY(0.3);
        FadeTransition ft = new FadeTransition(Duration.millis(300), symbol);
        ft.setToValue(1.0);
        ft.play();
        ScaleTransition st = new ScaleTransition(Duration.millis(300), symbol);
        st.setToX(1.0);
        st.setToY(1.0);
        st.setInterpolator(javafx.animation.Interpolator.EASE_OUT);
        st.play();
    }

    // Handles opponent disconnecting mid-game
    private void handleDisconnect() {
        if (!opponentLeft && chat != null) {
            chat.addSystemMessage("Opponent has disconnected.");
        }
        if (!gameOver) {
            gameOver = true;
            subTitle.setText("Opponent disconnected");
            WinScreen.showWin(gamePane, wrappedNavigateHome(), GameType.TTT);
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
            ca.ucalgary.seng300.model.GameState currentState = GameSessionManager.getCurrentState();
            if (currentState != null) {
                GameSessionManager.endGame(currentState);
            }
            onNavigateHome.run();
        };
    }

    // Click Handling

    // Called when a cell is clicked - handles both local and networked play
    private void handleCellClick(int row, int col) {
        if (gameOver) return;

        if (isNetworked()) {
            if (!myTurn) return;

            if (isHost) {
                processHostMove(row, col);
            } else {
                GameClient client = GameClient.getActive();
                if (client != null) {
                    client.send("MOVE_TTT:" + row + ":" + col);
                    myTurn = false;
                }
            }
        } else {
            boolean wasEmpty = cells[row][col].getChildren().isEmpty();

            ca.ucalgary.seng300.model.GameState state = GameSessionManager.submitMoveTTT(row, col);
            if (state == null) return;

            updateBoard();

            if (wasEmpty && !cells[row][col].getChildren().isEmpty()) {
                animateSymbol(cells[row][col]);
            }

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
            UserManager.recordGameResult("Tic Tac Toe", true, false);
            WinScreen.showWin(gamePane, wrappedNavigateHome(), GameType.TTT);
        } else if (status == GameStatus.P2_WINS) {
            UserManager.recordGameResult("Tic Tac Toe", false, false);
            WinScreen.showLose(gamePane, wrappedNavigateHome(), GameType.TTT);
        } else if (status == GameStatus.DRAW) {
            UserManager.recordGameResult("Tic Tac Toe", false, true);
            WinScreen.showDraw(gamePane, wrappedNavigateHome(), GameType.TTT);
        }
    }

    // Shows the win/lose/draw screen for the guest (results are flipped)
    private void handleGameEndGuest(GameStatus status) {
        if (status == GameStatus.P1_WINS) {
            UserManager.recordGameResult("Tic Tac Toe", false, false);
            WinScreen.showLose(gamePane, wrappedNavigateHome(), GameType.TTT);
        } else if (status == GameStatus.P2_WINS) {
            UserManager.recordGameResult("Tic Tac Toe", true, false);
            WinScreen.showWin(gamePane, wrappedNavigateHome(), GameType.TTT);
        } else if (status == GameStatus.DRAW) {
            UserManager.recordGameResult("Tic Tac Toe", false, true);
            WinScreen.showDraw(gamePane, wrappedNavigateHome(), GameType.TTT);
        }
    }

    // Board Rendering

    // Reads the engine state and updates all cell symbols on the grid
    private void updateBoard() {
        Object engineState = GameSessionManager.getEngineState();
        if (!(engineState instanceof ca.ucalgary.seng300.tictactoeengine.GameState)) return;
        ca.ucalgary.seng300.tictactoeengine.GameState tttState =
                (ca.ucalgary.seng300.tictactoeengine.GameState) engineState;

        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                cells[r][c].getChildren().clear();
                Symbol sym = tttState.getCell(r, c);
                if (sym == Symbol.X) {
                    cells[r][c].getChildren().add(createXSymbol());
                } else if (sym == Symbol.O) {
                    cells[r][c].getChildren().add(createOSymbol());
                }
            }
        }

        if (tttState.getStatus() == GameStatusTicTacToeEngine.IN_PROGRESS
                && tttState.getActivePlayerId() != null) {
            List<String> pIds = tttState.getPlayerIds();
            boolean isPlayer1 = tttState.getActivePlayerId().equals(pIds.get(0));
            subTitle.setText(isPlayer1
                    ? player1Name + "'s turn (X)"
                    : player2Name + "'s turn (O)");
        }
    }

    // Symbol Factories

    // Creates an X symbol using two crossed lines
    private StackPane createXSymbol() {
        double half = 40;
        Line l1 = new Line(-half, -half, half, half);
        l1.setStroke(Color.web("#ff4b4b"));
        l1.setStrokeWidth(6);

        Line l2 = new Line(half, -half, -half, half);
        l2.setStroke(Color.web("#ff4b4b"));
        l2.setStrokeWidth(6);

        StackPane pane = new StackPane(l1, l2);
        pane.setPrefSize(80, 80);
        return pane;
    }

    // Creates an O symbol using a hollow circle
    private StackPane createOSymbol() {
        Circle circle = new Circle(35);
        circle.setFill(Color.TRANSPARENT);
        circle.setStroke(Color.web("#4ea1ff"));
        circle.setStrokeWidth(6);

        StackPane pane = new StackPane(circle);
        pane.setPrefSize(80, 80);
        return pane;
    }

    // UI Build

    // Builds the main layout with title, grid, side streaks, and chat panel
    private void buildUI() {
        Label title = new Label("Tic-Tac-Toe");
        title.setStyle("-fx-text-fill: white; -fx-font-size: 48px; -fx-font-weight: bold;");

        subTitle = new Label(player1Name + "'s turn (X)");
        subTitle.setStyle("-fx-text-fill: white; -fx-font-size: 32px;");

        VBox titleBox = new VBox(title, subTitle);
        titleBox.setAlignment(Pos.CENTER);
        titleBox.setSpacing(5);

        GridPane grid = createGrid();

        VBox streakX = createPlayerCurrentStreakX(ROWS);
        streakX.setAlignment(Pos.BOTTOM_LEFT);

        VBox streakO = createPlayerCurrentStreakO(ROWS, CIRCLERADIUS);
        streakO.setAlignment(Pos.BOTTOM_RIGHT);

        BorderPane mainLayout = new BorderPane();
        mainLayout.setStyle("-fx-background-color: linear-gradient(to bottom, #020b1c, #0f172b);");
        BorderPane.setMargin(titleBox, new Insets(30, 40, 40, 40));

        mainLayout.setTop(titleBox);
        mainLayout.setCenter(grid);
        mainLayout.setLeft(streakX);
        mainLayout.setRight(streakO);

        gamePane = new StackPane(mainLayout);
        HBox.setHgrow(gamePane, Priority.ALWAYS);

        String myName = isHost ? player1Name : player2Name;
        chat = new ChatWidget(myName, this::sendChatMessage);

        HBox rootLayout = new HBox(gamePane, chat);
        rootLayout.setMaxWidth(Double.MAX_VALUE);
        rootLayout.setMaxHeight(Double.MAX_VALUE);

        getChildren().add(rootLayout);
    }

    // Creates the 3x3 grid of clickable cells
    private GridPane createGrid() {
        GridPane gameBoard = new GridPane();
        gameBoard.setAlignment(Pos.CENTER);
        gameBoard.setMaxSize(600, 600);
        gameBoard.setPadding(new Insets(20));
        gameBoard.setHgap(10);
        gameBoard.setVgap(10);
        gameBoard.setStyle("-fx-background-color: transparent;");

        for (int i = 0; i < ROWS; i++) {
            ColumnConstraints column = new ColumnConstraints();
            column.setPercentWidth(33.33);
            gameBoard.getColumnConstraints().add(column);

            RowConstraints row = new RowConstraints();
            row.setPercentHeight(33.33);
            gameBoard.getRowConstraints().add(row);
        }

        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                StackPane cell = new StackPane();
                cell.setPrefSize(150, 150);
                // Cell background and border styling
                cell.setStyle(
                        "-fx-background-color: rgba(9,25,48,0.98);" +
                        "-fx-border-color: #39cfff;" +
                        "-fx-border-radius: 8;" +
                        "-fx-background-radius: 8;" +
                        "-fx-cursor: hand;"
                );

                final int row = r;
                final int col = c;
                cell.setOnMouseClicked(e -> handleCellClick(row, col));

                gameBoard.add(cell, c, r);
                cells[r][c] = cell;
            }
        }
        return gameBoard;
    }

    // Creates a decorative column of faded O circles for the side
    private VBox createPlayerCurrentStreakO(int tiles, int radius) {
        VBox streak = new VBox();
        double size = radius * 2.0 + 6;
        streak.setSpacing(8);

        for (int j = 0; j < tiles; j++) {
            Circle circle = new Circle(radius);
            circle.setFill(Color.TRANSPARENT);
            circle.setStroke(Color.web("#4ea1ff"));
            circle.setStrokeWidth(3);
            circle.setOpacity(0.35);
            StackPane cell = new StackPane(circle);
            cell.setPrefSize(size, size);
            cell.setMinSize(size, size);
            cell.setMaxSize(size, size);
            streak.getChildren().add(cell);
        }
        return streak;
    }

    // Creates a decorative column of faded X symbols for the side
    private VBox createPlayerCurrentStreakX(int tiles) {
        VBox streak = new VBox();
        streak.setSpacing(8);

        for (int j = 0; j < tiles; j++) {
            StackPane x = createX();
            x.setOpacity(0.35);
            streak.getChildren().add(x);
        }
        return streak;
    }

    // Helper to create a small X graphic with two crossed lines
    private StackPane createX() {
        double half = (double) 80 / 2;

        Line l1 = new Line(-half, -half, half, half);
        l1.setStroke(Color.web("#ff4b4b"));
        l1.setStrokeWidth(4);

        Line l2 = new Line(half, -half, -half, half);
        l2.setStroke(Color.web("#ff4b4b"));
        l2.setStrokeWidth(4);

        StackPane pane = new StackPane(l1, l2);
        pane.setPrefSize(80, 80);
        return pane;
    }
}
