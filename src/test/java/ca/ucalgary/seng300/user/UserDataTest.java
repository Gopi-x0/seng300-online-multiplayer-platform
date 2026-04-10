package ca.ucalgary.seng300.user;
import org.junit.jupiter.api.Test;
import ca.ucalgary.seng300.model.UserData;
import ca.ucalgary.seng300.service.UserManager;
import org.junit.jupiter.api.*;

import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class UserDataTest {

    UserData user = new UserData("test@email.com", "user1", "1234");


    // Simple Setter and Getter Tests


    @Test
    void testSetGetEmail() {
        user.setEmail("new@email.com");
        assertEquals("new@email.com", user.getEmail());
    }

    @Test
    void testSetGetUsername() {
        user.setUsername("newUser");
        assertEquals("newUser", user.getUsername());
    }

    @Test
    void testSetGetPassword() {
        user.setPassword("newPass");
        assertEquals("newPass", user.getPassword());
    }

    @Test
    void testSetGetTttGamesPlayed() {
        user.setTttGamesPlayed(10);
        assertEquals(10, user.getTttGamesPlayed());
    }

    @Test
    void testSetGetTttWins() {
        user.setTttWins(5);
        assertEquals(5, user.getTttWins());
    }

    @Test
    void testSetGetTttLosses() {
        user.setTttLosses(3);
        assertEquals(3, user.getTttLosses());
    }

    @Test
    void testSetGetTttDraws() {
        user.setTttDraws(2);
        assertEquals(2, user.getTttDraws());
    }

    @Test
    void testSetGetTttCurrentStreak() {
        user.setTttCurrentStreak(4);
        assertEquals(4, user.getTttCurrentStreak());
    }

    @Test
    void testSetGetTttBestStreak() {
        user.setTttBestStreak(7);
        assertEquals(7, user.getTttBestStreak());
    }

    @Test
    void testSetGetTttPoints() {
        user.setTttPoints(100);
        assertEquals(100, user.getTttPoints());
    }

    @Test
    void testSetGetC4GamesPlayed() {
        user.setC4GamesPlayed(8);
        assertEquals(8, user.getC4GamesPlayed());
    }

    @Test
    void testSetGetC4Wins() {
        user.setC4Wins(4);
        assertEquals(4, user.getC4Wins());
    }

    @Test
    void testSetGetC4Losses() {
        user.setC4Losses(2);
        assertEquals(2, user.getC4Losses());
    }

    @Test
    void testSetGetC4Draws() {
        user.setC4Draws(1);
        assertEquals(1, user.getC4Draws());
    }

    @Test
    void testSetGetC4CurrentStreak() {
        user.setC4CurrentStreak(3);
        assertEquals(3, user.getC4CurrentStreak());
    }

    @Test
    void testSetGetC4BestStreak() {
        user.setC4BestStreak(6);
        assertEquals(6, user.getC4BestStreak());
    }

    @Test
    void testSetGetC4Points() {
        user.setC4Points(200);
        assertEquals(200, user.getC4Points());
    }


    // Correct cases


    @Test
    void testConstructorAndGetters() {
        assertEquals("test@email.com", user.getEmail());
        assertEquals("user1", user.getUsername());
        assertEquals("1234", user.getPassword());
    }

    @Test
    void testDerivedStatsCorrect() {
        user.setTttGamesPlayed(10);
        user.setTttWins(6);

        user.setC4GamesPlayed(5);
        user.setC4Wins(2);

        assertEquals(15, user.getTotalGames());
        assertEquals(8, user.getTotalWins());
        assertEquals((8.0 / 15) * 100, user.getOverallWinRate());
    }

    @Test
    void testSerializeDeserializeRoundTrip() {
        user.setTttGamesPlayed(1);
        user.setTttWins(1);
        user.setC4GamesPlayed(2);
        user.setC4Wins(1);

        String serialized = user.serialize();
        UserData result = UserData.deserialize(serialized);

        assertNotNull(result);
        assertEquals(user.getEmail(), result.getEmail());
        assertEquals(user.getTotalGames(), result.getTotalGames());
        assertEquals(user.getTotalWins(), result.getTotalWins());
    }

    @Test
    void testBestStreakCorrect() {
        user.setTttBestStreak(3);
        user.setC4BestStreak(5);
        assertEquals(5, user.getBestStreak());
    }

    // Incorrect Test cases

    @Test
    void testDeserializeInvalidFormatTooShort() {
        String badInput = "email|user|pass"; // too few fields

        UserData result = UserData.deserialize(badInput);

        assertNull(result);
    }

    @Test
    void testDeserializeInvalidNumberFormat() {
        String badInput = "e|u|p|notANumber|0|0|0|0|0|0|0|0|0|0|0|0|0";

        assertThrows(NumberFormatException.class, () -> {
            UserData.deserialize(badInput);
        });
    }


    // Edge Cases

    @Test
    void testWinRateZeroGames() {
        assertEquals(0, user.getOverallWinRate());
        assertEquals(0, user.getTttWinRate());
        assertEquals(0, user.getC4WinRate());
    }

    @Test
    void testAllZeroStats() {
        assertEquals(0, user.getTotalGames());
        assertEquals(0, user.getTotalWins());
        assertEquals(0, user.getTotalLosses());
        assertEquals(0, user.getTotalDraws());
        assertEquals(0, user.getTotalPoints());
    }

    @Test
    void testNegativeValuesEdgeCase() {
        user.setTttWins(-5); // logically invalid but allowed by class

        assertEquals(-5, user.getTttWins());
    }

    @Test
    void testEmptyStringsSerialization() {
        String serialized = user.serialize();
        UserData result = UserData.deserialize(serialized);

        assertNotNull(result);
        assertEquals("test@email.com", result.getEmail());
        assertEquals("user1", result.getUsername());
        assertEquals("1234", result.getPassword());
    }
}
