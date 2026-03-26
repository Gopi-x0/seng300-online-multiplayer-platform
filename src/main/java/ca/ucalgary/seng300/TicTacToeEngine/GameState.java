package ca.ucalgary.seng300.TicTacToeEngine;

import java.util.ArrayList;
import java.util.List;

/**
 * GameState will be a DTO data transfer object
 * Takes a snapshot of the current board status to pass to gamesession manager, or any caller
 * Board is a 3x3 grid of symbols
 * All fields are read only from outside
 * Board will create new GameState instances to update GameState
 */

public class GameState {
    private final Symbol[][] board;
    private final String activePlayerId;
    private final GameStatus status;
    private final List<Move> moveHistory;

    /**
     * Creates a new GameState.
     * The board is defensively copied to preserve immutability.
     */
    public GameState(Symbol[][] board, String activePlayerId, GameStatus status, List<Move> moveHistory) {
        // Defensive copy of the 2D board array
        this.board = new Symbol[board.length][];
        for (int i = 0; i < board.length; i++) {
            this.board[i] = board[i].clone();
        }
        this.activePlayerId = activePlayerId;
        this.status = status;
        this.moveHistory = new ArrayList<>(moveHistory);
    }

    /**
     * Returns the Symbol at a given cell.
     */
    public Symbol getCell(int r, int c) {
        return board[r][c];
    }

    /**
     * Returns a defensive copy of the full board.
     * Use getCell() for single-cell reads where possible.
     */
    public Symbol[][] getBoard() {
        Symbol[][] copy = new Symbol[board.length][];
        for (int i = 0; i < board.length; i++) {
            copy[i] = board[i].clone();
        }
        return copy;
    }

    public String getActivePlayerId() {
        return activePlayerId;
    }

    public GameStatus getStatus() {
        return status;
    }

    /**
     * Returns an unmodifiable view of the move history.
     */
    public List<Move> getMoveHistory() {
        return List.copyOf(moveHistory);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("GameState[status=%s, activePlayer=%s]\n", status, activePlayerId));
        for (int r = 0; r < board.length; r++) {
            for (int c = 0; c < board[r].length; c++) {
                Symbol s = board[r][c];
                sb.append(s == Symbol.EMPTY ? "." : s.toString());
                if (c < board[r].length - 1) sb.append("|");
            }
            sb.append("\n");
        }
        return sb.toString();
    }


}
