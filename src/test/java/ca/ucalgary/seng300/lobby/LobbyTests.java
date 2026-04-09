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
    @Test
    // removing someone who isn't in the lobby should return false
    public void removeNonexistentPlayer() {
        assertFalse(session.removePlayer("ghost"));
    }

    @Test
    // host getter should return the right username
    public void hostUsernameCorrect() {
        assertEquals("alice", session.getHostUsername());
    }

    @Test
    // the player list should be read-only so nobody messes with it from outside
    public void playerListIsUnmodifiable() {
        List<String> players = session.getPlayers();
        assertThrows(UnsupportedOperationException.class, () -> players.add("hacker"));
    }
    @Test
    // after removing a player, the lobby shouldn't be full anymore
    public void notFullAfterRemoval() {
        session.addPlayer("bob");
        session.removePlayer("bob");
        assertFalse(session.isFull());
    }

    @Test
    // a new player should be able to join after someone leaves
    public void canRejoinAfterRemoval() {
        session.addPlayer("bob");
        session.removePlayer("bob");
        assertTrue(session.addPlayer("charlie"));
    }
    // LobbyManager Tests

    @Test
    // creating a lobby should give us a non-null session back
    public void createLobbyReturnsSession() {
        LobbySession s = LobbyManager.createLobby(null, "Connect 4", "alice");
        assertNotNull(s);
    }

    @Test
    // the generated lobby code should be 8 characters long
    public void lobbyCodeIsEightChars() {
        LobbySession s = LobbyManager.createLobby(null, "Connect 4", "alice");
        assertEquals(8, s.getLobbyCode().length());
    }

    @Test
    // lobby code should only have hex characters (0-9, A-F)
    public void lobbyCodeIsHex() {
        LobbySession s = LobbyManager.createLobby(null, "Connect 4", "alice");
        assertTrue(s.getLobbyCode().matches("[0-9A-F]{8}"));
    }

    @Test
    // two lobbies created back to back should get different codes
    public void twoLobbiesGetDifferentCodes() {
        LobbySession s1 = LobbyManager.createLobby(null, "Connect 4", "alice");
        LobbySession s2 = LobbyManager.createLobby(null, "Connect 4", "bob");
        assertNotEquals(s1.getLobbyCode(), s2.getLobbyCode());
    }

    @Test
    // the host in the created lobby should match who we passed in
    public void createdLobbyHasCorrectHost() {
        LobbySession s = LobbyManager.createLobby(null, "Connect 4", "alice");
        assertEquals("alice", s.getHostUsername());
    }

    @Test
    // removing a lobby with null shouldn't blow up
    public void removeNullLobbyCode() {
        LobbyManager.removeLobby(null);
        // no exception means we're good
    }

    @Test
    // removing a lobby that doesn't exist should just do nothing
    public void removeNonexistentLobby() {
        LobbyManager.removeLobby("ZZZZZZZZ");
        // no exception means we're good
    }
}