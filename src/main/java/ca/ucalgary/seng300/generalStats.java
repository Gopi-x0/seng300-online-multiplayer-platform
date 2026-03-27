package ca.ucalgary.seng300;

public class generalStats {
    /*
    class used to reference data for a given game type
    contains simple counting stats like wins, losses
    should retrieve stats from the database based on playerId and gameType
    to be called by playerProfile class
     */

    public generalStats(String playerId, String gameType) {
        this.gameType = gameType;
        // TODO: retrieve data from database to initialize
        // the following are placeholders until there is a way to call data from the database:
        this.games_played = getGamesPlayed();
        this.wins = getWins();
        this.losses = getLosses();
        this.draws = getDraws();
        this. win_differential = this.wins - this.losses;
    }

    public String gameType;

    private int games_played;
    private int wins;
    private int losses;
    private int draws;
    private int win_differential;

    // TODO: adding new games to record
}
