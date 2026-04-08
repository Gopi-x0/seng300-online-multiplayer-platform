package ca.ucalgary.seng300.gamelogic;

import ca.ucalgary.seng300.tictactoeengine.*;
import ca.ucalgary.seng300.interfaces.Move;
import ca.ucalgary.seng300.interfaces.MoveResult;
import ca.ucalgary.seng300.interfaces.ValidationCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TicTacToeTest {

    private TicTacToeEngine engine;
    private GameState initialState;

    private static final String PLAYER_X = "player_x";
    private static final String PLAYER_O = "player_o";

    private Move xMove(int r, int c) { return new Move(r, c, PLAYER_X, "X"); }
    private Move oMove(int r, int c) { return new Move(r, c, PLAYER_O, "O"); }

    @BeforeEach
    void setUp() {
        engine = new TicTacToeEngine();
        initialState = engine.createInitialState(List.of(PLAYER_X, PLAYER_O));
    }

    // --- setup and creation ---

    // fresh game should be in progress, X goes first, board empty, no history
    @Test
    void newGameSetUpCorrectly() {
        assertNotNull(initialState);
        assertEquals(GameStatusTicTacToeEngine.IN_PROGRESS, initialState.getStatus());
        assertEquals(PLAYER_X, initialState.getActivePlayerId());
        assertEquals(0, initialState.getMoveHistory().size());
        Symbol[][] board = initialState.getBoard();
        for (int r = 0; r < 3; r++)
            for (int c = 0; c < 3; c++)
                assertEquals(Symbol.EMPTY, board[r][c]);
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

    // --- making moves ---

    // dropping X at center should show up on the board
    @Test
    void moveShowsOnBoard() {
        GameState next = engine.applyMove(initialState, xMove(1, 1));
        assertEquals(Symbol.X, next.getBoard()[1][1]);
    }

    // applying a move shouldn't change the old state
    @Test
    void oldStateUnchanged() {
        engine.applyMove(initialState, xMove(2, 2));
        assertEquals(Symbol.EMPTY, initialState.getBoard()[2][2]);
    }

    // turns should go X, O, X, O...
    @Test
    void turnsAlternate() {
        GameState state = initialState;
        assertEquals(PLAYER_X, state.getActivePlayerId());
        state = engine.applyMove(state, xMove(0, 0));
        assertEquals(PLAYER_O, state.getActivePlayerId());
        state = engine.applyMove(state, oMove(1, 1));
        assertEquals(PLAYER_X, state.getActivePlayerId());
    }

    // move history should track what was played
    @Test
    void historyTrackedCorrectly() {
        GameState state = engine.applyMove(initialState, xMove(0, 2));
        assertEquals(1, state.getMoveHistory().size());
        assertEquals(0, state.getMoveHistory().get(0).getRow());
        assertEquals(2, state.getMoveHistory().get(0).getCol());

        state = engine.applyMove(state, oMove(1, 1));
        assertEquals(2, state.getMoveHistory().size());
    }

    // player list shouldn't change after moves
    @Test
    void playerIdsStaySame() {
        GameState state = engine.applyMove(initialState, xMove(0, 0));
        assertEquals(List.of(PLAYER_X, PLAYER_O), state.getPlayerIds());
    }

    // getBoard should return a copy so you can't cheat by editing it
    @Test
    void boardIsCopiedNotShared() {
        Symbol[][] board1 = initialState.getBoard();
        Symbol[][] board2 = initialState.getBoard();
        assertNotSame(board1, board2);
        board1[0][0] = Symbol.X;
        assertEquals(Symbol.EMPTY, initialState.getBoard()[0][0]);
    }

    // --- validation ---

    // a normal move on an empty cell should be fine
    @Test
    void validMoveAccepted() {
        MoveResult result = engine.validateMove(initialState, xMove(0, 0));
        assertTrue(result.isValid());
        assertEquals(ValidationCode.SUCCESS, result.getErrorCode());
    }

    // anything outside the 3x3 grid should be rejected
    @Test
    void outOfBoundsRejected() {
        assertFalse(engine.validateMove(initialState, new Move(-1, 0, PLAYER_X, "X")).isValid());
        assertFalse(engine.validateMove(initialState, new Move(0, -1, PLAYER_X, "X")).isValid());
        assertFalse(engine.validateMove(initialState, new Move(3, 0, PLAYER_X, "X")).isValid());
        assertEquals(ValidationCode.OUT_OF_BOUNDS,
                engine.validateMove(initialState, new Move(0, 3, PLAYER_X, "X")).getErrorCode());
    }

    // O can't go when it's X's turn
    @Test
    void wrongTurnRejected() {
        MoveResult result = engine.validateMove(initialState, oMove(0, 0));
        assertFalse(result.isValid());
        assertEquals(ValidationCode.WRONG_TURN, result.getErrorCode());
    }

    // can't place on a cell that's already taken
    @Test
    void takenCellRejected() {
        GameState state = engine.applyMove(initialState, xMove(0, 0));
        MoveResult result = engine.validateMove(state, oMove(0, 0));
        assertFalse(result.isValid());
        assertEquals(ValidationCode.CELL_OCCUPIED, result.getErrorCode());
    }

    // can't play on your own piece either
    @Test
    void cantPlayOnOwnPiece() {
        GameState state = engine.applyMove(initialState, xMove(1, 1));
        state = engine.applyMove(state, oMove(0, 0));
        MoveResult result = engine.validateMove(state, xMove(1, 1));
        assertFalse(result.isValid());
        assertEquals(ValidationCode.CELL_OCCUPIED, result.getErrorCode());
    }

    // no moves allowed once someone has won
    @Test
    void cantMoveAfterWin() {
        GameState state = initialState;
        state = engine.applyMove(state, xMove(0, 0));
        state = engine.applyMove(state, oMove(1, 0));
        state = engine.applyMove(state, xMove(0, 1));
        state = engine.applyMove(state, oMove(2, 0));
        state = engine.applyMove(state, xMove(0, 2)); // X wins top row

        MoveResult result = engine.validateMove(state, oMove(1, 1));
        assertFalse(result.isValid());
        assertEquals(ValidationCode.GAME_ALREADY_OVER, result.getErrorCode());
    }

    // game-over check should happen before wrong-turn check
    @Test
    void gameOverCheckedBeforeWrongTurn() {
        GameState state = initialState;
        state = engine.applyMove(state, xMove(0, 0));
        state = engine.applyMove(state, oMove(1, 0));
        state = engine.applyMove(state, xMove(0, 1));
        state = engine.applyMove(state, oMove(2, 0));
        state = engine.applyMove(state, xMove(0, 2)); // X wins, turn goes to O

        // X tries to move even though it's O's turn AND game is over
        MoveResult result = engine.validateMove(state, xMove(2, 2));
        assertEquals(ValidationCode.GAME_ALREADY_OVER, result.getErrorCode());
    }

    // --- wins ---

    // X gets a full row
    @Test
    void xWinsWithRow() {
        GameState state = initialState;
        state = engine.applyMove(state, xMove(0, 0));
        state = engine.applyMove(state, oMove(1, 0));
        state = engine.applyMove(state, xMove(0, 1));
        state = engine.applyMove(state, oMove(2, 0));
        state = engine.applyMove(state, xMove(0, 2));
        assertEquals(GameStatusTicTacToeEngine.X_WINS, state.getStatus());
    }

    // X gets a full column
    @Test
    void xWinsWithColumn() {
        GameState state = initialState;
        state = engine.applyMove(state, xMove(0, 0));
        state = engine.applyMove(state, oMove(0, 1));
        state = engine.applyMove(state, xMove(1, 0));
        state = engine.applyMove(state, oMove(1, 1));
        state = engine.applyMove(state, xMove(2, 0));
        assertEquals(GameStatusTicTacToeEngine.X_WINS, state.getStatus());
    }

    // X gets top-left to bottom-right diagonal
    @Test
    void xWinsWithDiagonal() {
        GameState state = initialState;
        state = engine.applyMove(state, xMove(0, 0));
        state = engine.applyMove(state, oMove(0, 1));
        state = engine.applyMove(state, xMove(1, 1));
        state = engine.applyMove(state, oMove(0, 2));
        state = engine.applyMove(state, xMove(2, 2));
        assertEquals(GameStatusTicTacToeEngine.X_WINS, state.getStatus());
    }

    // X gets top-right to bottom-left diagonal
    @Test
    void xWinsWithAntiDiagonal() {
        GameState state = initialState;
        state = engine.applyMove(state, xMove(0, 2));
        state = engine.applyMove(state, oMove(0, 0));
        state = engine.applyMove(state, xMove(1, 1));
        state = engine.applyMove(state, oMove(0, 1));
        state = engine.applyMove(state, xMove(2, 0));
        assertEquals(GameStatusTicTacToeEngine.X_WINS, state.getStatus());
    }

    // O should be able to win too
    @Test
    void oCanWin() {
        GameState state = initialState;
        state = engine.applyMove(state, xMove(0, 1));
        state = engine.applyMove(state, oMove(0, 0));
        state = engine.applyMove(state, xMove(0, 2));
        state = engine.applyMove(state, oMove(1, 1));
        state = engine.applyMove(state, xMove(1, 0));
        state = engine.applyMove(state, oMove(2, 2)); // O diagonal win
        assertEquals(GameStatusTicTacToeEngine.O_WINS, state.getStatus());
    }

    // --- draw ---

    // fill the whole board with no winner
    @Test
    void fullBoardIsDraw() {
        // board ends up as:  X O X / X X O / O X O
        GameState state = initialState;
        state = engine.applyMove(state, xMove(0, 0));
        state = engine.applyMove(state, oMove(0, 1));
        state = engine.applyMove(state, xMove(0, 2));
        state = engine.applyMove(state, oMove(1, 2));
        state = engine.applyMove(state, xMove(1, 0));
        state = engine.applyMove(state, oMove(2, 0));
        state = engine.applyMove(state, xMove(1, 1));
        state = engine.applyMove(state, oMove(2, 2));
        state = engine.applyMove(state, xMove(2, 1));
        assertEquals(GameStatusTicTacToeEngine.DRAW, state.getStatus());
    }

    // --- edge cases ---

    // winning on the 9th move should count as a win, not a draw
    // board: X O O / O X X / X O X → X wins diagonal
    @Test
    void winOnLastMoveBeatsDrawCheck() {
        GameState state = initialState;
        state = engine.applyMove(state, xMove(0, 0));
        state = engine.applyMove(state, oMove(0, 1));
        state = engine.applyMove(state, xMove(1, 1));
        state = engine.applyMove(state, oMove(0, 2));
        state = engine.applyMove(state, xMove(2, 0));
        state = engine.applyMove(state, oMove(1, 0));
        state = engine.applyMove(state, xMove(1, 2));
        state = engine.applyMove(state, oMove(2, 1));
        state = engine.applyMove(state, xMove(2, 2));
        assertEquals(GameStatusTicTacToeEngine.X_WINS, state.getStatus());
    }

    // two in a row shouldn't trigger anything
    @Test
    void twoInARowNotAWin() {
        GameState state = initialState;
        state = engine.applyMove(state, xMove(0, 0));
        state = engine.applyMove(state, oMove(1, 0));
        state = engine.applyMove(state, xMove(0, 1));
        assertEquals(GameStatusTicTacToeEngine.IN_PROGRESS, state.getStatus());
    }

    // evaluateEnd on a game still going should give back the same state
    @Test
    void evaluateEndDoesNothingMidGame() {
        GameState state = engine.applyMove(initialState, xMove(0, 0));
        assertSame(state, engine.evaluateEnd(state));
    }
}