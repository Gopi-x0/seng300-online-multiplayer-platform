package ca.ucalgary.seng300.integrationtest;

import ca.ucalgary.seng300.model.GameState;
import ca.ucalgary.seng300.model.GameStatus;
import ca.ucalgary.seng300.model.PlayerProfile;
import ca.ucalgary.seng300.model.UserData;
import ca.ucalgary.seng300.service.GameSessionManager;
import ca.ucalgary.seng300.service.UserManager;
import ca.ucalgary.seng300.tictactoeengine.GameStatusTicTacToeEngine;
import ca.ucalgary.seng300.tictactoeengine.Symbol;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

// Integration tests for games that end in a draw
// We fill the whole board with no winner and check the draw is detected correctly
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class DrawGameIntegrationTest {

    // Try to register the user, if they already exist just log in instead
    private static UserData registerOrLogin(String email, String password) {
        UserManager.logout();
        UserData user = UserManager.register(email, password);
        if (user == null) {
            user = UserManager.login(email, password);
        }
        assertNotNull(user, "Should be able to register or login as " + email);
        return user;
    }

    // Clear any leftover state before the test class runs
    @BeforeAll
    static void globalSetup() {
        UserManager.logout();
        GameSessionManager.endGame(GameSessionManager.getCurrentState());
    }

    // Clean up after each test so the next one starts fresh
    @AfterEach
    void tearDown() {
        GameSessionManager.endGame(GameSessionManager.getCurrentState());
        UserManager.logout();
    }

    // Test a Tic-Tac-Toe draw and check the board state after every move
    @Test
    @Order(1)
    void testFullTTTGame_Draw_ApproachA() {
        // Register Player 1 and save their draw stats before the game
        UserData p1User = registerOrLogin("draw_p1_a@integrationtest.com", "password123");
        UserManager.logout();
        UserManager.login("draw_p1_a@integrationtest.com", "password123");
        int p1DrawsBefore = UserManager.getCurrentUser().getTttDraws();
        int p1GamesBefore = UserManager.getCurrentUser().getTttGamesPlayed();
        int p1PointsBefore = UserManager.getCurrentUser().getTttPoints();

        // Start the game
        PlayerProfile p1 = new PlayerProfile("0", p1User.getUsername());
        PlayerProfile p2 = new PlayerProfile("1", "draw_opponent");
        GameState state = GameSessionManager.startGame("tictactoe", p1, p2);
        assertEquals(GameStatus.IN_PROGRESS, state.getGameStatus());

        ca.ucalgary.seng300.tictactoeengine.GameState engState;

        // X plays top-left, check the cell and that it is now O's turn
        state = GameSessionManager.submitMoveTTT(0, 0);
        engState = (ca.ucalgary.seng300.tictactoeengine.GameState) GameSessionManager.getEngineState();
        assertEquals(Symbol.X, engState.getCell(0, 0));
        assertEquals(GameStatus.IN_PROGRESS, state.getGameStatus());
        assertEquals("1", engState.getActivePlayerId(), "O's turn next");

        // O plays top-middle, check it is now X's turn
        state = GameSessionManager.submitMoveTTT(0, 1);
        engState = (ca.ucalgary.seng300.tictactoeengine.GameState) GameSessionManager.getEngineState();
        assertEquals(Symbol.O, engState.getCell(0, 1));
        assertEquals("0", engState.getActivePlayerId(), "X's turn next");

        // X plays top-right
        state = GameSessionManager.submitMoveTTT(0, 2);
        engState = (ca.ucalgary.seng300.tictactoeengine.GameState) GameSessionManager.getEngineState();
        assertEquals(Symbol.X, engState.getCell(0, 2));

        // O plays middle-left
        state = GameSessionManager.submitMoveTTT(1, 0);
        engState = (ca.ucalgary.seng300.tictactoeengine.GameState) GameSessionManager.getEngineState();
        assertEquals(Symbol.O, engState.getCell(1, 0));

        // X plays center still no winner yet
        state = GameSessionManager.submitMoveTTT(1, 1);
        engState = (ca.ucalgary.seng300.tictactoeengine.GameState) GameSessionManager.getEngineState();
        assertEquals(Symbol.X, engState.getCell(1, 1));
        assertEquals(GameStatus.IN_PROGRESS, state.getGameStatus(), "Still in progress, no win yet");

        // O plays bottom-left
        state = GameSessionManager.submitMoveTTT(2, 0);
        engState = (ca.ucalgary.seng300.tictactoeengine.GameState) GameSessionManager.getEngineState();
        assertEquals(Symbol.O, engState.getCell(2, 0));

        // X plays middle-right
        state = GameSessionManager.submitMoveTTT(1, 2);
        engState = (ca.ucalgary.seng300.tictactoeengine.GameState) GameSessionManager.getEngineState();
        assertEquals(Symbol.X, engState.getCell(1, 2));

        // O plays bottom-right, one cell left
        state = GameSessionManager.submitMoveTTT(2, 2);
        engState = (ca.ucalgary.seng300.tictactoeengine.GameState) GameSessionManager.getEngineState();
        assertEquals(Symbol.O, engState.getCell(2, 2));
        assertEquals(GameStatus.IN_PROGRESS, state.getGameStatus(), "One cell left, still in progress");

        // X plays the last cell, board is full with no winner so it should be a draw
        state = GameSessionManager.submitMoveTTT(2, 1);
        engState = (ca.ucalgary.seng300.tictactoeengine.GameState) GameSessionManager.getEngineState();
        assertEquals(Symbol.X, engState.getCell(2, 1));
        assertEquals(GameStatusTicTacToeEngine.DRAW, engState.getStatus(), "TTT engine should report DRAW");
        assertEquals(GameStatus.DRAW, state.getGameStatus(), "Platform state should report DRAW");

        // Check every cell on the final board matches what we expect
        assertEquals(Symbol.X, engState.getCell(0, 0));
        assertEquals(Symbol.O, engState.getCell(0, 1));
        assertEquals(Symbol.X, engState.getCell(0, 2));
        assertEquals(Symbol.O, engState.getCell(1, 0));
        assertEquals(Symbol.X, engState.getCell(1, 1));
        assertEquals(Symbol.X, engState.getCell(1, 2));
        assertEquals(Symbol.O, engState.getCell(2, 0));
        assertEquals(Symbol.X, engState.getCell(2, 1));
        assertEquals(Symbol.O, engState.getCell(2, 2));

        // Check that all 9 moves were recorded in the history
        assertEquals(9, engState.getMoveHistory().size(), "All 9 cells should have been played");

        // Record the draw for Player 1 and check their stats
        // Points should stay the same on a draw, streak resets to 0
        UserManager.recordGameResult("tictactoe", false, true);
        UserData updated = UserManager.getCurrentUser();
        assertEquals(p1DrawsBefore + 1, updated.getTttDraws(), "Draw count should increment");
        assertEquals(p1GamesBefore + 1, updated.getTttGamesPlayed(), "Games played should increment");
        assertEquals(p1PointsBefore, updated.getTttPoints(), "Points should stay the same on draw");
        assertEquals(0, updated.getTttCurrentStreak(), "Streak resets to 0 on draw");

        // Check the player still has a valid rank after the game
        int rank = UserManager.getUserRank(updated);
        assertTrue(rank >= 1, "Rank should be a positive integer");

        // End the game and log out
        GameSessionManager.endGame(state);
        assertNull(GameSessionManager.getCurrentState());
        UserManager.logout();
    }

    // Test a different draw layout and record the draw result for both players separately

    @Test
    @Order(2)
    void testFullTTTGame_Draw_ApproachB_BothPlayersRecordStats() {
        // Register both players and save Player 1's draw count
        UserData p1User = registerOrLogin("draw_p1_b@integrationtest.com", "password123");
        UserData p2User = registerOrLogin("draw_p2_b@integrationtest.com", "password123");

        UserManager.logout();
        UserManager.login("draw_p1_b@integrationtest.com", "password123");
        int p1DrawsBefore = UserManager.getCurrentUser().getTttDraws();

        // Start the game
        PlayerProfile p1 = new PlayerProfile("0", p1User.getUsername());
        PlayerProfile p2 = new PlayerProfile("1", p2User.getUsername());
        GameState state = GameSessionManager.startGame("tictactoe", p1, p2);

        // Play all 9 moves filling the board with no winner
        state = GameSessionManager.submitMoveTTT(1, 1); // X center
        assertEquals(GameStatus.IN_PROGRESS, state.getGameStatus());

        state = GameSessionManager.submitMoveTTT(0, 0); // O top-left
        assertEquals(GameStatus.IN_PROGRESS, state.getGameStatus());

        state = GameSessionManager.submitMoveTTT(0, 2); // X top-right
        assertEquals(GameStatus.IN_PROGRESS, state.getGameStatus());

        state = GameSessionManager.submitMoveTTT(2, 0); // O bottom-left
        assertEquals(GameStatus.IN_PROGRESS, state.getGameStatus());

        state = GameSessionManager.submitMoveTTT(1, 0); // X middle-left
        assertEquals(GameStatus.IN_PROGRESS, state.getGameStatus());

        state = GameSessionManager.submitMoveTTT(1, 2); // O middle-right
        assertEquals(GameStatus.IN_PROGRESS, state.getGameStatus());

        state = GameSessionManager.submitMoveTTT(0, 1); // X top-middle
        assertEquals(GameStatus.IN_PROGRESS, state.getGameStatus());

        state = GameSessionManager.submitMoveTTT(2, 1); // O bottom-middle
        assertEquals(GameStatus.IN_PROGRESS, state.getGameStatus());

        // X plays the last cell, board full with no winner so it should be a draw
        state = GameSessionManager.submitMoveTTT(2, 2);
        assertEquals(GameStatus.DRAW, state.getGameStatus(), "Should be a draw - board full, no winner");

        // Check the engine also reports a draw
        ca.ucalgary.seng300.tictactoeengine.GameState engState =
                (ca.ucalgary.seng300.tictactoeengine.GameState) GameSessionManager.getEngineState();
        assertEquals(GameStatusTicTacToeEngine.DRAW, engState.getStatus());

        // Record the draw for Player 1 and check their draw count went up
        UserManager.recordGameResult("tictactoe", false, true);
        assertEquals(p1DrawsBefore + 1, UserManager.getCurrentUser().getTttDraws());

        // Switch to Player 2 and record their draw result too
        UserManager.logout();
        UserData loggedInP2 = UserManager.login("draw_p2_b@integrationtest.com", "password123");
        assertNotNull(loggedInP2);
        int p2DrawsBefore = loggedInP2.getTttDraws();

        UserManager.recordGameResult("tictactoe", false, true);
        assertEquals(p2DrawsBefore + 1, UserManager.getCurrentUser().getTttDraws(),
                "Player 2's draw count should also increment");
        assertEquals(0, UserManager.getCurrentUser().getTttCurrentStreak(),
                "Player 2's streak should reset on draw");

        // Check both players each have at least 1 total game recorded
        UserManager.logout();
        UserManager.login("draw_p1_b@integrationtest.com", "password123");
        assertTrue(UserManager.getCurrentUser().getTotalGames() >= 1);

        UserManager.logout();
        UserManager.login("draw_p2_b@integrationtest.com", "password123");
        assertTrue(UserManager.getCurrentUser().getTotalGames() >= 1);

        // End the game and log out
        GameSessionManager.endGame(state);
        UserManager.logout();
    }
}
