package ca.ucalgary.seng300.lobby;

import ca.ucalgary.seng300.network.LobbySession;
import ca.ucalgary.seng300.network.LobbyManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

public class LobbyTests {

    private LobbySession session;

    @BeforeEach
    public void setUp() {
        // fresh lobby before each test so nothing leaks between them
        session = new LobbySession("ABC12345", null, "Connect 4", "alice");
    }

    // LobbySession Tests

    @Test
    // host should be the first player in the lobby right away
    public void hostIsFirstPlayer() {
        assertEquals("alice", session.getPlayers().get(0));
    }

    @Test
    // lobby should start with exactly one player (the host)
    public void startsWithOnePlayer() {
        assertEquals(1, session.getPlayerCount());
    }
}