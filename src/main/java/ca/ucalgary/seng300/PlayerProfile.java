package ca.ucalgary.seng300;

/*
class which can be used to reference data from the database relating to players and their stats
 */

public class PlayerProfile {

    public PlayerProfile(String playerId, String username, boolean isGuest) {
        this.playerId = playerId;
        this.username = username;
        this.isGuest = isGuest;
    }

    public String playerId;
    // used to refer to a player in the database

    private String username;
    // display name for the player and their opponents to see

    private boolean isGuest;
    // true if player is a guest, false otherwise

    private String email;
    // email address linked to a player's account

    private String password;
    // a string that only the user associated with a given player should know, used to verify their identity

    private Rank ranking;
    // a player's rank based on their performance in the available games


    public void login() {

    }

    public void logout() {

    }

    public GeneralStats getStats(String gameType) {
        // retrieves the stats for the player
    }

    public void updateUsername(String oldName, String newName) {
        // updates a user's username (should require a password)
    }

    public void updateEmail(String username, String email) {
        // updates a user's email address
    }

    public void updatePassword(String username, String password) {
        // updates a user's password
    }
}
