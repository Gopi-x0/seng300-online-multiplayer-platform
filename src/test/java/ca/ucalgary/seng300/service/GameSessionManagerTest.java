package ca.ucalgary.seng300.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import ca.ucalgary.seng300.model.*;

public class GameSessionManagerTest {

    PlayerProfile p1;
    PlayerProfile p2;

    @BeforeEach
    void setup() {
        p1 = new PlayerProfile("0", "Alice");
        p2 = new PlayerProfile("1", "Bob");

        // Reset any previous game
        GameSessionManager.endGame(GameSessionManager.getCurrentState());
    }

    // Verifies that a Connect4 game initializes correctly with valid state
    @Test
    void startGame_Connect4() {
        GameState state = GameSessionManager.startGame("connect4", p1, p2);

        assertNotNull(state);
        assertEquals("connect4", state.getGameType());
        assertEquals(GameStatus.IN_PROGRESS, state.getGameStatus());
        assertEquals(0, state.getActivePlayerIndex());
        assertNotNull(state.getBoardPayLoad());
    }

    // Verifies that a TicTacToe game initializes correctly with valid state
    @Test
    void startGame_TicTacToe() {
        GameState state = GameSessionManager.startGame("tictactoe", p1, p2);

        assertNotNull(state);
        assertEquals("tictactoe", state.getGameType());
        assertEquals(GameStatus.IN_PROGRESS, state.getGameStatus());
        assertEquals(0, state.getActivePlayerIndex());
    }

    // Verifies that a valid Connect4 move updates the game state
    @Test
    void submitMove_Connect4_Valid() {
        GameSessionManager.startGame("connect4", p1, p2);

        GameState state = GameSessionManager.submitMove(0);

        assertNotNull(state);
        assertEquals("connect4", state.getGameType());
        assertNotNull(state.getBoardPayLoad());
    }

    // Verifies that an invalid Connect4 move does not change the state
    @Test
    void submitMove_Connect4_Invalid() {
        GameSessionManager.startGame("connect4", p1, p2);

        GameState before = GameSessionManager.getCurrentState();
        GameState after = GameSessionManager.submitMove(100); // invalid column

        assertEquals(before, after);
    }

    // Verifies that a valid TicTacToe move updates the game state
    @Test
    void submitMoveTTT_Valid() {
        GameSessionManager.startGame("tictactoe", p1, p2);

        GameState state = GameSessionManager.submitMoveTTT(0, 0);

        assertNotNull(state);
        assertEquals("tictactoe", state.getGameType());
        assertNotNull(state.getBoardPayLoad());
    }

    // Verifies that an invalid TicTacToe move does not alter the state
    @Test
    void submitMoveTTT_Invalid() {
        GameSessionManager.startGame("tictactoe", p1, p2);

        GameSessionManager.submitMoveTTT(0, 0);
        GameState before = GameSessionManager.getCurrentState();

        GameState after = GameSessionManager.submitMoveTTT(0, 0); // same spot

        assertEquals(before, after);
    }

    // Verifies that submitting a move without starting a game returns null
    @Test
    void submitMove_NoGame() {

        GameSessionManager.endGame(GameSessionManager.getCurrentState());

        GameState state = GameSessionManager.submitMove(0);

        assertNull(state);
    }

    // Verifies that endGame clears all internal game state
    @Test
    void endGame_ClearsState() {
        GameSessionManager.startGame("connect4", p1, p2);

        GameSessionManager.endGame(GameSessionManager.getCurrentState());

        assertNull(GameSessionManager.getCurrentState());
        assertNull(GameSessionManager.getEngineState());
    }

    // Verifies that getCurrentState returns the correct state after starting a game
    @Test
    void getCurrentState_AfterStartGame() {

        GameState state = GameSessionManager.startGame("connect4", p1, p2);

        GameState current = GameSessionManager.getCurrentState();

        assertNotNull(current);
        assertEquals(state, current);
    }

    // Verifies that getEngineState returns a non-null engine state after game start
    @Test
    void getEngineState_AfterStartGame() {

        GameSessionManager.startGame("connect4", p1, p2);

        Object engineState = GameSessionManager.getEngineState();

        assertNotNull(engineState);
    }

    // Verifies that getCurrentState reflects updates after a move
    @Test
    void getCurrentState_AfterMove() {

        GameSessionManager.startGame("connect4", p1, p2);

        GameSessionManager.submitMove(0);

        GameState current = GameSessionManager.getCurrentState();

        assertNotNull(current);
        assertEquals("connect4", current.getGameType());
    }

    // Verifies that engine state changes after a valid move is applied
    @Test
    void getEngineState_AfterMove() {

        GameSessionManager.startGame("connect4", p1, p2);

        Object before = GameSessionManager.getEngineState();

        GameSessionManager.submitMove(0);

        Object after = GameSessionManager.getEngineState();

        assertNotNull(after);
        assertNotEquals(before, after);
    }

    // Verifies that both getters return null after ending the game
    @Test
    void getters_AfterEndGame() {

        GameSessionManager.startGame("connect4", p1, p2);

        GameSessionManager.endGame(GameSessionManager.getCurrentState());

        assertNull(GameSessionManager.getCurrentState());
        assertNull(GameSessionManager.getEngineState());
    }
}