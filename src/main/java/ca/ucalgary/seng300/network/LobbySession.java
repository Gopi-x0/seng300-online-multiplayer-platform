package ca.ucalgary.seng300.network;

import ca.ucalgary.seng300.model.GameType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents a single lobby session for multiplayer game matchmaking.
 * Tracks the lobby code, game type, connected players, and game state.
 *
 * @author Muhammad Ahmad
 * @email muham.ahmad@ucalgary.ca
 */
public class LobbySession {

    private final String lobbyCode;
    private final GameType gameType;
    private final String gameName;
    private final int maxPlayers;
    private final List<String> players;
    private final String hostUsername;
    private boolean gameStarted;

    /**
     * Creates a new lobby session with the host as the first player.
     * @author Muhammad Ahmad
     */
    public LobbySession(String lobbyCode, GameType gameType, String gameName, String hostUsername) {
        this.lobbyCode = lobbyCode;
        this.gameType = gameType;
        this.gameName = gameName;
        this.hostUsername = hostUsername;
        this.maxPlayers = 2;
        this.players = new ArrayList<>();
        this.players.add(hostUsername);
        this.gameStarted = false;
    }

    // Getters

    public String getLobbyCode() { return lobbyCode; }
    public int getMaxPlayers() { return maxPlayers; }
    public List<String> getPlayers() { return Collections.unmodifiableList(players); }
    public String getHostUsername() { return hostUsername; }
    public int getPlayerCount() { return players.size(); }
    public boolean isFull() { return players.size() >= maxPlayers; }

    // Player Management

    /**
     * Adds a player to the lobby if it's not full and the game hasn't started.
     * Returns false if the player is already in the lobby.
     * @author Muhammad Ahmad
     */
    public boolean addPlayer(String username) {
        if (isFull() || gameStarted) return false;
        if (players.contains(username)) return false;
        players.add(username);
        return true;
    }

    /**
     * Removes a non-host player from the lobby by username.
     * @author Muhammad Ahmad
     */
    public boolean removePlayer(String username) {
        if (username == null || username.equals(hostUsername)) return false;
        return players.remove(username);
    }

    /**
     * Marks the game as started so no more players can join.
     * @author Muhammad Ahmad
     */
    public void startGame() { this.gameStarted = true; }
}
