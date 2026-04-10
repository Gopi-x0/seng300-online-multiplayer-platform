package ca.ucalgary.seng300.integrationtest;

import ca.ucalgary.seng300.connect4engine.Connect4State;
import ca.ucalgary.seng300.model.GameState;
import ca.ucalgary.seng300.model.GameStatus;
import ca.ucalgary.seng300.model.PlayerProfile;
import ca.ucalgary.seng300.model.UserData;
import ca.ucalgary.seng300.service.GameSessionManager;
import ca.ucalgary.seng300.service.UserManager;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

// Integration tests for a full Connect 4 game
// We test Player 2 winning vertically and Player 1 winning horizontally
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class Connect4FullGameIntegrationTest {

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

    // Test that Player 2 wins by stacking 4 yellows in column 3

    @Test
    @Order(1)
    void testFullC4Game_Player2WinsVertical_ApproachA() {
        // Register both players
        UserData p1User = registerOrLogin("c4_p1_a@integrationtest.com", "password123");
        UserData p2User = registerOrLogin("c4_p2_a@integrationtest.com", "password123");

        // Log Player 2 back in and save their stats before the game
        UserManager.logout();
        UserManager.login("c4_p2_a@integrationtest.com", "password123");
        int p2C4GamesBefore = UserManager.getCurrentUser().getC4GamesPlayed();
        int p2C4WinsBefore = UserManager.getCurrentUser().getC4Wins();
        int p2C4PointsBefore = UserManager.getCurrentUser().getC4Points();

        // Start the Connect 4 game
        PlayerProfile p1 = new PlayerProfile("0", p1User.getUsername());
        PlayerProfile p2 = new PlayerProfile("1", p2User.getUsername());

        GameState state = GameSessionManager.startGame("connect4", p1, p2);
        assertNotNull(state);
        assertEquals("connect4", state.getGameType());
        assertEquals(GameStatus.IN_PROGRESS, state.getGameStatus());

        // Play the moves, Red and Yellow take turns dropping pieces
        state = GameSessionManager.submitMove(0); // Red drops in col 0
        assertEquals(GameStatus.IN_PROGRESS, state.getGameStatus());

        state = GameSessionManager.submitMove(3); // Yellow drops in col 3
        assertEquals(GameStatus.IN_PROGRESS, state.getGameStatus());

        state = GameSessionManager.submitMove(1); // Red drops in col 1
        assertEquals(GameStatus.IN_PROGRESS, state.getGameStatus());

        state = GameSessionManager.submitMove(3); // Yellow drops in col 3
        assertEquals(GameStatus.IN_PROGRESS, state.getGameStatus());

        state = GameSessionManager.submitMove(0); // Red drops in col 0
        assertEquals(GameStatus.IN_PROGRESS, state.getGameStatus());

        state = GameSessionManager.submitMove(3); // Yellow drops in col 3
        assertEquals(GameStatus.IN_PROGRESS, state.getGameStatus());

        state = GameSessionManager.submitMove(1); // Red drops in col 1
        assertEquals(GameStatus.IN_PROGRESS, state.getGameStatus());

        // Yellow drops 4th piece in col 3 and wins
        state = GameSessionManager.submitMove(3);
        assertEquals(GameStatus.P2_WINS, state.getGameStatus(), "Player 2 (Y) should win with vertical 4 in col 3");

        // Check the engine agrees Yellow won
        Connect4State c4State = (Connect4State) GameSessionManager.getEngineState();
        assertNotNull(c4State.getWinner(), "There should be a winner");
        assertEquals('Y', c4State.getWinner().getSymbol(), "Winner should be Yellow (Player 2)");
        assertEquals("1", c4State.getWinner().getId(), "Winner ID should be player 1 (second player)");

        // Check that column 3 actually has 4 yellows stacked up
        char[][] grid = c4State.getBoard().getGrid();
        assertEquals('Y', grid[5][3]);
        assertEquals('Y', grid[4][3]);
        assertEquals('Y', grid[3][3]);
        assertEquals('Y', grid[2][3]);

        // Record the win for Player 2 and check their stats went up correctly
        UserManager.recordGameResult("connect4", true, false);
        UserData updatedP2 = UserManager.getCurrentUser();
        assertEquals(p2C4GamesBefore + 1, updatedP2.getC4GamesPlayed());
        assertEquals(p2C4WinsBefore + 1, updatedP2.getC4Wins());
        assertEquals(p2C4PointsBefore + 50, updatedP2.getC4Points());

        // End the game and make sure state is cleared
        GameSessionManager.endGame(state);
        assertNull(GameSessionManager.getCurrentState());

        UserManager.logout();
        assertNull(UserManager.getCurrentUser());
    }

    // Test that Player 1 wins by filling 4 reds across the bottom row
    // Also checks the board state after every single move to make sure gravity and turn switching work
    // Board goal:
    //   Y Y Y
    //   R R R R
    @Test
    @Order(2)
    void testFullC4Game_Player1WinsHorizontal_ApproachB() {
        // Register Player 1 and save their loss count before the game
        UserData p1User = registerOrLogin("c4_p1_b@integrationtest.com", "password123");
        UserManager.logout();
        UserManager.login("c4_p1_b@integrationtest.com", "password123");
        int p1C4LossesBefore = UserManager.getCurrentUser().getC4Losses();

        // Start the game
        PlayerProfile p1 = new PlayerProfile("0", p1User.getUsername());
        PlayerProfile p2 = new PlayerProfile("1", "opponent_player");
        GameState state = GameSessionManager.startGame("connect4", p1, p2);

        // Check the engine starts with Red going first
        Connect4State c4State = (Connect4State) GameSessionManager.getEngineState();
        assertEquals('R', c4State.getCurrentPlayer().getSymbol(), "Red should go first");
        assertEquals("0", c4State.getCurrentPlayer().getId());
        assertEquals(GameStatus.IN_PROGRESS, c4State.getStatus());

        // Red drops in col 0, check it lands on the bottom row and Yellow's turn starts
        state = GameSessionManager.submitMove(0);
        c4State = (Connect4State) GameSessionManager.getEngineState();
        char[][] grid = c4State.getBoard().getGrid();
        assertEquals('R', grid[5][0], "R should land at bottom of col 0 (gravity)");
        assertEquals('Y', c4State.getCurrentPlayer().getSymbol(), "Y's turn after R moves");

        // Yellow drops in col 0, stacks on top of Red
        state = GameSessionManager.submitMove(0);
        c4State = (Connect4State) GameSessionManager.getEngineState();
        grid = c4State.getBoard().getGrid();
        assertEquals('Y', grid[4][0], "Y should land on top of R in col 0");
        assertEquals('R', c4State.getCurrentPlayer().getSymbol(), "R's turn");

        // Red drops in col 1
        state = GameSessionManager.submitMove(1);
        c4State = (Connect4State) GameSessionManager.getEngineState();
        grid = c4State.getBoard().getGrid();
        assertEquals('R', grid[5][1], "R at bottom of col 1");

        // Yellow drops in col 1
        state = GameSessionManager.submitMove(1);
        c4State = (Connect4State) GameSessionManager.getEngineState();
        grid = c4State.getBoard().getGrid();
        assertEquals('Y', grid[4][1], "Y stacks on col 1");

        // Red drops in col 2
        state = GameSessionManager.submitMove(2);
        c4State = (Connect4State) GameSessionManager.getEngineState();
        grid = c4State.getBoard().getGrid();
        assertEquals('R', grid[5][2], "R at bottom of col 2");
        assertEquals(GameStatus.IN_PROGRESS, state.getGameStatus(), "Not yet 4 in a row");

        // Yellow drops in col 2
        state = GameSessionManager.submitMove(2);
        c4State = (Connect4State) GameSessionManager.getEngineState();
        grid = c4State.getBoard().getGrid();
        assertEquals('Y', grid[4][2], "Y stacks on col 2");

        // Red drops in col 3 and completes 4 in a row across the bottom
        state = GameSessionManager.submitMove(3);
        assertEquals(GameStatus.P1_WINS, state.getGameStatus(), "R should win with horizontal 4 on bottom row");

        // Check the engine agrees Red won
        c4State = (Connect4State) GameSessionManager.getEngineState();
        assertNotNull(c4State.getWinner());
        assertEquals('R', c4State.getWinner().getSymbol());
        assertEquals("0", c4State.getWinner().getId());

        // Check the bottom row actually has 4 reds in columns 0 to 3
        grid = c4State.getBoard().getGrid();
        assertEquals('R', grid[5][0]);
        assertEquals('R', grid[5][1]);
        assertEquals('R', grid[5][2]);
        assertEquals('R', grid[5][3]);

        // Record a loss for Player 1 and check their loss count and streak reset
        UserManager.recordGameResult("connect4", false, false);
        UserData updatedP1 = UserManager.getCurrentUser();
        assertEquals(p1C4LossesBefore + 1, updatedP1.getC4Losses(), "Losses should increment");
        assertEquals(0, updatedP1.getC4CurrentStreak(), "Streak resets on loss");

        // End the game and log out
        GameSessionManager.endGame(state);
        UserManager.logout();
    }
}
