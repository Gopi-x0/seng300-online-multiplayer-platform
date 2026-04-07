package ca.ucalgary.seng300.connect4engine;

import ca.ucalgary.seng300.model.GameStatus;
import ca.ucalgary.seng300.interfaces.GameEngine;
import ca.ucalgary.seng300.interfaces.MoveResult;
import ca.ucalgary.seng300.interfaces.Move;
import ca.ucalgary.seng300.interfaces.ValidationCode;

import java.util.List;

/**
 * Connect4Game - the Connect 4 game engine.
 *
 * Implements the GameEngine interface.
 * The @Override methods are intentionally thin wrappers - all real logic
 * lives in private methods below. This means if Platform Core changes the
 * GameEngine interface signature, only the @Override declarations need
 * updating, not the actual game logic.
 *
 * Architecture rule (from platform diagram):
 *   This class returns state ONLY. It does NOT call UI, send notifications,
 *   write to databases, or know anything about sessions or lobbies.
 */
public class Connect4Game implements GameEngine<Connect4State> {

    /**
     * Builds the starting state for a new Connect 4 game.
     *
     * Takes the two platform player IDs and wraps them in Player objects.
     * Player 1 gets 'R' (Red) and always goes first.
     * Player 2 gets 'Y' (Yellow).
     * Returns a fresh Connect4State with an empty 6x7 board.
     */
    @Override
    public Connect4State createInitialState(List<String> players) {
        if (players == null || players.size() < 2) {
            throw new IllegalArgumentException("Connect 4 requires exactly 2 players.");
        }

        Player player1 = new Player(players.get(0), 'R');
        Player player2 = new Player(players.get(1), 'Y');

        return new Connect4State(
                new Board(),
                player1,
                player2,
                player1,                // Player 1 moves first
                GameStatus.IN_PROGRESS,
                null                    // No winner yet
        );
    }

    /**
     * Checks whether a move is legal - WITHOUT changing anything.
     *
     * Connect 4 move rules:
     *   1. The game must still be IN_PROGRESS (no moves after game ends)
     *   2. The column must be within bounds (0-6)
     *   3. The column must not already be full
     *
     * Turn order (is it actually this player's turn?) is enforced by the
     * GameSessionManager, not here. This engine trusts the platform on that.
     */
    @Override
    public MoveResult validateMove(Connect4State state, ca.ucalgary.seng300.interfaces.Move move) {
        if (state.getStatus() != GameStatus.IN_PROGRESS) {
            return MoveResult.failure(
                    ValidationCode.GAME_ALREADY_OVER, "The game has already ended with status: " + state.getStatus()
            );
        }

        int col = move.getCol();

        if (col < 0 || col >= Board.COLS) {
            return MoveResult.failure(
                    ValidationCode.OUT_OF_BOUNDS, "Column " + col + " is out of bounds (valid: 0-" + (Board.COLS - 1) + ")"
            );
        }

        if (state.getBoard().isColumnFull(col)) {
            return MoveResult.failure(
                    ValidationCode.CELL_OCCUPIED, "Column " + col + " is full"
            );
        }

        return MoveResult.success();
    }

    /**
     * Applies a validated move and returns a brand-new Connect4State.
     *
     * Steps:
     *   1. Deep-copy the current board (never mutate the old state)
     *   2. Drop the current player's piece - gravity finds the landing row
     *   3. Check if that landing created a win (4-in-a-row)
     *   4. If no win, check if the board is now full (draw)
     *   5. Swap the active player
     *   6. Return a new immutable Connect4State
     */
    @Override
    public Connect4State applyMove(Connect4State state, ca.ucalgary.seng300.interfaces.Move move) {
        Board newBoard = new Board(state.getBoard()); // deep copy
        Player currentPlayer = state.getCurrentPlayer();

        int landedRow = newBoard.dropPiece(move.getCol(), currentPlayer);

        // Determine outcome after the piece lands
        GameStatus newStatus;
        Player winner = null;

        if (newBoard.checkWin(landedRow, move.getCol())) {
            newStatus = (currentPlayer.getSymbol() == 'R') ? GameStatus.P1_WINS : GameStatus.P2_WINS;
            winner = currentPlayer;
        } else if (newBoard.isFull()) {
            newStatus = GameStatus.DRAW;
        } else {
            newStatus = GameStatus.IN_PROGRESS;
        }

        Player nextPlayer = state.getOtherPlayer(currentPlayer);

        return new Connect4State(
                newBoard,
                state.getPlayer1(),   // never changes across turns
                state.getPlayer2(),   // never changes across turns
                nextPlayer,
                newStatus,
                winner
        );
    }

    /**
     * Returns the state unchanged. End-state evaluation (win/draw detection)
     * is performed inside {@link #applyMove} immediately after each piece lands.
     */
    @Override
    public Connect4State evaluateEnd(Connect4State state) {
        return state;
    }
}
