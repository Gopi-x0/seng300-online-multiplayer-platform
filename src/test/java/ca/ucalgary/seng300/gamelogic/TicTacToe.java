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
}