package ca.ucalgary.seng300.TicTacToeEngine;

import ca.ucalgary.seng300.interfaces.GameEngine;

import java.util.ArrayList;
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



    //OVERRIDEN GAMEENGINE INTERFACE METHODS
    //-------------------------------------------------------------------------------//
    /**
     * Initialises a fresh 3x3 board. All cells are EMPTY.
     * The first player in the playerIds list goes first and is assigned Symbol.X.
     */
    @Override
    public GameState createInitialState(List<String> playerIds) {

        if (playerIds == null || playerIds.size() < 2) {
            throw new IllegalArgumentException("TicTacToe requires exactly 2 player IDs.");
        }

        Symbol[][] board = new Symbol[BOARD_SIZE][BOARD_SIZE];
        for (int r = 0; r < BOARD_SIZE; r++) {
            for (int c = 0; c < BOARD_SIZE; c++) {
                board[r][c] = Symbol.EMPTY;
            }
        }

        return new GameState(board, playerIds.get(0), GameStatusTicTacToeEngine.IN_PROGRESS, new ArrayList<>(), playerIds);
    }

    /**
     * Validates a move against the current state.
     * Checks (in order):
     *   1. Game is not already over → GAME_ALREADY_OVER
     *   2. It is the player's turn → WRONG_TURN
     *   3. Coordinates are within bounds → OUT_OF_BOUNDS
     *   4. Target cell is empty → CELL_OCCUPIED
     */
    @Override
    public MoveResult validateMove(GameState state, Move move) {
        // 1. Game already finished
        if (state.getStatus() != GameStatusTicTacToeEngine.IN_PROGRESS) {
            return MoveResult.failure(
                    ValidationCode.GAME_ALREADY_OVER, "The game has already ended with status: " + state.getStatus()
            );
        }

        // 2. Wrong player's turn
        if (!move.getPlayerId().equals(state.getActivePlayerId())) {
            return MoveResult.failure(
                    ValidationCode.WRONG_TURN, "It is not player " + move.getPlayerId() + "'s turn. " + "Active player: " + state.getActivePlayerId()
            );
        }

        // 3. Out of bounds
        int row = move.getRow();
        int col = move.getCol();
        if (row < 0 || row >= BOARD_SIZE || col < 0 || col >= BOARD_SIZE) {
            return MoveResult.failure(
                    ValidationCode.OUT_OF_BOUNDS, String.format("Cell (%d, %d) is out of bounds for a %dx%d board.",
                            row, col, BOARD_SIZE, BOARD_SIZE)
            );
        }

        //4. If cell is already occupied
        if (state.getCell(row, col)!= Symbol.EMPTY){
            return MoveResult.failure(ValidationCode.CELL_OCCUPIED, String.format("Cell (%d, %d) is already occupied by %s", row, col, state.getCell(row,col)));
        }
        return MoveResult.success();
    }

    /**
     *Applies move to a cloned board. Apply move process:
     * Clone Board
     * Place symbol(row,col)
     * Toggle a new activePlayerID
     * Run evaluateEnd and update gamestatus
     */
    @Override
    public GameState applyMove(GameState state, Move move) {
        //Clone the current board
        Symbol[][] newBoard = state.getBoard();

        //apply the new move by calling upon the Move class
        newBoard[move.getRow()][move.getCol()]= move.getSymbol();

        //add move to the newHistory
        List<Move> newHistory = new ArrayList<>(state.getMoveHistory());
        newHistory.add(move);


        //toggle the activePlayer and name the next playerID
        //Game session manager will pass the correct playerIDs
        List<String> playerIds = state.getPlayerIds();
        String nextPlayerId;
        if (playerIds.get(0).equals(move.getPlayerId())) {
            // the player at index 0 just moved, so next is index 1
            nextPlayerId = playerIds.get(1);
        } else {
            // the player at index 1 just moved, so next is index 0
            nextPlayerId = playerIds.get(0);
        }

        //Build a current gameState and pass it to evaluateEnd
        GameState intermediateState = new GameState(newBoard, nextPlayerId, GameStatusTicTacToeEngine.IN_PROGRESS, newHistory, playerIds);



        return evaluateEnd(intermediateState);
    }

    @Override
    public GameState evaluateEnd(GameState state) {
        Symbol[][] board = state.getBoard();

        //Check if X has won
        if (checkWin(board, Symbol.X)){
            return new GameState(board, state.getActivePlayerId(), GameStatusTicTacToeEngine.X_WINS, state.getMoveHistory(), state.getPlayerIds());
        }
        //Check if O has won
        if (checkWin(board, Symbol.O)){
            return new GameState(board, state.getActivePlayerId(), GameStatusTicTacToeEngine.O_WINS, state.getMoveHistory(), state.getPlayerIds());
        }
        //Check for draw
        if (isBoardFull(board)){
            return new GameState(board, state.getActivePlayerId(), GameStatusTicTacToeEngine.DRAW, state.getMoveHistory(), state.getPlayerIds());
        }
        //Game still in progress - return original state without changes
        return state;
    }

    //HELPER METHODS


    /**
     * Scans all Win_lines to determine if a given symbol occupies a full win line
     * @param board the current board
     * @param symbol the symbol we want to check
     * @return true if the symbol meets win line condition
     */
    private boolean checkWin(Symbol[][] board, Symbol symbol){
        for (int[] line : WIN_LINES) {
            if (board[line[0]][line[1]] == symbol
                    && board[line[2]][line[3]] == symbol
                    && board[line[4]][line[5]] == symbol) {
                return true;
            }
        }
        return false;
    }


    /**
     * Returns if every cell on the board is occupied
     * Used to detect a draw in match progress
     * @param board the current board
     * @return true if the baord is full, and the for loop doesnt find an empty symbol
     */
    private boolean isBoardFull(Symbol[][] board){
        for (int r= 0; r<BOARD_SIZE; r++){
            for (int c=0; c<BOARD_SIZE; c++){
                if(board[r][c] == Symbol.EMPTY){
                    return false;
                }
            }
        }
        return true;
    }



}
