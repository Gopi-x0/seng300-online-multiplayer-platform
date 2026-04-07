package ca.ucalgary.seng300.service;

import ca.ucalgary.seng300.connect4engine.Connect4Game;
import ca.ucalgary.seng300.connect4engine.Connect4State;
import ca.ucalgary.seng300.tictactoeengine.GameStatusTicTacToeEngine;
import ca.ucalgary.seng300.tictactoeengine.TicTacToeEngine;
import ca.ucalgary.seng300.interfaces.GameEngine;
import ca.ucalgary.seng300.interfaces.MoveResult;
import ca.ucalgary.seng300.model.GameState;
import ca.ucalgary.seng300.model.GameStatus;
import ca.ucalgary.seng300.model.PlayerProfile;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

// Bridges the UI screens and game engines, managing the active game session
public class GameSessionManager {
    static GameEngine<?> engine;
    static Object engineState;
    static GameState currentState;
    static List<PlayerProfile> players = new ArrayList<>();

    /**
     * @param gameType Connect-4 OR Tic-tac-toe
     * @param p1 Player 1
     * @param p2 Player 2
     * @return created game state
     */
    public static GameState startGame(String gameType, PlayerProfile p1, PlayerProfile p2) {
        players.clear();
        players.add(p1);
        players.add(p2);

        List<String> playerIds = new ArrayList<>();
        playerIds.add(p1.getPlayerId());
        playerIds.add(p2.getPlayerId());

        if (Objects.equals(gameType, "connect4")) {
            Connect4Game c4 = new Connect4Game();
            engine = c4;
            engineState = c4.createInitialState(playerIds);
        } else if (Objects.equals(gameType, "tictactoe")) {
            TicTacToeEngine ttt = new TicTacToeEngine();
            engine = ttt;
            engineState = ttt.createInitialState(playerIds);
        }

        int player1Id = Integer.parseInt(p1.getPlayerId());
        currentState = new GameState(gameType, GameStatus.IN_PROGRESS, player1Id, engineState);
        return currentState;
    }

    /**
     * Submits a Connect 4 move for the given column.
     * Validates the move, applies it, and updates both
     * the engine state and the platform GameState.
     *
     * @param column column index (0-6)
     * @return the updated GameState, or null if no game is active
     */
    public static GameState submitMove(int column) {
        if (currentState == null || engineState == null || engine == null) {
            return null;
        }

        if (engine instanceof Connect4Game) {
            Connect4Game c4Engine = (Connect4Game) engine;
            Connect4State c4State = (Connect4State) engineState;

            String currentPlayerId = c4State.getCurrentPlayer().getId();
            String currentSymbol = String.valueOf(c4State.getCurrentPlayer().getSymbol());
            ca.ucalgary.seng300.interfaces.Move move =
                    new ca.ucalgary.seng300.interfaces.Move(0, column, currentPlayerId, currentSymbol);

            MoveResult result = c4Engine.validateMove(c4State, move);
            if (!result.isValid()) {
                return currentState;
            }

            Connect4State newC4State = c4Engine.applyMove(c4State, move);
            engineState = newC4State;

            GameStatus newStatus = newC4State.getStatus();
            int activePlayerIndex;
            if (newStatus == GameStatus.IN_PROGRESS) {
                activePlayerIndex = Integer.parseInt(newC4State.getCurrentPlayer().getId());
            } else {
                activePlayerIndex = currentState.getActivePlayerIndex();
            }

            currentState = new GameState(
                    currentState.getGameType(),
                    newStatus,
                    activePlayerIndex,
                    engineState
            );
        }

        return currentState;
    }

    /**
     * Submits a Tic-Tac-Toe move for the given cell.
     * Validates the move, applies it, and updates both
     * the engine state and the platform GameState.
     *
     * @param row row index (0-2)
     * @param col column index (0-2)
     * @return the updated GameState, or null if no game is active
     */
    public static GameState submitMoveTTT(int row, int col) {
        if (currentState == null || engineState == null || engine == null) {
            return null;
        }

        if (engine instanceof TicTacToeEngine) {
            TicTacToeEngine tttEngine = (TicTacToeEngine) engine;
            ca.ucalgary.seng300.tictactoeengine.GameState tttState =
                    (ca.ucalgary.seng300.tictactoeengine.GameState) engineState;

            String activePlayerId = tttState.getActivePlayerId();
            List<String> pIds = tttState.getPlayerIds();
            String symbol = activePlayerId.equals(pIds.get(0)) ? "X" : "O";

            ca.ucalgary.seng300.interfaces.Move move =
                    new ca.ucalgary.seng300.interfaces.Move(row, col, activePlayerId, symbol);

            MoveResult result = tttEngine.validateMove(tttState, move);
            if (!result.isValid()) {
                return currentState;
            }

            ca.ucalgary.seng300.tictactoeengine.GameState newTttState =
                    tttEngine.applyMove(tttState, move);
            engineState = newTttState;

            GameStatusTicTacToeEngine tttStatus = newTttState.getStatus();
            GameStatus newStatus;
            if (tttStatus == GameStatusTicTacToeEngine.X_WINS) {
                newStatus = GameStatus.P1_WINS;
            } else if (tttStatus == GameStatusTicTacToeEngine.O_WINS) {
                newStatus = GameStatus.P2_WINS;
            } else if (tttStatus == GameStatusTicTacToeEngine.DRAW) {
                newStatus = GameStatus.DRAW;
            } else {
                newStatus = GameStatus.IN_PROGRESS;
            }

            int activePlayerIndex;
            if (newStatus == GameStatus.IN_PROGRESS) {
                activePlayerIndex = Integer.parseInt(newTttState.getActivePlayerId());
            } else {
                activePlayerIndex = currentState.getActivePlayerIndex();
            }

            currentState = new GameState(
                    currentState.getGameType(),
                    newStatus,
                    activePlayerIndex,
                    engineState
            );
        }

        return currentState;
    }


    /**
     * Run to end game and return final results
     * @param gameState gameState
     */
    public static void endGame(GameState gameState) {
        if (gameState == null) {
            return;
        }
        engine = null;
        engineState = null;
        currentState = null;
        players.clear();
    }

    // Returns the raw engine state object (Connect4State or TTT GameState)
    public static Object getEngineState() {
        return engineState;
    }

    // Returns the current platform-level game state
    public static GameState getCurrentState() {
        return currentState;
    }
}
