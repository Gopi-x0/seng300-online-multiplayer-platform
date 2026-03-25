package ca.ucalgary.seng300.TicTacToeEngine;

import ca.ucalgary.seng300.GameEngine;

import java.util.List;

/**
 * Implementation of GameEngine that is specific to tictactoe
 * Constants: Board size(3x3), win lines/conditions: 8 possible winning board states
 * Helper functions:
 * Checkwin(): scans win lines to see if a player has won
 * isBoardFull(): scans board to detect when there are no EMPTY cells remaining and if a DRAW has taken place
 */

public class TicTacToeEngine implements GameEngine {
    private static final int BOARD_SIZE = 3;

    /**
     * Array of 8 winning conditions
     * Coordinates are stored in the following convention: row0, col0, row0, col2, row0, col3 <-(Forms the first line of a 3x3 grid)
     */
    private static final int[][] WIN_LINES={
        //winning coords for rows
        {0, 0, 0, 1, 0, 2},
        {1, 0, 1, 1, 1, 2},
        {2, 0, 2, 1, 2, 2},
        //winning coords for columns
        {0, 0, 1, 0, 2, 0},
        {0, 1, 1, 1, 2, 1},
        {0, 2, 1, 2, 2, 2},
        //winning coords for diagnal
        {0, 0, 1, 1, 2, 2},
        {0, 2, 1, 1, 2, 0}


    };




    //-------------------------------------------------------------------------------//
    @Override
    public GameState createInitialState(List<String> playerIds) {
        return null;
    }

    @Override
    public MoveResult validateMove(GameState state, Move move) {
        return null;
    }

    @Override
    public GameState applyMove(GameState state, Move move) {
        return null;
    }

    @Override
    public GameState evaluateEnd(GameState state) {
        return null;
    }
}
