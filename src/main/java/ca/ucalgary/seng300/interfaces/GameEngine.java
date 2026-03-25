package ca.ucalgary.seng300.interfaces;

import ca.ucalgary.seng300.TicTacToeEngine.GameState;
import ca.ucalgary.seng300.TicTacToeEngine.Move;
import ca.ucalgary.seng300.TicTacToeEngine.MoveResult;

import java.util.List;

/**
 * Game engine interface that defines the move sequence for any two player game on this platform
 * Communication between GameSessionManageer and GUI will happen through this interface.
 * Communication lifecycle will be same across all gameengines that implement this interface
 * Lifecycle of a single turn:
 * validateMove()-check if a move is legal
 * applyMove()-apply move and return a new game state
 * evaluateEnd()-called inside applymove and checks if game has ended
 *
 */

public interface GameEngine {

    /**
     * Builds and returns a fresh initial GameState for a new game.
     *
     * @param playerIds ordered list of player IDs; index 0 goes first
     * @return a brand-new GameState with an empty board
     */
    GameState createInitialState(List<String> playerIds);
    /**
     * Validates a player's Move against the current GameState.
     * Does NOT modify state — purely a read operation.
     *
     * @param state the current game state
     * @param move  the move the player wants to make
     * @return a MoveResult containing isValid and a ValidationCode
     */
    MoveResult validateMove(GameState state, Move move);
    /**
     * Applies a validated Move to the current GameState and returns the new state.
     * Callers should always call validateMove() first.
     *
     * applyMove Process (per UML notes):
     *   1. Clone the current board (preserve immutability)
     *   2. Set board[move.row][move.col] = move.symbol
     *   3. Toggle activePlayerId to the opponent
     *   4. Call evaluateEnd() to update GameStatus
     *
     * @param state the current game state
     * @param move  a move that has already been validated
     * @return a new GameState reflecting the applied move
     */
    GameState applyMove(GameState state, Move move);
    /**
     * Evaluates whether the game has reached a terminal state.
     * Updates GameStatus to X_WINS, O_WINS, DRAW, or leaves it IN_PROGRESS.
     *
     * @param state the state after a move has been applied
     * @return a new GameState with an updated GameStatus
     */
    GameState evaluateEnd(GameState state);



}
