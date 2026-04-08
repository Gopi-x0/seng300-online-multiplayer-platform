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
