package ca.ucalgary.seng300.gamelogic;

import ca.ucalgary.seng300.Connect4GameEngine.*;
import ca.ucalgary.seng300.GameStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for Connect4Game engine.
 * These won't pass until the engine team finishes implementing the methods.
 * Written based on the classes and design docs we were given.
 */
public class Connect4Test {

    private Connect4Game engine;
    private Connect4State initialState;

    private static final String PLAYER_RED    = "player_red";    // gets 'R', goes first
    private static final String PLAYER_YELLOW = "player_yellow"; // gets 'Y', goes second

    @BeforeEach
    void setUp() {
        engine = new Connect4Game();
        initialState = (Connect4State) engine.createInitialState(List.of(PLAYER_RED, PLAYER_YELLOW));
    }

    // --- createInitialState tests ---

    /**
     * createInitialState should return something and not null
     */
    @Test
    void initialStateNotNull() {
        assertNotNull(initialState, "Initial state should not be null");
    }

    /**
     * A new game should start as IN_PROGRESS
     */
    @Test
    void initialStatusIsInProgress() {
        assertEquals(GameStatus.IN_PROGRESS, initialState.getStatus(),
                "New game status should be IN_PROGRESS");
    }

    /**
     * Red player (first in the list) should go first
     */
    @Test
    void redGoesFirst() {
        assertEquals(PLAYER_RED, initialState.getCurrentPlayer().getId(),
                "Red player should go first");
    }

    /**
     * Red player should have the 'R' symbol
     */
    @Test
    void redPlayerHasCorrectSymbol() {
        assertEquals('R', initialState.getPlayer1().getSymbol(),
                "Player 1 should have symbol R");
    }

    /**
     * Yellow player should have the 'Y' symbol
     */
    @Test
    void yellowPlayerHasCorrectSymbol() {
        assertEquals('Y', initialState.getPlayer2().getSymbol(),
                "Player 2 should have symbol Y");
    }

    /**
     * Every cell on the board should be empty at the start
     */
    @Test
    void boardStartsEmpty() {
        char[][] grid = initialState.getBoard().getGrid();
        for (int r = 0; r < Board.ROWS; r++) {
            for (int c = 0; c < Board.COLS; c++) {
                assertEquals('.', grid[r][c],
                        "Cell [" + r + "][" + c + "] should be empty at start");
            }
        }
    }

    /**
     * No winner at the start of the game
     */
    @Test
    void noWinnerAtStart() {
        assertNull(initialState.getWinner(), "There should be no winner at the start");
    }

    // --- validateMove tests ---

    /**
     * Dropping into an empty column should be valid
     */
    @Test
    void validMoveReturnsTrue() {
        Move move = new Move(0);
        assertTrue(engine.validateMove(initialState, move),
                "Dropping into an empty column should be valid");
    }

    /**
     * Negative column index should be invalid
     */
    @Test
    void negativeColumnIsInvalid() {
        Move move = new Move(-1);
        assertFalse(engine.validateMove(initialState, move),
                "Negative column should be invalid");
    }

    /**
     * Column index of 7 is past the board edge, should be invalid
     */
    @Test
    void colTooLargeIsInvalid() {
        Move move = new Move(7);
        assertFalse(engine.validateMove(initialState, move),
                "Column 7 is out of bounds and should be invalid");
    }

    /**
     * Dropping into a full column should be invalid
     */
    @Test
    void fullColumnIsInvalid() {
        // Fill column 0 completely (6 rows) by alternating players
        Connect4State state = initialState;
        for (int i = 0; i < Board.ROWS; i++) {
            state = (Connect4State) engine.applyMove(state, new Move(0));
        }

        Move move = new Move(0);
        assertFalse(engine.validateMove(state, move),
                "Dropping into a full column should be invalid");
    }

    /**
     * No moves should be allowed once the game is already over
     */
    @Test
    void noMovesAfterGameOver() {
        // Red wins column 0 vertically
        Connect4State state = initialState;
        state = (Connect4State) engine.applyMove(state, new Move(0)); // R
        state = (Connect4State) engine.applyMove(state, new Move(1)); // Y
        state = (Connect4State) engine.applyMove(state, new Move(0)); // R
        state = (Connect4State) engine.applyMove(state, new Move(1)); // Y
        state = (Connect4State) engine.applyMove(state, new Move(0)); // R
        state = (Connect4State) engine.applyMove(state, new Move(1)); // Y
        state = (Connect4State) engine.applyMove(state, new Move(0)); // R wins

        assertFalse(engine.validateMove(state, new Move(2)),
                "No moves should be allowed after the game is over");
    }

    // --- applyMove tests ---

    /**
     * After red drops in column 3, that cell should have 'R'
     */
    @Test
    void movePlacesPieceOnBoard() {
        Connect4State next = (Connect4State) engine.applyMove(initialState, new Move(3));
        char[][] grid = next.getBoard().getGrid();

        // Gravity means the piece lands on the bottom row (row 5)
        assertEquals('R', grid[Board.ROWS - 1][3],
                "Red's piece should be at the bottom of column 3");
    }

    /**
     * After red moves it should be yellow's turn
     */
    @Test
    void turnSwitchesAfterMove() {
        Connect4State next = (Connect4State) engine.applyMove(initialState, new Move(0));

        assertEquals(PLAYER_YELLOW, next.getCurrentPlayer().getId(),
                "After red moves, it should be yellow's turn");
    }

    /**
     * applyMove should not change the original state
     */
    @Test
    void originalStateNotMutated() {
        engine.applyMove(initialState, new Move(0));
        char[][] grid = initialState.getBoard().getGrid();

        assertEquals('.', grid[Board.ROWS - 1][0],
                "Original state's board should not be mutated by applyMove");
    }

    /**
     * Pieces should stack — second piece in the same column lands above the first
     */
    @Test
    void piecesStackInColumn() {
        Connect4State state = initialState;
        state = (Connect4State) engine.applyMove(state, new Move(0)); // R bottom
        state = (Connect4State) engine.applyMove(state, new Move(0)); // Y above R

        char[][] grid = state.getBoard().getGrid();
        assertEquals('R', grid[Board.ROWS - 1][0], "R should be on the bottom row");
        assertEquals('Y', grid[Board.ROWS - 2][0], "Y should be one row above R");
    }

    // --- evaluateEnd tests ---

    /**
     * evaluateEnd should return false when the game is still going
     */
    @Test
    void gameNotOverAtStart() {
        assertFalse(engine.evaluateEnd(initialState),
                "Game should not be over at the start");
    }

    /**
     * Red wins vertically in column 0 — evaluateEnd should return true
     */
    @Test
    void redWinsVertically() {
        Connect4State state = initialState;
        state = (Connect4State) engine.applyMove(state, new Move(0)); // R
        state = (Connect4State) engine.applyMove(state, new Move(1)); // Y
        state = (Connect4State) engine.applyMove(state, new Move(0)); // R
        state = (Connect4State) engine.applyMove(state, new Move(1)); // Y
        state = (Connect4State) engine.applyMove(state, new Move(0)); // R
        state = (Connect4State) engine.applyMove(state, new Move(1)); // Y
        state = (Connect4State) engine.applyMove(state, new Move(0)); // R wins

        assertTrue(engine.evaluateEnd(state), "Game should be over after red wins vertically");
        assertEquals(GameStatus.P1_WINS, state.getStatus(), "Status should be P1_WINS");
        assertEquals(PLAYER_RED, state.getWinner().getId(), "Red should be the winner");
    }

    /**
     * Red wins horizontally across the bottom row
     */
    @Test
    void redWinsHorizontally() {
        Connect4State state = initialState;
        state = (Connect4State) engine.applyMove(state, new Move(0)); // R
        state = (Connect4State) engine.applyMove(state, new Move(0)); // Y
        state = (Connect4State) engine.applyMove(state, new Move(1)); // R
        state = (Connect4State) engine.applyMove(state, new Move(1)); // Y
        state = (Connect4State) engine.applyMove(state, new Move(2)); // R
        state = (Connect4State) engine.applyMove(state, new Move(2)); // Y
        state = (Connect4State) engine.applyMove(state, new Move(3)); // R wins

        assertTrue(engine.evaluateEnd(state), "Game should be over after red wins horizontally");
        assertEquals(GameStatus.P1_WINS, state.getStatus(), "Status should be P1_WINS");
    }

    /**
     * Yellow wins vertically in column 2
     */
    @Test
    void yellowWinsVertically() {
        Connect4State state = initialState;
        state = (Connect4State) engine.applyMove(state, new Move(0)); // R
        state = (Connect4State) engine.applyMove(state, new Move(2)); // Y
        state = (Connect4State) engine.applyMove(state, new Move(0)); // R
        state = (Connect4State) engine.applyMove(state, new Move(2)); // Y
        state = (Connect4State) engine.applyMove(state, new Move(0)); // R
        state = (Connect4State) engine.applyMove(state, new Move(2)); // Y
        state = (Connect4State) engine.applyMove(state, new Move(1)); // R
        state = (Connect4State) engine.applyMove(state, new Move(2)); // Y wins

        assertTrue(engine.evaluateEnd(state), "Game should be over after yellow wins vertically");
        assertEquals(GameStatus.P2_WINS, state.getStatus(), "Status should be P2_WINS");
        assertEquals(PLAYER_YELLOW, state.getWinner().getId(), "Yellow should be the winner");
    }

    /**
     * Yellow wins horizontally
     */
    @Test
    void yellowWinsHorizontally() {
        Connect4State state = initialState;
        state = (Connect4State) engine.applyMove(state, new Move(0)); // R
        state = (Connect4State) engine.applyMove(state, new Move(3)); // Y
        state = (Connect4State) engine.applyMove(state, new Move(0)); // R
        state = (Connect4State) engine.applyMove(state, new Move(4)); // Y
        state = (Connect4State) engine.applyMove(state, new Move(1)); // R
        state = (Connect4State) engine.applyMove(state, new Move(5)); // Y
        state = (Connect4State) engine.applyMove(state, new Move(1)); // R
        state = (Connect4State) engine.applyMove(state, new Move(6)); // Y wins

        assertTrue(engine.evaluateEnd(state), "Game should be over after yellow wins horizontally");
        assertEquals(GameStatus.P2_WINS, state.getStatus(), "Status should be P2_WINS");
    }

    /**
     * Red wins diagonally going bottom-left to top-right
     */
    @Test
    void redWinsDiagonally() {
        // Build a diagonal win for R at columns 0,1,2,3 going upward
        Connect4State state = initialState;
        state = (Connect4State) engine.applyMove(state, new Move(0)); // R row5 col0
        state = (Connect4State) engine.applyMove(state, new Move(1)); // Y row5 col1
        state = (Connect4State) engine.applyMove(state, new Move(1)); // R row4 col1
        state = (Connect4State) engine.applyMove(state, new Move(2)); // Y row5 col2
        state = (Connect4State) engine.applyMove(state, new Move(2)); // R row4 col2
        state = (Connect4State) engine.applyMove(state, new Move(3)); // Y row5 col3
        state = (Connect4State) engine.applyMove(state, new Move(2)); // R row3 col2
        state = (Connect4State) engine.applyMove(state, new Move(3)); // Y row4 col3
        state = (Connect4State) engine.applyMove(state, new Move(3)); // R row3 col3
        state = (Connect4State) engine.applyMove(state, new Move(4)); // Y row5 col4
        state = (Connect4State) engine.applyMove(state, new Move(3)); // R row2 col3 — diagonal win

        assertTrue(engine.evaluateEnd(state), "Game should be over after red wins diagonally");
        assertEquals(GameStatus.P1_WINS, state.getStatus(), "Status should be P1_WINS");
    }

    /**
     * A full board with no winner should be a DRAW
     */
    @Test
    void fullBoardNoWinnerIsDraw() {
        // Fill the board column by column in a pattern that avoids any 4-in-a-row
        // This sequence is verified to produce a draw on a 6x7 board
        Connect4State state = initialState;
        int[] moves = {
                0,1,0,1,0,1,1,0,1,0,1,0, // cols 0 and 1 filled, alternating
                2,3,2,3,2,3,3,2,3,2,3,2, // cols 2 and 3
                4,5,4,5,4,5,5,4,5,4,5,4, // cols 4 and 5
                6,6,6,6,6,6               // col 6
        };
        for (int col : moves) {
            state = (Connect4State) engine.applyMove(state, new Move(col));
        }

        assertEquals(GameStatus.DRAW, state.getStatus(), "Full board with no winner should be DRAW");
    }

    /**
     * createInitialState should throw if fewer than 2 players are given
     */
    @Test
    void tooFewPlayersThrowsException() {
        assertThrows(IllegalArgumentException.class, () ->
                        engine.createInitialState(List.of("only_one_player")),
                "Should throw if fewer than 2 player IDs are provided");
    }

    /**
     * createInitialState should throw if a null list is passed
     */
    @Test
    void nullPlayerListThrowsException() {
        assertThrows(IllegalArgumentException.class, () ->
                        engine.createInitialState(null),
                "Should throw if player list is null");
    }
}