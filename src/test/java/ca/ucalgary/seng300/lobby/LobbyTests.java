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
    @Test
    // make sure the lobby code we passed in is what we get back
    public void lobbyCodeMatches() {
        assertEquals("ABC12345", session.getLobbyCode());
    }

    @Test
    // max players should be 2 since that's hardcoded
    public void maxPlayersIsTwo() {
        assertEquals(2, session.getMaxPlayers());
    }

    @Test
    // lobby with just the host shouldn't be full yet
    public void notFullWithOnePlayer() {
        assertFalse(session.isFull());
    }
    @Test
    // adding a second player should work fine
    public void addSecondPlayer() {
        assertTrue(session.addPlayer("bob"));
        assertEquals(2, session.getPlayerCount());
    }

    @Test
    // lobby should be full once two players are in
    public void fullAfterTwoPlayers() {
        session.addPlayer("bob");
        assertTrue(session.isFull());
    }

    @Test
    // can't add a third player when the lobby is already full
    public void rejectThirdPlayer() {
        session.addPlayer("bob");
        assertFalse(session.addPlayer("charlie"));
    }

    @Test
    // same person shouldn't be able to join twice
    public void rejectDuplicatePlayer() {
        assertFalse(session.addPlayer("alice"));
    }
    @Test
    // nobody should be able to join after the game starts
    public void rejectJoinAfterGameStarted() {
        session.startGame();
        assertFalse(session.addPlayer("bob"));
    }

    @Test
    // removing a normal player should work
    public void removeNonHostPlayer() {
        session.addPlayer("bob");
        assertTrue(session.removePlayer("bob"));
        assertEquals(1, session.getPlayerCount());
    }

    @Test
    // host can't be removed from the lobby
    public void cantRemoveHost() {
        assertFalse(session.removePlayer("alice"));
    }

    @Test
    // removing null shouldn't crash, just return false
    public void removeNullPlayer() {
        assertFalse(session.removePlayer(null));
    }
}