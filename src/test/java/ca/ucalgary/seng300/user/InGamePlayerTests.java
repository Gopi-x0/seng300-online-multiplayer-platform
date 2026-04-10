package ca.ucalgary.seng300.user;
import ca.ucalgary.seng300.connect4engine.Player;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class InGamePlayerTests {

    // Correct test cases

    @Test
    void testConstructorAndGetters() {
        Player player = new Player("alice", 'R');

        assertEquals("alice", player.getId());
        assertEquals('R', player.getSymbol());
    }

    @Test
    void testToStringCorrectFormat() {
        Player player = new Player("bob", 'Y');

        String expected = "Player{id='bob', symbol=Y}";
        assertEquals(expected, player.toString());
    }

    // Edge Cases

    @Test
    void testEmptyId() {
        Player player = new Player("", 'R');

        assertEquals("", player.getId());
        assertEquals('R', player.getSymbol());
    }

    @Test
    void testNullId() {
        Player player = new Player(null, 'Y');

        assertNull(player.getId());
        assertEquals('Y', player.getSymbol());
    }

    @Test
    void testLowercaseSymbol() {
        Player player = new Player("charlie", 'r');

        assertEquals('r', player.getSymbol()); // class does not enforce uppercase
    }

    @Test
    void testInvalidSymbolCharacter() {
        Player player = new Player("dave", 'X');

        assertEquals('X', player.getSymbol()); // no validation in class
    }

    @Test
    void testSpecialCharactersInId() {
        Player player = new Player("user_123!@", 'R');

        assertEquals("user_123!@", player.getId());
    }

    // Incorrect Cases

    @Test
    void testToStringWithNullId() {
        Player player = new Player(null, 'R');

        String result = player.toString();

        assertTrue(result.contains("id='null'"));
    }

    @Test
    void testImmutability() {
        Player player = new Player("alice", 'R');

        // No setters exist, so values should remain constant
        assertEquals("alice", player.getId());
        assertEquals('R', player.getSymbol());
    }
}
