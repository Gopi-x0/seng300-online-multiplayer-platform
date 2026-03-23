package ca.ucalgary.seng300.player;

import static org.junit.jupiter.api.Assertions.*;

import ca.ucalgary.seng300.Player;
import ca.ucalgary.seng300.Rank;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PlayerUpdateVariablesTest {

    private Player player;

    @BeforeEach
    void setUp() {
        player = new Player();
        player.playerId = "1111";
        player.setUsername("testUser");
        player.setEmail("test@example.com");
        player.setRanking(Rank.BRONZE);
    }

    @Test
    void testUpdateUsernameWithCorrectOldNameChangesUsername() {
        player.updateUsername("originalUser", "newUser");
        assertEquals("newUser", player.getUsername(),
                "updateUsername should change the username when the old name matches");
    }

    @Test
    void testUpdateUsernameWithIncorrectOldNameDoesNotChange() {
        player.updateUsername("wrongUserName", "newUsername");  //One username is wrong, username change should be rejected
        assertEquals("originalUser", player.getUsername());
    }

    /*  NOT SURE IF IT WILL ALLOW SWITCHING USER NAME TO THE SAME THING
    @Test
    void testUpdateUsernameToSameThrowError() {
        assertDoesNotThrow(() -> player.updateUsername("originalUser", "originalUser"),
                "updateUsername should handle updating to the same value without error");
    }
    */

    @Test
    void testUpdateUsernameMultipleTimesReflectsLatestValue() {
        player.updateUsername("originalUser", "secondName");
        player.updateUsername("secondName", "thirdName");
        assertEquals("thirdName", player.getUsername());
    }

}
