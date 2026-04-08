package ca.ucalgary.seng300.model;

/**
 * Data model for a user account, including credentials and per-game stats.
 * Serializable to/from a pipe-delimited text line for file-based persistence.
 *
 * @author Muhammad Ahmad
 * @email muham.ahmad@ucalgary.ca
 */
public class UserData {

    private String email;
    private String username;
    private String password;

    // Tic-Tac-Toe stats
    private int tttGamesPlayed, tttWins, tttLosses, tttDraws;
    private int tttCurrentStreak, tttBestStreak, tttPoints;

    // Connect 4 stats
    private int c4GamesPlayed, c4Wins, c4Losses, c4Draws;
    private int c4CurrentStreak, c4BestStreak, c4Points;

    /**
     * Creates a new user with the given credentials and zeroed-out stats.
     * @author Muhammad Ahmad
     */
    public UserData(String email, String username, String password) {
        this.email = email;
        this.username = username;
        this.password = password;
    }

    // Derived Stats

    /**
     * Returns the total number of games played across both game types.
     * @author Muhammad Ahmad
     */
    public int getTotalGames()  { return tttGamesPlayed + c4GamesPlayed; }

    /**
     * Returns total wins across both games.
     * @author Muhammad Ahmad
     */
    public int getTotalWins()   { return tttWins + c4Wins; }

    /**
     * Returns total losses across both games.
     * @author Muhammad Ahmad
     */
    public int getTotalLosses() { return tttLosses + c4Losses; }

    /**
     * Returns total draws across both games.
     * @author Muhammad Ahmad
     */
    public int getTotalDraws()  { return tttDraws + c4Draws; }

    /**
     * Returns total points across both games.
     * @author Muhammad Ahmad
     */
    public int getTotalPoints() { return tttPoints + c4Points; }

    /**
     * Calculates the overall win rate as a percentage across both games.
     * @author Muhammad Ahmad
     */
    public double getOverallWinRate() {
        int total = getTotalGames();
        return total == 0 ? 0 : (double) getTotalWins() / total * 100;
    }

    /**
     * Calculates the Tic-Tac-Toe win rate as a percentage.
     * @author Muhammad Ahmad
     */
    public double getTttWinRate() {
        return tttGamesPlayed == 0 ? 0 : (double) tttWins / tttGamesPlayed * 100;
    }

    /**
     * Calculates the Connect 4 win rate as a percentage.
     * @author Muhammad Ahmad
     */
    public double getC4WinRate() {
        return c4GamesPlayed == 0 ? 0 : (double) c4Wins / c4GamesPlayed * 100;
    }

    /**
     * Returns the best streak across both games.
     * @author Muhammad Ahmad
     */
    public int getBestStreak() {
        return Math.max(tttBestStreak, c4BestStreak);
    }

    // Serialization

    /**
     * Converts this user's data into a pipe-delimited string for saving to file.
     * @author Muhammad Ahmad
     */
    public String serialize() {
        return String.join("|",
                email, username, password,
                s(tttGamesPlayed), s(tttWins), s(tttLosses), s(tttDraws),
                s(tttCurrentStreak), s(tttBestStreak), s(tttPoints),
                s(c4GamesPlayed), s(c4Wins), s(c4Losses), s(c4Draws),
                s(c4CurrentStreak), s(c4BestStreak), s(c4Points)
        );
    }

    /**
     * Parses a pipe-delimited line back into a UserData object. Returns null if the format is invalid.
     * @author Muhammad Ahmad
     */
    public static UserData deserialize(String line) {
        String[] p = line.split("\\|", -1);
        if (p.length < 17) return null;
        UserData u = new UserData(p[0], p[1], p[2]);
        u.tttGamesPlayed  = Integer.parseInt(p[3]);
        u.tttWins          = Integer.parseInt(p[4]);
        u.tttLosses        = Integer.parseInt(p[5]);
        u.tttDraws         = Integer.parseInt(p[6]);
        u.tttCurrentStreak = Integer.parseInt(p[7]);
        u.tttBestStreak    = Integer.parseInt(p[8]);
        u.tttPoints        = Integer.parseInt(p[9]);
        u.c4GamesPlayed    = Integer.parseInt(p[10]);
        u.c4Wins           = Integer.parseInt(p[11]);
        u.c4Losses         = Integer.parseInt(p[12]);
        u.c4Draws          = Integer.parseInt(p[13]);
        u.c4CurrentStreak  = Integer.parseInt(p[14]);
        u.c4BestStreak     = Integer.parseInt(p[15]);
        u.c4Points         = Integer.parseInt(p[16]);
        return u;
    }

    /**
     * Helper to convert an int to a string for serialization.
     * @author Muhammad Ahmad
     */
    private static String s(int v) { return String.valueOf(v); }

    // Getters and Setters

    public String getEmail()    { return email; }
    public String getUsername()  { return username; }
    public String getPassword() { return password; }

    public void setEmail(String email)       { this.email = email; }
    public void setUsername(String username)  { this.username = username; }
    public void setPassword(String password) { this.password = password; }

    public int getTttGamesPlayed()  { return tttGamesPlayed; }
    public int getTttWins()         { return tttWins; }
    public int getTttLosses()       { return tttLosses; }
    public int getTttDraws()        { return tttDraws; }
    public int getTttCurrentStreak(){ return tttCurrentStreak; }
    public int getTttBestStreak()   { return tttBestStreak; }
    public int getTttPoints()       { return tttPoints; }

    public int getC4GamesPlayed()   { return c4GamesPlayed; }
    public int getC4Wins()          { return c4Wins; }
    public int getC4Losses()        { return c4Losses; }
    public int getC4Draws()         { return c4Draws; }
    public int getC4CurrentStreak() { return c4CurrentStreak; }
    public int getC4BestStreak()    { return c4BestStreak; }
    public int getC4Points()        { return c4Points; }

    public void setTttGamesPlayed(int v)   { tttGamesPlayed = v; }
    public void setTttWins(int v)          { tttWins = v; }
    public void setTttLosses(int v)        { tttLosses = v; }
    public void setTttDraws(int v)         { tttDraws = v; }
    public void setTttCurrentStreak(int v) { tttCurrentStreak = v; }
    public void setTttBestStreak(int v)    { tttBestStreak = v; }
    public void setTttPoints(int v)        { tttPoints = v; }

    public void setC4GamesPlayed(int v)    { c4GamesPlayed = v; }
    public void setC4Wins(int v)           { c4Wins = v; }
    public void setC4Losses(int v)         { c4Losses = v; }
    public void setC4Draws(int v)          { c4Draws = v; }
    public void setC4CurrentStreak(int v)  { c4CurrentStreak = v; }
    public void setC4BestStreak(int v)     { c4BestStreak = v; }
    public void setC4Points(int v)         { c4Points = v; }
}
