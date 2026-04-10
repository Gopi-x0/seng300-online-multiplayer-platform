package ca.ucalgary.seng300.user;

import ca.ucalgary.seng300.model.UserData;
import ca.ucalgary.seng300.service.UserManager;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

public class PlayerGameRecordsTests {

    @BeforeEach
    void setUp() {
        // Ensure no user is logged in before each test
        UserManager.logout();
    }

    // recordGameResult() Tests - Tic Tac Toe

    @Test
    void testRecordGameResultCorrect_TicTacToeWin() {
        UserManager.register("ttt@test.com", "password123");
        UserManager.login("ttt@test.com", "password123");
        UserData user = UserManager.getCurrentUser();
        int winsBefore = user.getTttWins();
        int pointsBefore = user.getTttPoints();
        int gamesBefore = user.getTttGamesPlayed();

        UserManager.recordGameResult("Tic Tac Toe", true, false);

        assertEquals(winsBefore + 1, user.getTttWins());
        assertEquals(pointsBefore + 50, user.getTttPoints());
        assertEquals(gamesBefore + 1, user.getTttGamesPlayed());
    }

    @Test
    void testRecordGameResultIncorrect_NoUserLoggedIn() {
        // No user logged in (setUp calls logout)
        // Should not throw any exception
        assertDoesNotThrow(() -> {
            UserManager.recordGameResult("Tic Tac Toe", true, false);
        }, "recordGameResult should silently do nothing when no user is logged in");
    }

    @Test
    void testRecordGameResultEdge_TicTacToeDraw() {
        UserManager.register("ttt@test.com", "password123");
        UserManager.login("ttt@test.com", "password123");
        UserData user = UserManager.getCurrentUser();
        int drawsBefore = user.getTttDraws();
        int streakBefore = user.getTttCurrentStreak();

        UserManager.recordGameResult("Tic Tac Toe", false, true);

        assertEquals(drawsBefore + 1, user.getTttDraws(), "increment draws by 1");
        assertEquals(0, user.getTttCurrentStreak(), "TTT current streak should reset to 0 on a draw");
    }

    @Test
    void testRecordGameResultEdge_TicTacToeLoss() {
        UserManager.register("ttt@test.com", "password123");
        UserManager.login("ttt@test.com", "password123");
        UserData user = UserManager.getCurrentUser();
        int lossesBefore = user.getTttLosses();

        UserManager.recordGameResult("Tic Tac Toe", false, false);

        assertEquals(lossesBefore + 1, user.getTttLosses(), "TTT losses should increment by 1");
        assertEquals(0, user.getTttCurrentStreak(), "TTT current streak should reset to 0 on a loss");
    }

    @Test
    void testRecordGameResultEdge_TicTacToePointsCannotGoNegative() {
        UserManager.register("ttt@test.com", "password123");
        UserManager.login("ttt@test.com", "password123");
        UserData user = UserManager.getCurrentUser();
        // Points start at 0 for a new user, a loss should not make them negative
        UserManager.recordGameResult("Tic Tac Toe", false, false);
        assertTrue(user.getTttPoints() >= 0, "TTT points should never go below 0");
    }

    // recordGameResult() Tests - Connect 4

    @Test
    void testRecordGameResultCorrect_Connect4Win() {
        UserManager.register("connect4@test.com", "password123");
        UserManager.login("connect4@test.com", "password123");
        UserData user = UserManager.getCurrentUser();
        int winsBefore = user.getC4Wins();
        int pointsBefore = user.getC4Points();

        UserManager.recordGameResult("Connect 4", true, false);

        assertEquals(winsBefore + 1, user.getC4Wins(), "C4 wins should increment by 1");
        assertEquals(pointsBefore + 50, user.getC4Points(), "C4 points should increase by 50 for a win");
    }

    @Test
    void testRecordGameResultEdge_Connect4DrawResetsStreak() {
        UserManager.register("connect4@test.com", "password123");
        UserManager.login("connect4@test.com", "password123");
        UserData user = UserManager.getCurrentUser();

        // Build up a streak first
        UserManager.recordGameResult("Connect 4", true, false);
        assertTrue(user.getC4CurrentStreak() > 0);

        // A draw should reset it
        UserManager.recordGameResult("Connect 4", false, true);
        assertEquals(0, user.getC4CurrentStreak(), "C4 current streak should reset to 0 on a draw");
    }

    @Test
    void testRecordGameResultEdge_Connect4LossResetsStreak() {
        UserManager.register("connect4@test.com", "password123");
        UserManager.login("connect4@test.com", "password123");
        UserData user = UserManager.getCurrentUser();

        UserManager.recordGameResult("Connect 4", true, false);
        assertTrue(user.getC4CurrentStreak() > 0);

        UserManager.recordGameResult("Connect 4", false, false);
        assertEquals(0, user.getC4CurrentStreak(), "C4 current streak should reset to 0 on a loss");
    }

    @Test
    void testRecordGameResultEdge_WinStreakUpdatesBestStreak() {
        UserManager.register("connect4new2@test.com", "password123");
        UserManager.login("connect4new2@test.com", "password123");

        UserManager.recordGameResult("Connect 4", true, false);
        UserManager.recordGameResult("Connect 4", true, false);
        UserManager.recordGameResult("Connect 4", true, false);

        UserData user = UserManager.getCurrentUser(); // fetch AFTER mutations

        assertEquals(3, user.getC4CurrentStreak(), "C4 current streak should be 3 after 3 consecutive wins");
        assertEquals(3, user.getC4BestStreak(), "C4 best streak should update to match current streak");
    }
}
