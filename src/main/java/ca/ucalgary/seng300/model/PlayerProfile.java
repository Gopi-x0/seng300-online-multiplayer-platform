package ca.ucalgary.seng300.model;

// Holds basic profile info for a player (ID and username)
public class PlayerProfile {

    private final String playerId;
    private final String username;

    public PlayerProfile(String playerId, String username) {
        this.playerId = playerId;
        this.username = username;
    }

    public String getPlayerId() {
        return playerId;
    }

    public String getUsername() {
        return username;
    }
}
