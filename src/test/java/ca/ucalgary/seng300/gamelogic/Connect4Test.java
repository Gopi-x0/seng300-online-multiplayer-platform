package ca.ucalgary.seng300.gamelogic;

import ca.ucalgary.seng300.connect4engine.*;
import ca.ucalgary.seng300.model.GameStatus;
import ca.ucalgary.seng300.interfaces.MoveResult;
import ca.ucalgary.seng300.interfaces.Move;
import ca.ucalgary.seng300.interfaces.ValidationCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class Connect4Test {

    private Connect4Game engine;
    private Connect4State initialState;

    private static final String PLAYER_RED    = "player_red";
    private static final String PLAYER_YELLOW = "player_yellow";

    private Move move(int col) {
        return new Move(-1, col, PLAYER_RED, "R");
    }

    @BeforeEach
    void setUp() {
        engine = new Connect4Game();
        initialState = engine.createInitialState(List.of(PLAYER_RED, PLAYER_YELLOW));
    }

    // --- setup and creation ---

    // fresh game: in progress, red first, correct symbols, no winner
    @Test
    void newGameSetUpCorrectly() {
        assertNotNull(initialState);
        assertEquals(GameStatus.IN_PROGRESS, initialState.getStatus());
        assertEquals(PLAYER_RED, initialState.getCurrentPlayer().getId());
        assertEquals('R', initialState.getPlayer1().getSymbol());
        assertEquals('Y', initialState.getPlayer2().getSymbol());
        assertNull(initialState.getWinner());
    }

    // board should be all dots at the start
    @Test
    void boardStartsEmpty() {
        char[][] grid = initialState.getBoard().getGrid();
        for (int r = 0; r < Board.ROWS; r++)
            for (int c = 0; c < Board.COLS; c++)
                assertEquals('.', grid[r][c]);
    }

    // need at least 2 players
    @Test
    void onePlayerThrows() {
        assertThrows(IllegalArgumentException.class, () ->
                engine.createInitialState(List.of("solo")));
    }

    // null list should throw
    @Test
    void nullPlayersThrows() {
        assertThrows(IllegalArgumentException.class, () ->
                engine.createInitialState(null));
    }

    // --- validation ---

    // normal drop in an empty column should work
    @Test
    void validMoveAccepted() {
        MoveResult result = engine.validateMove(initialState, move(0));
        assertTrue(result.isValid());
        assertEquals(ValidationCode.SUCCESS, result.getErrorCode());
    }

    // every column 0-6 should be valid on an empty board
    @Test
    void allColumnsOpenAtStart() {
        for (int col = 0; col < Board.COLS; col++)
            assertTrue(engine.validateMove(initialState, move(col)).isValid());
    }

    // columns -1 and 7 don't exist
    @Test
    void outOfBoundsRejected() {
        assertFalse(engine.validateMove(initialState, move(-1)).isValid());
        assertEquals(ValidationCode.OUT_OF_BOUNDS,
                engine.validateMove(initialState, move(7)).getErrorCode());
    }

    // can't drop into a column that's already full (6 pieces)
    @Test
    void fullColumnRejected() {
        Connect4State state = initialState;
        for (int i = 0; i < Board.ROWS; i++)
            state = engine.applyMove(state, move(0));

        MoveResult result = engine.validateMove(state, move(0));
        assertFalse(result.isValid());
        assertEquals(ValidationCode.CELL_OCCUPIED, result.getErrorCode());
    }

    // 5 pieces in a column — still room for one more
    @Test
    void almostFullColumnStillValid() {
        Connect4State state = initialState;
        for (int i = 0; i < Board.ROWS - 1; i++)
            state = engine.applyMove(state, move(0));

        assertTrue(engine.validateMove(state, move(0)).isValid());
    }

    // game is over, no more moves allowed
    @Test
    void cantMoveAfterWin() {
        Connect4State state = initialState;
        state = engine.applyMove(state, move(0)); // R
        state = engine.applyMove(state, move(1)); // Y
        state = engine.applyMove(state, move(0)); // R
        state = engine.applyMove(state, move(1)); // Y
        state = engine.applyMove(state, move(0)); // R
        state = engine.applyMove(state, move(1)); // Y
        state = engine.applyMove(state, move(0)); // R wins vertically

        MoveResult result = engine.validateMove(state, move(2));
        assertFalse(result.isValid());
        assertEquals(ValidationCode.GAME_ALREADY_OVER, result.getErrorCode());
    }

    // --- making moves ---

    // piece should fall to the bottom row
    @Test
    void pieceFallsToBottom() {
        Connect4State next = engine.applyMove(initialState, move(3));
        assertEquals('R', next.getBoard().getGrid()[Board.ROWS - 1][3]);
    }

    // second piece in the same column should land on top of the first
    @Test
    void piecesStack() {
        Connect4State state = engine.applyMove(initialState, move(0));
        state = engine.applyMove(state, move(0));

        char[][] grid = state.getBoard().getGrid();
        assertEquals('R', grid[Board.ROWS - 1][0]);
        assertEquals('Y', grid[Board.ROWS - 2][0]);
    }

    // turns should go red, yellow, red, yellow...
    @Test
    void turnsAlternate() {
        Connect4State state = engine.applyMove(initialState, move(0));
        assertEquals(PLAYER_YELLOW, state.getCurrentPlayer().getId());
        state = engine.applyMove(state, move(1));
        assertEquals(PLAYER_RED, state.getCurrentPlayer().getId());
    }

    // applying a move shouldn't touch the old state
    @Test
    void oldStateUnchanged() {
        engine.applyMove(initialState, move(0));
        assertEquals('.', initialState.getBoard().getGrid()[Board.ROWS - 1][0]);
    }

    // getOtherPlayer should swap correctly
    @Test
    void otherPlayerSwaps() {
        Player p1 = initialState.getPlayer1();
        Player p2 = initialState.getPlayer2();
        assertSame(p2, initialState.getOtherPlayer(p1));
        assertSame(p1, initialState.getOtherPlayer(p2));
    }

    // getGrid should return a copy so external changes don't affect the board
    @Test
    void gridIsCopiedNotShared() {
        char[][] grid1 = initialState.getBoard().getGrid();
        char[][] grid2 = initialState.getBoard().getGrid();
        assertNotSame(grid1, grid2);
        grid1[0][0] = 'R';
        assertEquals('.', initialState.getBoard().getGrid()[0][0]);
    }

    // two separate games shouldn't affect each other
    @Test
    void separateGamesIndependent() {
        Connect4State game1 = engine.createInitialState(List.of("alice", "bob"));
        Connect4State game2 = engine.createInitialState(List.of("charlie", "dave"));

        game1 = engine.applyMove(game1, move(0));
        assertEquals('.', game2.getBoard().getGrid()[Board.ROWS - 1][0]);
    }

    // --- wins ---

    // 4 in a column
    @Test
    void redWinsVertical() {
        Connect4State state = initialState;
        state = engine.applyMove(state, move(0)); // R
        state = engine.applyMove(state, move(1)); // Y
        state = engine.applyMove(state, move(0)); // R
        state = engine.applyMove(state, move(1)); // Y
        state = engine.applyMove(state, move(0)); // R
        state = engine.applyMove(state, move(1)); // Y
        state = engine.applyMove(state, move(0)); // R — 4 in col 0

        assertEquals(GameStatus.P1_WINS, state.getStatus());
        assertEquals(PLAYER_RED, state.getWinner().getId());
    }

    // 4 across the bottom row
    @Test
    void redWinsHorizontal() {
        Connect4State state = initialState;
        state = engine.applyMove(state, move(0)); // R bottom
        state = engine.applyMove(state, move(0)); // Y on top
        state = engine.applyMove(state, move(1)); // R bottom
        state = engine.applyMove(state, move(1)); // Y on top
        state = engine.applyMove(state, move(2)); // R bottom
        state = engine.applyMove(state, move(2)); // Y on top
        state = engine.applyMove(state, move(3)); // R bottom — 4 across

        assertEquals(GameStatus.P1_WINS, state.getStatus());
    }

    // 4 going bottom-left to top-right: (5,0)(4,1)(3,2)(2,3)
    @Test
    void redWinsDiagonal() {
        Connect4State state = initialState;
        state = engine.applyMove(state, move(0)); // R at (5,0)
        state = engine.applyMove(state, move(1)); // Y at (5,1)
        state = engine.applyMove(state, move(1)); // R at (4,1)
        state = engine.applyMove(state, move(2)); // Y at (5,2)
        state = engine.applyMove(state, move(2)); // R at (4,2)
        state = engine.applyMove(state, move(3)); // Y at (5,3)
        state = engine.applyMove(state, move(2)); // R at (3,2)
        state = engine.applyMove(state, move(3)); // Y at (4,3)
        state = engine.applyMove(state, move(3)); // R at (3,3)
        state = engine.applyMove(state, move(4)); // Y at (5,4)
        state = engine.applyMove(state, move(3)); // R at (2,3)

        assertEquals(GameStatus.P1_WINS, state.getStatus());
    }

    // 4 going bottom-right to top-left: (5,6)(4,5)(3,4)(2,3)
    @Test
    void redWinsAntiDiagonal() {
        Connect4State state = initialState;
        state = engine.applyMove(state, move(6)); // R at (5,6)
        state = engine.applyMove(state, move(5)); // Y at (5,5)
        state = engine.applyMove(state, move(5)); // R at (4,5)
        state = engine.applyMove(state, move(4)); // Y at (5,4)
        state = engine.applyMove(state, move(4)); // R at (4,4)
        state = engine.applyMove(state, move(3)); // Y at (5,3)
        state = engine.applyMove(state, move(4)); // R at (3,4)
        state = engine.applyMove(state, move(3)); // Y at (4,3)
        state = engine.applyMove(state, move(3)); // R at (3,3)
        state = engine.applyMove(state, move(0)); // Y at (5,0)
        state = engine.applyMove(state, move(3)); // R at (2,3)

        assertEquals(GameStatus.P1_WINS, state.getStatus());
    }

    // yellow should be able to win too
    @Test
    void yellowCanWin() {
        Connect4State state = initialState;
        state = engine.applyMove(state, move(0)); // R
        state = engine.applyMove(state, move(2)); // Y
        state = engine.applyMove(state, move(0)); // R
        state = engine.applyMove(state, move(2)); // Y
        state = engine.applyMove(state, move(0)); // R
        state = engine.applyMove(state, move(2)); // Y
        state = engine.applyMove(state, move(1)); // R (not col 2)
        state = engine.applyMove(state, move(2)); // Y — 4 in col 2

        assertEquals(GameStatus.P2_WINS, state.getStatus());
        assertEquals(PLAYER_YELLOW, state.getWinner().getId());
    }

    // filling a gap between pieces: R at cols 0,2,3, then drop R at 1 to connect all 4
    @Test
    void winByFillingGap() {
        Connect4State state = initialState;
        state = engine.applyMove(state, move(0)); // R at (5,0)
        state = engine.applyMove(state, move(6)); // Y
        state = engine.applyMove(state, move(2)); // R at (5,2)
        state = engine.applyMove(state, move(6)); // Y
        state = engine.applyMove(state, move(3)); // R at (5,3)
        state = engine.applyMove(state, move(6)); // Y
        state = engine.applyMove(state, move(1)); // R at (5,1) — fills the gap

        assertEquals(GameStatus.P1_WINS, state.getStatus());
    }

    // vertical win using the top rows (pieces stacked high)
    // col 0: Y Y R R R R (bottom to top) — R wins rows 3 to 0
    @Test
    void verticalWinNearTopOfBoard() {
        Connect4State state = initialState;
        state = engine.applyMove(state, move(1)); // R
        state = engine.applyMove(state, move(0)); // Y at (5,0)
        state = engine.applyMove(state, move(1)); // R
        state = engine.applyMove(state, move(0)); // Y at (4,0)
        state = engine.applyMove(state, move(0)); // R at (3,0)
        state = engine.applyMove(state, move(1)); // Y
        state = engine.applyMove(state, move(0)); // R at (2,0)
        state = engine.applyMove(state, move(1)); // Y
        state = engine.applyMove(state, move(0)); // R at (1,0)
        state = engine.applyMove(state, move(1)); // Y
        state = engine.applyMove(state, move(0)); // R at (0,0) — 4 R's on top

        assertEquals(GameStatus.P1_WINS, state.getStatus());
    }

    // --- draw ---

    // fill entire board with no 4-in-a-row
    @Test
    void fullBoardIsDraw() {
        Connect4State state = initialState;
        int[] moves = {
                0,1,0,1,0,1,1,0,1,0,1,0,
                2,3,2,3,2,3,3,2,3,2,3,2,
                4,5,4,5,4,5,5,4,5,4,5,4,
                6,6,6,6,6,6
        };
        for (int col : moves)
            state = engine.applyMove(state, move(col));

        assertEquals(GameStatus.DRAW, state.getStatus());
    }

    // --- edge cases ---

    // only 3 in a row shouldn't end the game
    @Test
    void threeInARowNotEnough() {
        Connect4State state = initialState;
        state = engine.applyMove(state, move(0)); // R
        state = engine.applyMove(state, move(6)); // Y
        state = engine.applyMove(state, move(1)); // R
        state = engine.applyMove(state, move(6)); // Y
        state = engine.applyMove(state, move(2)); // R — only 3

        assertEquals(GameStatus.IN_PROGRESS, state.getStatus());
    }

    // evaluateEnd on a game still going should return the same object
    @Test
    void evaluateEndDoesNothingMidGame() {
        assertSame(initialState, engine.evaluateEnd(initialState));
    }
}