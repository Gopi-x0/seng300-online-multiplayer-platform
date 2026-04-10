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

// Integration tests for a full Tic-Tac-Toe game
// We test Player 1 winning via top row and via diagonal
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TicTacToeFullGameIntegrationTest {

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

    // Test that Player 1 wins by filling the top row with X

    @Test
    @Order(1)
    void testFullTTTGame_Player1WinsTopRow_ApproachA() {
        // Register both players
        UserData player1User = registerOrLogin("ttt_p1_a@integrationtest.com", "password123");
        int p1TttGamesBefore = player1User.getTttGamesPlayed();
        int p1TttWinsBefore = player1User.getTttWins();
        int p1TttPointsBefore = player1User.getTttPoints();

        UserData player2User = registerOrLogin("ttt_p2_a@integrationtest.com", "password123");

        // Log Player 1 back in so their stats get updated at the end
        UserManager.logout();
        UserData loggedIn = UserManager.login("ttt_p1_a@integrationtest.com", "password123");
        assertNotNull(loggedIn, "Player 1 login should succeed");
        assertEquals(player1User.getEmail(), UserManager.getCurrentUser().getEmail());

        // IDs must be "0" and "1" because GameSessionManager parses them as integers
        PlayerProfile p1 = new PlayerProfile("0", player1User.getUsername());
        PlayerProfile p2 = new PlayerProfile("1", player2User.getUsername());

        // Start the game and check it was created correctly
        GameState state = GameSessionManager.startGame("tictactoe", p1, p2);
        assertNotNull(state, "Game should be created");
        assertEquals("tictactoe", state.getGameType());
        assertEquals(GameStatus.IN_PROGRESS, state.getGameStatus());
        assertEquals(0, state.getActivePlayerIndex(), "Player 0 (X) should go first");

        // X plays top-left
        state = GameSessionManager.submitMoveTTT(0, 0);
        assertNotNull(state);
        assertEquals(GameStatus.IN_PROGRESS, state.getGameStatus());

        // O plays middle-left
        state = GameSessionManager.submitMoveTTT(1, 0);
        assertNotNull(state);
        assertEquals(GameStatus.IN_PROGRESS, state.getGameStatus());

        // X plays top-middle
        state = GameSessionManager.submitMoveTTT(0, 1);
        assertNotNull(state);
        assertEquals(GameStatus.IN_PROGRESS, state.getGameStatus());

        // O plays middle-center
        state = GameSessionManager.submitMoveTTT(1, 1);
        assertNotNull(state);
        assertEquals(GameStatus.IN_PROGRESS, state.getGameStatus());

        // X plays top-right and completes the top row, X wins
        state = GameSessionManager.submitMoveTTT(0, 2);
        assertNotNull(state);
        assertEquals(GameStatus.P1_WINS, state.getGameStatus(), "X should win with top row complete");

        // Double check the engine also agrees X won and the board looks right
        ca.ucalgary.seng300.tictactoeengine.GameState engineState =
                (ca.ucalgary.seng300.tictactoeengine.GameState) GameSessionManager.getEngineState();
        assertNotNull(engineState);
        assertEquals(GameStatusTicTacToeEngine.X_WINS, engineState.getStatus());
        assertEquals(Symbol.X, engineState.getCell(0, 0));
        assertEquals(Symbol.X, engineState.getCell(0, 1));
        assertEquals(Symbol.X, engineState.getCell(0, 2));
        assertEquals(Symbol.O, engineState.getCell(1, 0));
        assertEquals(Symbol.O, engineState.getCell(1, 1));

        // Record the win for Player 1 and check their stats went up correctly
        UserManager.recordGameResult("tictactoe", true, false);
        UserData updatedP1 = UserManager.getCurrentUser();
        assertEquals(p1TttGamesBefore + 1, updatedP1.getTttGamesPlayed(), "Games played should increment by 1");
        assertEquals(p1TttWinsBefore + 1, updatedP1.getTttWins(), "Wins should increment by 1");
        assertEquals(p1TttPointsBefore + 50, updatedP1.getTttPoints(), "Points should increase by 50 for a win");

        // End the game and make sure everything is cleared
        GameSessionManager.endGame(state);
        assertNull(GameSessionManager.getCurrentState(), "Game state should be cleared after endGame");
        assertNull(GameSessionManager.getEngineState(), "Engine state should be cleared after endGame");
    }

    // Test that Player 1 wins by completing the main diagonal

    @Test
    @Order(2)
    void testFullTTTGame_Player1WinsDiagonal_ApproachB() {
        // Register both players
        UserData player1User = registerOrLogin("ttt_p1_b@integrationtest.com", "password123");
        UserData player2User = registerOrLogin("ttt_p2_b@integrationtest.com", "password123");

        // Log Player 1 back in and save their current win stats before the game
        UserManager.logout();
        UserManager.login("ttt_p1_b@integrationtest.com", "password123");
        int p1WinsBefore = UserManager.getCurrentUser().getTttWins();
        int p1StreakBefore = UserManager.getCurrentUser().getTttCurrentStreak();

        // Start the game
        PlayerProfile p1 = new PlayerProfile("0", player1User.getUsername());
        PlayerProfile p2 = new PlayerProfile("1", player2User.getUsername());
        GameState state = GameSessionManager.startGame("tictactoe", p1, p2);

        // Check the engine starts with Player 0 (X) going first
        ca.ucalgary.seng300.tictactoeengine.GameState engState =
                (ca.ucalgary.seng300.tictactoeengine.GameState) GameSessionManager.getEngineState();
        assertEquals("0", engState.getActivePlayerId(), "Player 0 should start");
        assertEquals(GameStatusTicTacToeEngine.IN_PROGRESS, engState.getStatus());

        // X plays top-left, check the cell and that it is now O's turn
        state = GameSessionManager.submitMoveTTT(0, 0);
        engState = (ca.ucalgary.seng300.tictactoeengine.GameState) GameSessionManager.getEngineState();
        assertEquals(Symbol.X, engState.getCell(0, 0), "Cell (0,0) should be X after move 1");
        assertEquals("1", engState.getActivePlayerId(), "After X moves, O's turn (player 1)");

        // O plays top-middle, check the cell and that it is now X's turn
        state = GameSessionManager.submitMoveTTT(0, 1);
        engState = (ca.ucalgary.seng300.tictactoeengine.GameState) GameSessionManager.getEngineState();
        assertEquals(Symbol.O, engState.getCell(0, 1), "Cell (0,1) should be O after move 2");
        assertEquals("0", engState.getActivePlayerId(), "After O moves, X's turn (player 0)");

        // X plays center
        state = GameSessionManager.submitMoveTTT(1, 1);
        engState = (ca.ucalgary.seng300.tictactoeengine.GameState) GameSessionManager.getEngineState();
        assertEquals(Symbol.X, engState.getCell(1, 1), "Cell (1,1) should be X after move 3");
        assertEquals("1", engState.getActivePlayerId());

        // O plays top-right
        state = GameSessionManager.submitMoveTTT(0, 2);
        engState = (ca.ucalgary.seng300.tictactoeengine.GameState) GameSessionManager.getEngineState();
        assertEquals(Symbol.O, engState.getCell(0, 2), "Cell (0,2) should be O after move 4");
        assertEquals(GameStatus.IN_PROGRESS, state.getGameStatus(), "Game still in progress");

        // X plays bottom-right to complete the diagonal and win
        state = GameSessionManager.submitMoveTTT(2, 2);
        engState = (ca.ucalgary.seng300.tictactoeengine.GameState) GameSessionManager.getEngineState();
        assertEquals(Symbol.X, engState.getCell(2, 2));
        assertEquals(GameStatusTicTacToeEngine.X_WINS, engState.getStatus(), "X should win via diagonal");
        assertEquals(GameStatus.P1_WINS, state.getGameStatus(), "Platform state should show P1_WINS");

        // Record the win and check wins and streak both went up
        UserManager.recordGameResult("tictactoe", true, false);
        UserData updated = UserManager.getCurrentUser();
        assertEquals(p1WinsBefore + 1, updated.getTttWins());
        assertEquals(p1StreakBefore + 1, updated.getTttCurrentStreak(), "Win streak should increment");

        // End the game and log out
        GameSessionManager.endGame(state);
        assertNull(GameSessionManager.getCurrentState());

        UserManager.logout();
        assertNull(UserManager.getCurrentUser(), "No user should be logged in after logout");
    }
}
