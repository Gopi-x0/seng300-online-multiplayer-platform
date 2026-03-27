package ca.ucalgary.seng300.gamelogic;

import ca.ucalgary.seng300.TicTacToeEngine.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TicTacToeStateTest {

    private TicTacToeEngine engine;
    private GameState initialState;

    private static final String PLAYER_X = "player_x";
    private static final String PLAYER_O = "player_o";

    @BeforeEach
    void setUp() {
        engine = new TicTacToeEngine();
        initialState = engine.createInitialState(List.of(PLAYER_X, PLAYER_O));
    }

    @Test
    void createInitialState() {
        assertNotNull(initialState, "The InitialState Should not be Null");
    }

    @Test
    void initialStatusIsInProgress() {
        assertEquals(GameStatus.IN_PROGRESS, initialState.getStatus(),
                "New game status should be IN_PROGRESS");
    }

    @Test
    void xGoesFirst() {
        assertEquals(PLAYER_X, initialState.getCurrentPlayerId(),
                "First turn should belong to the X player");
    }

    @Test
    void boardStartsEmpty() {
        Symbol[][] board = initialState.getBoard();
        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                assertEquals(Symbol.EMPTY, board[r][c],
                        "Cell [" + r + "][" + c + "] should be EMPTY at start");
            }
        }
    }

    @Test
    void movePlacesSymbolOnBoard() {
        Move move = new Move(1, 1, PLAYER_X, Symbol.X);
        GameState next = engine.applyMove(initialState, move);

        assertEquals(Symbol.X, next.getBoard()[1][1],
                "Cell (1,1) should contain X after X's move");
    }

    @Test
    void turnSwitchesAfterMove() {
        Move move = new Move(0, 0, PLAYER_X, Symbol.X);
        GameState next = engine.applyMove(initialState, move);

        assertEquals(PLAYER_O, next.getCurrentPlayerId(),
                "After X moves, it should be O's turn");
    }

    @Test
    void originalStateNotMutated() {
        Move move = new Move(2, 2, PLAYER_X, Symbol.X);
        engine.applyMove(initialState, move);

        assertEquals(Symbol.EMPTY, initialState.getBoard()[2][2],
                "Original state's board must not be mutated by applyMove");
    }

    @Test
    void applyMoveWithInvalidMoveDoesNotChangeBoard() {
        Move invalidMove = new Move(0, 0, PLAYER_O, Symbol.O);
        GameState result = engine.applyMove(initialState, invalidMove);

        assertEquals(Symbol.EMPTY, result.getBoard()[0][0],
                "Board should be unchanged after an invalid move");
        assertEquals(PLAYER_X, result.getCurrentPlayerId(),
                "Turn should not switch after an invalid move");
    }
    @Test
    void validMoveReturnsSuccess() {
        Move move = new Move(0, 0, PLAYER_X, Symbol.X);
        MoveResult result = engine.validateMove(initialState, move);

        assertTrue(result.isValid(), "Move to empty cell should be valid");
        assertEquals(ValidationCode.SUCCESS, result.getErrorCode());
    }

    @Test
    void negativeRowIsOutOfBounds() {
        Move move = new Move(-1, 0, PLAYER_X, Symbol.X);
        MoveResult result = engine.validateMove(initialState, move);

        assertFalse(result.isValid(), "Out-of-bounds move should be invalid");
        assertEquals(ValidationCode.OUT_OF_BOUNDS, result.getErrorCode());
    }

    @Test
    void colTooLargeIsOutOfBounds() {
        Move move = new Move(0, 3, PLAYER_X, Symbol.X);
        MoveResult result = engine.validateMove(initialState, move);

        assertFalse(result.isValid(), "Move with col=3 should be OUT_OF_BOUNDS");
        assertEquals(ValidationCode.OUT_OF_BOUNDS, result.getErrorCode());
    }

    @Test
    void wrongTurnIsRejected() {
        Move move = new Move(0, 0, PLAYER_O, Symbol.O);
        MoveResult result = engine.validateMove(initialState, move);

        assertFalse(result.isValid(), "Player O should not be allowed to go first");
        assertEquals(ValidationCode.WRONG_TURN, result.getErrorCode());
    }

    @Test
    void occupiedCellIsRejected() {
        Move firstMove = new Move(0, 0, PLAYER_X, Symbol.X);
        GameState afterFirst = engine.applyMove(initialState, firstMove);

        Move secondMove = new Move(0, 0, PLAYER_O, Symbol.O);
        MoveResult result = engine.validateMove(afterFirst, secondMove);

        assertFalse(result.isValid(), "Move on occupied cell should be invalid");
        assertEquals(ValidationCode.CELL_OCCUPIED, result.getErrorCode());
    }

    @Test
    void noMovesAfterGameOver() {
        GameState state = initialState;
        state = engine.applyMove(state, new Move(0, 0, PLAYER_X, Symbol.X));
        state = engine.applyMove(state, new Move(1, 0, PLAYER_O, Symbol.O));
        state = engine.applyMove(state, new Move(0, 1, PLAYER_X, Symbol.X));
        state = engine.applyMove(state, new Move(2, 0, PLAYER_O, Symbol.O));
        state = engine.applyMove(state, new Move(0, 2, PLAYER_X, Symbol.X));
        state = engine.evaluateEnd(state);

        Move extraMove = new Move(1, 1, PLAYER_O, Symbol.O);
        MoveResult result = engine.validateMove(state, extraMove);

        assertFalse(result.isValid(), "No moves allowed after game over");
        assertEquals(ValidationCode.GAME_ALREADY_OVER, result.getErrorCode());
    }

    @Test
    void nullMoveIsHandledGracefully() {
        MoveResult result = engine.validateMove(initialState, null);

        assertNotNull(result, "Engine should return a MoveResult even for a null move");
        assertFalse(result.isValid(), "Null move should not be valid");
    }

    @Test
    void nullStateIsHandledGracefully() {
        Move move = new Move(0, 0, PLAYER_X, Symbol.X);
        MoveResult result = engine.validateMove(null, move);

        assertNotNull(result, "Engine should return a MoveResult even for a null state");
        assertFalse(result.isValid(), "Move on null state should not be valid");
    }

    @Test
    void failureMoveResultIsCorrect() {
        MoveResult result = MoveResult.failure(ValidationCode.CELL_OCCUPIED, "Cell is taken");

        assertFalse(result.isValid(), "Failure result should not be valid");
        assertEquals(ValidationCode.CELL_OCCUPIED, result.getErrorCode());
        assertEquals("Cell is taken", result.getMessage());
    }

    @Test
    void xWinsTopRow() {
        GameState state = initialState;
        state = engine.applyMove(state, new Move(0, 0, PLAYER_X, Symbol.X));
        state = engine.applyMove(state, new Move(1, 0, PLAYER_O, Symbol.O));
        state = engine.applyMove(state, new Move(0, 1, PLAYER_X, Symbol.X));
        state = engine.applyMove(state, new Move(2, 0, PLAYER_O, Symbol.O));
        state = engine.applyMove(state, new Move(0, 2, PLAYER_X, Symbol.X));
        state = engine.evaluateEnd(state);

        assertEquals(GameStatus.X_WINS, state.getStatus(), "X should win with top row");
    }

    @Test
    void xWinsMiddleRow() {
        GameState state = initialState;
        state = engine.applyMove(state, new Move(1, 0, PLAYER_X, Symbol.X));
        state = engine.applyMove(state, new Move(0, 0, PLAYER_O, Symbol.O));
        state = engine.applyMove(state, new Move(1, 1, PLAYER_X, Symbol.X));
        state = engine.applyMove(state, new Move(0, 1, PLAYER_O, Symbol.O));
        state = engine.applyMove(state, new Move(1, 2, PLAYER_X, Symbol.X));
        state = engine.evaluateEnd(state);

        assertEquals(GameStatus.X_WINS, state.getStatus(), "X should win with middle row");
    }

    @Test
    void xWinsBottomRow() {
        GameState state = initialState;
        state = engine.applyMove(state, new Move(2, 0, PLAYER_X, Symbol.X));
        state = engine.applyMove(state, new Move(0, 0, PLAYER_O, Symbol.O));
        state = engine.applyMove(state, new Move(2, 1, PLAYER_X, Symbol.X));
        state = engine.applyMove(state, new Move(0, 1, PLAYER_O, Symbol.O));
        state = engine.applyMove(state, new Move(2, 2, PLAYER_X, Symbol.X));
        state = engine.evaluateEnd(state);

        assertEquals(GameStatus.X_WINS, state.getStatus(), "X should win with bottom row");
    }

    @Test
    void xWinsLeftColumn() {
        GameState state = initialState;
        state = engine.applyMove(state, new Move(0, 0, PLAYER_X, Symbol.X));
        state = engine.applyMove(state, new Move(0, 1, PLAYER_O, Symbol.O));
        state = engine.applyMove(state, new Move(1, 0, PLAYER_X, Symbol.X));
        state = engine.applyMove(state, new Move(1, 1, PLAYER_O, Symbol.O));
        state = engine.applyMove(state, new Move(2, 0, PLAYER_X, Symbol.X));
        state = engine.evaluateEnd(state);

        assertEquals(GameStatus.X_WINS, state.getStatus(), "X should win with left column");
    }

    @Test
    void xWinsOppositeDiagonal() {
        GameState state = initialState;
        state = engine.applyMove(state, new Move(0, 2, PLAYER_X, Symbol.X));
        state = engine.applyMove(state, new Move(0, 0, PLAYER_O, Symbol.O));
        state = engine.applyMove(state, new Move(1, 1, PLAYER_X, Symbol.X));
        state = engine.applyMove(state, new Move(0, 1, PLAYER_O, Symbol.O));
        state = engine.applyMove(state, new Move(2, 0, PLAYER_X, Symbol.X));
        state = engine.evaluateEnd(state);

        assertEquals(GameStatus.X_WINS, state.getStatus(), "X should win with OppositeDiagonal");
    }

    @Test
    void oWinsDiagonal() {
        GameState state = initialState;
        state = engine.applyMove(state, new Move(0, 1, PLAYER_X, Symbol.X));
        state = engine.applyMove(state, new Move(0, 0, PLAYER_O, Symbol.O));
        state = engine.applyMove(state, new Move(0, 2, PLAYER_X, Symbol.X));
        state = engine.applyMove(state, new Move(1, 1, PLAYER_O, Symbol.O));
        state = engine.applyMove(state, new Move(1, 0, PLAYER_X, Symbol.X));
        state = engine.applyMove(state, new Move(2, 2, PLAYER_O, Symbol.O));
        state = engine.evaluateEnd(state);

        assertEquals(GameStatus.O_WINS, state.getStatus(), "O should win with main diagonal");
    }

    @Test
    void oWinsTopRow() {
        GameState state = initialState;
        state = engine.applyMove(state, new Move(1, 0, PLAYER_X, Symbol.X));
        state = engine.applyMove(state, new Move(0, 0, PLAYER_O, Symbol.O));
        state = engine.applyMove(state, new Move(2, 0, PLAYER_X, Symbol.X));
        state = engine.applyMove(state, new Move(0, 1, PLAYER_O, Symbol.O));
        state = engine.applyMove(state, new Move(1, 1, PLAYER_X, Symbol.X));
        state = engine.applyMove(state, new Move(0, 2, PLAYER_O, Symbol.O));
        state = engine.evaluateEnd(state);

        assertEquals(GameStatus.O_WINS, state.getStatus(), "O should win with top row");
    }

    @Test
    void oWinsRightColumn() {
        GameState state = initialState;
        state = engine.applyMove(state, new Move(0, 0, PLAYER_X, Symbol.X));
        state = engine.applyMove(state, new Move(0, 2, PLAYER_O, Symbol.O));
        state = engine.applyMove(state, new Move(1, 0, PLAYER_X, Symbol.X));
        state = engine.applyMove(state, new Move(1, 2, PLAYER_O, Symbol.O));
        state = engine.applyMove(state, new Move(2, 1, PLAYER_X, Symbol.X));
        state = engine.applyMove(state, new Move(2, 2, PLAYER_O, Symbol.O));
        state = engine.evaluateEnd(state);

        assertEquals(GameStatus.O_WINS, state.getStatus(), "O should win with right column");
    }

    @Test
    void fullBoardNoWinnerIsDraw() {
        GameState state = initialState;
        state = engine.applyMove(state, new Move(0, 0, PLAYER_X, Symbol.X));
        state = engine.applyMove(state, new Move(0, 1, PLAYER_O, Symbol.O));
        state = engine.applyMove(state, new Move(0, 2, PLAYER_X, Symbol.X));
        state = engine.applyMove(state, new Move(1, 2, PLAYER_O, Symbol.O));
        state = engine.applyMove(state, new Move(1, 0, PLAYER_X, Symbol.X));
        state = engine.applyMove(state, new Move(2, 0, PLAYER_O, Symbol.O));
        state = engine.applyMove(state, new Move(1, 1, PLAYER_X, Symbol.X));
        state = engine.applyMove(state, new Move(2, 2, PLAYER_O, Symbol.O));
        state = engine.applyMove(state, new Move(2, 1, PLAYER_X, Symbol.X));
        state = engine.evaluateEnd(state);

        assertEquals(GameStatus.DRAW, state.getStatus(), "Full board with no winner should be DRAW");
    }

    @Test
    void gameStillInProgress() {
        GameState state = engine.applyMove(initialState, new Move(0, 0, PLAYER_X, Symbol.X));
        state = engine.evaluateEnd(state);

        assertEquals(GameStatus.IN_PROGRESS, state.getStatus(),
                "Game with one move should still be IN_PROGRESS");
    }









}