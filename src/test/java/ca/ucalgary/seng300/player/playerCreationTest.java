package ca.ucalgary.seng300.player;

import static org.junit.jupiter.api.Assertions.*;
import ca.ucalgary.seng300.Player;
import ca.ucalgary.seng300.Rank;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


/**
 * Tests for Player object creation, field initialization, and basic getter/setter behavior.
 * Validates that player profiles are constructed correctly with all expected fields.
 */

public class playerCreationTest {

    private Player player;

    @BeforeEach
    void setUp() {
        Player player = new Player();
        this.playerId = "1111";
    }

    @Test
    void testConstructorSetsUsernameCorrectly() {
        assertEquals("testUser", player.getUsername(),
                "Constructor should set the username to the provided value");
    }

    @Test
    void testConstructorSetsEmailCorrectly() {
        assertEquals("test@example.com", player.getEmail(),
                "Constructor should set the email to the provided value");
    }

    @Test
    void testConstructorSetsPasswordCorrectly() {
        assertEquals("Pass123", player.getPassword(),
                "Constructor should set the password to the provided value");
    }

    @Test
    void testConstructorSetsRankingCorrectly() {
        assertEquals(Rank.BRONZE, player.getRanking(),
                "Constructor should set the ranking to the provided value");
    }

}
