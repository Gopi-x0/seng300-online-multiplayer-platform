package ca.ucalgary.seng300;

public class generalStats {

    public generalStats(String playerId, String gameType) {
        this.gameType = gameType;
    }

    public String gameType;

    private int games_played;
    private int wins;
    private int losses;
    private int draws;
    private int win_differential;

    // TODO:
    // adding new games to record
    // retrieving data from database
}
