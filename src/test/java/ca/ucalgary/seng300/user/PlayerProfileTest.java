package ca.ucalgary.seng300.user;
import ca.ucalgary.seng300.model.PlayerProfile;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PlayerProfileTest {

    // Simple correctness tests

    @Test
    void testConstructorAndGetters() {
        PlayerProfile profile = new PlayerProfile("1234", "user1");

        assertEquals("1234", profile.getPlayerId());
        assertEquals("user1", profile.getUsername());
    }

    @Test
    void testGetPlayerId() {
        PlayerProfile profile = new PlayerProfile("1234", "user");

        assertEquals("1234", profile.getPlayerId());
    }

    @Test
    void testGetUsername() {
        PlayerProfile profile = new PlayerProfile("1234", "bob");

        assertEquals("bob", profile.getUsername());
    }

    // Basic Edge Cases

    @Test
    void testEmptyStrings() {
        PlayerProfile profile = new PlayerProfile("", "");

        assertEquals("", profile.getPlayerId());
        assertEquals("", profile.getUsername());
    }

    @Test
    void testNullValues() {
        PlayerProfile profile = new PlayerProfile(null, null);

        assertNull(profile.getPlayerId());
        assertNull(profile.getUsername());
    }
}
