package ca.ucalgary.seng300.Service;

import ca.ucalgary.seng300.PlayerProfile;
import ca.ucalgary.seng300.Stubs.DatabaseStub;

import java.util.List;

/**
 * Tracks player stats and displays them in a leaderboard 
 */

public class LeaderboardManager {
    private DatabaseStub database;

    // Using the database stub:
    public LeaderboardManager(DatabaseStub database) {
        this.database = database;
    }

    // When game ends it saves match results
    public void recordMatchResult(String gameType, PlayerProfile winner, PlayerProfile loser, boolean isDraw) {
        if (isDraw) {
            System.out.println(gameType + " match ended in a DRAW.");
        } else {
            System.out.println(winner.username + " defeated " + loser.username + " in " + gameType + "!");
        }

        database.saveMatchRecord(gameType, winner, loser, isDraw);
    }

    // Used by the UI team when they load the leaderboard to get the top players and their stats
    public List<PlayerProfile> getTopPlayers(String gameType) {
        System.out.println("Requesting top players for " + gameType);

        // Database stub returns the leaderboard
        return database.fetchLeaderboard(gameType);

    }
}
