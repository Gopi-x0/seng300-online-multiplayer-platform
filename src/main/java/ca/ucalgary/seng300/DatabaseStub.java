package ca.ucalgary.seng300;
import java.util.ArrayList;
import java.util.List;

/**
 * Database stub file that simulates an actual database for both match history and leaderboard
 */

public class DatabaseStub {

    /**
     * Saves match data
     * @param gameType Tic-tac-toe or Connect 4
     * @param winner Displays which player won by their profile
     * @param loser Displays which player lost by their profile
     * @param isDraw Checks if a draw has occurred
     */

    public void saveMatchRecord(String gameType, PlayerProfile winner, PlayerProfile loser, boolean isDraw) {

        // Shows message to confirm how a match has ended
        if (isDraw) {

            System.out.println("Result: " + gameType + " match ended in a draw!");

        } else  {
            System.out.println("Result: " + winner.username + " defeated " + loser.username + "!");
        }

    }

    /**
     * Adds and displays players on the leaderboard
     * @param gameType Tic-tac-toe or Connect 4
     * @return Leaderboard data
     */

    public List<PlayerProfile> fetchLeaderboard(String gameType) {

        // Example for the UI team to create visually
        List<PlayerProfile> fakeLeaderboard = new ArrayList<>();
        fakeLeaderboard.add(new PlayerProfile("113131", "Player1", false));
        fakeLeaderboard.add(new PlayerProfile("123131", "Player2", true));

        return fakeLeaderboard;

    }

}
