package ca.ucalgary.seng300.stubs;

import ca.ucalgary.seng300.model.UserData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * DatabaseStub - simulates a database back-end for the Student Gaming Platform.
 *
 * Current state:
 * All real persistence currently goes through {@link ca.ucalgary.seng300.service.UserManager},
 * which reads and writes a flat file ({@code sgp_users.dat}) with pipe-delimited rows.
 * That approach is sufficient for a single-machine prototype but would not scale to
 * concurrent multi-client deployments.
 *
 * Purpose of this stub:
 * This class defines the contract a real database adapter would fulfill in a
 * future iteration (e.g. backed by SQLite, PostgreSQL, or a cloud-hosted service).
 * Every public method mirrors an operation the platform already performs through
 * UserManager, but expressed as a database-style API with proper separation of concerns:
 * 
 * User CRUD - create, authenticate, update, delete accounts
 * Stats & amp; Leaderboard - record results, query rankings
 * Match History - persist and retrieve completed game records
 *
 * Methods are intentionally stubbed (return defaults / print to console) so the
 * project compiles and the structure is visible for grading, without coupling any
 * live code path to this class.
 *
 * Migration path (future iteration)
 * 
 * Extract an interface ({@code DatabaseProvider}) from these public methods.
 * Implement a concrete adapter (e.g. {@code SqliteDatabaseProvider}).
 * Refactor {@code UserManager} to delegate to the provider instead of performing file I/O directly.
 */
public class DatabaseStub {

    private final List<UserData> userTable = new ArrayList<>();
    private final List<MatchRecord> matchHistory = new ArrayList<>();

    // User CRUD

    /**
     * Inserts a new user row into the database.
     *
     * @param email    unique email (primary key)
     * @param username display name (unique key)
     * @param password plain-text password (would be hashed in production)
     * @return the created UserData, or null if the email already exists
     * @author Muhammad Ahmad
     */
    public UserData createUser(String email, String username, String password) {
        for (UserData u : userTable) {
            if (u.getEmail().equalsIgnoreCase(email)) {
                System.out.println("[DatabaseStub] createUser REJECTED - duplicate email: " + email);
                return null;
            }
        }
        UserData user = new UserData(email, username, password);
        userTable.add(user);
        System.out.println("[DatabaseStub] createUser OK - " + email);
        return user;
    }

    /**
     * Authenticates a user by email/username and password.
     *
     * @param identifier email or username
     * @param password   plain-text password
     * @return matching UserData, or null on failure
     * @author Muhammad Ahmad
     */
    public UserData authenticateUser(String identifier, String password) {
        for (UserData u : userTable) {
            boolean idMatch = u.getEmail().equalsIgnoreCase(identifier)
                           || u.getUsername().equalsIgnoreCase(identifier);
            if (idMatch && u.getPassword().equals(password)) {
                System.out.println("[DatabaseStub] authenticateUser OK - " + identifier);
                return u;
            }
        }
        System.out.println("[DatabaseStub] authenticateUser FAILED - " + identifier);
        return null;
    }

    /**
     * Retrieves a user by email.
     *
     * @param email the lookup key
     * @return the UserData, or null if not found
     * @author Muhammad Ahmad
     */
    public UserData findUserByEmail(String email) {
        for (UserData u : userTable) {
            if (u.getEmail().equalsIgnoreCase(email)) return u;
        }
        return null;
    }

    /**
     * Updates the username for a given user.
     *
     * @param email identifies the user to update
     * @param newUsername new display name
     * @return true if the update succeeded
     * @author Muhammad Ahmad
     */
    public boolean updateUsername(String email, String newUsername) {
        UserData u = findUserByEmail(email);
        if (u == null) return false;
        u.setUsername(newUsername);
        System.out.println("[DatabaseStub] updateUsername - " + email + " -> " + newUsername);
        return true;
    }

    /**
     * Updates the email (re-key) for a given user.
     *
     * @param oldEmail current email
     * @param newEmail replacement email
     * @return true if the update succeeded
     * @author Muhammad Ahmad
     */
    public boolean updateEmail(String oldEmail, String newEmail) {
        UserData u = findUserByEmail(oldEmail);
        if (u == null) return false;
        u.setEmail(newEmail);
        System.out.println("[DatabaseStub] updateEmail - " + oldEmail + " -> " + newEmail);
        return true;
    }

    /**
     * Updates the password for a given user after verifying the old one.
     *
     * @param email identifies the user
     * @param oldPassword current password (must match)
     * @param newPassword replacement password
     * @return true if the update succeeded
     * @author Muhammad Ahmad
     */
    public boolean updatePassword(String email, String oldPassword, String newPassword) {
        UserData u = findUserByEmail(email);
        if (u == null || !u.getPassword().equals(oldPassword)) return false;
        u.setPassword(newPassword);
        System.out.println("[DatabaseStub] updatePassword - " + email);
        return true;
    }

    /**
     * Deletes a user account entirely.
     *
     * @param email identifies the user
     * @param password must match for confirmation
     * @return true if the account was deleted
     * @author Muhammad Ahmad
     */
    public boolean deleteUser(String email, String password) {
        UserData u = findUserByEmail(email);
        if (u == null || !u.getPassword().equals(password)) return false;
        userTable.remove(u);
        System.out.println("[DatabaseStub] deleteUser - " + email);
        return true;
    }

    // Stats Recording

    /**
     * Records a completed game result and updates the player's running stats.
     * In a real database this would be a transactional UPDATE on the user row
     * plus an INSERT into the match_history table.
     *
     * @param email    the player's email
     * @param gameType "tictactoe" or "connect4"
     * @param isWin    true if the player won
     * @param isDraw   true if the game was a draw (overrides isWin)
     * @author Muhammad Ahmad
     */
    public void recordGameResult(String email, String gameType, boolean isWin, boolean isDraw) {
        UserData u = findUserByEmail(email);
        if (u == null) {
            System.out.println("[DatabaseStub] recordGameResult SKIPPED - unknown user " + email);
            return;
        }

        boolean isTTT = gameType.toLowerCase().contains("tictactoe")
                      || gameType.toLowerCase().contains("tic tac toe");

        if (isTTT) {
            u.setTttGamesPlayed(u.getTttGamesPlayed() + 1);
            if (isDraw) {
                u.setTttDraws(u.getTttDraws() + 1);
                u.setTttCurrentStreak(0);
            } else if (isWin) {
                u.setTttWins(u.getTttWins() + 1);
                int streak = u.getTttCurrentStreak() + 1;
                u.setTttCurrentStreak(streak);
                if (streak > u.getTttBestStreak()) u.setTttBestStreak(streak);
                u.setTttPoints(u.getTttPoints() + 50);
            } else {
                u.setTttLosses(u.getTttLosses() + 1);
                u.setTttCurrentStreak(0);
                u.setTttPoints(Math.max(0, u.getTttPoints() - 50));
            }
        } else {
            u.setC4GamesPlayed(u.getC4GamesPlayed() + 1);
            if (isDraw) {
                u.setC4Draws(u.getC4Draws() + 1);
                u.setC4CurrentStreak(0);
            } else if (isWin) {
                u.setC4Wins(u.getC4Wins() + 1);
                int streak = u.getC4CurrentStreak() + 1;
                u.setC4CurrentStreak(streak);
                if (streak > u.getC4BestStreak()) u.setC4BestStreak(streak);
                u.setC4Points(u.getC4Points() + 50);
            } else {
                u.setC4Losses(u.getC4Losses() + 1);
                u.setC4CurrentStreak(0);
                u.setC4Points(Math.max(0, u.getC4Points() - 50));
            }
        }

        System.out.println("[DatabaseStub] recordGameResult - " + email
                + " | " + gameType + " | win=" + isWin + " draw=" + isDraw);
    }

    // Leaderboard Queries

    /**
     * Returns all users ranked by total points descending.
     * Equivalent to {@code SELECT * FROM users ORDER BY total_points DESC}.
     *
     * @return unmodifiable ranked list
     * @author Muhammad Ahmad
     */
    public List<UserData> getGlobalLeaderboard() {
        List<UserData> sorted = new ArrayList<>(userTable);
        sorted.sort(Comparator.comparingInt(UserData::getTotalPoints).reversed());
        return Collections.unmodifiableList(sorted);
    }

    /**
     * Returns users ranked by Tic-Tac-Toe points.
     *
     * @return unmodifiable ranked list
     * @author Muhammad Ahmad
     */
    public List<UserData> getTttLeaderboard() {
        List<UserData> sorted = new ArrayList<>(userTable);
        sorted.sort(Comparator.comparingInt(UserData::getTttPoints).reversed());
        return Collections.unmodifiableList(sorted);
    }

    /**
     * Returns users ranked by Connect 4 points.
     *
     * @return unmodifiable ranked list
     * @author Muhammad Ahmad
     */
    public List<UserData> getC4Leaderboard() {
        List<UserData> sorted = new ArrayList<>(userTable);
        sorted.sort(Comparator.comparingInt(UserData::getC4Points).reversed());
        return Collections.unmodifiableList(sorted);
    }

    /**
     * Computes a player's global rank (1-based).
     *
     * @param email identifies the player
     * @return rank (1 = top), or -1 if user not found
     * @author Muhammad Ahmad
     */
    public int getUserRank(String email) {
        List<UserData> board = getGlobalLeaderboard();
        for (int i = 0; i < board.size(); i++) {
            if (board.get(i).getEmail().equalsIgnoreCase(email)) return i + 1;
        }
        return -1;
    }

    // Match History

    /**
     * Logs a completed match for future retrieval.
     * In a real database this would be an INSERT into a match_history table.
     *
     * @param gameType     "tictactoe" or "connect4"
     * @param player1Email email of player 1
     * @param player2Email email of player 2
     * @param result       "P1_WINS", "P2_WINS", or "DRAW"
     * @author Muhammad Ahmad
     */
    public void saveMatchRecord(String gameType, String player1Email, String player2Email, String result) {
        MatchRecord record = new MatchRecord(gameType, player1Email, player2Email, result,
                System.currentTimeMillis());
        matchHistory.add(record);
        System.out.println("[DatabaseStub] saveMatchRecord - " + gameType
                + " | " + player1Email + " vs " + player2Email + " | " + result);
    }

    /**
     * Retrieves the last N match records for a given player.
     *
     * @param email player email
     * @param limit max records to return
     * @return list of matches (most recent first)
     * @author Muhammad Ahmad
     */
    public List<MatchRecord> getMatchHistory(String email, int limit) {
        List<MatchRecord> results = new ArrayList<>();
        for (int i = matchHistory.size() - 1; i >= 0 && results.size() < limit; i--) {
            MatchRecord r = matchHistory.get(i);
            if (r.player1Email.equalsIgnoreCase(email) || r.player2Email.equalsIgnoreCase(email)) {
                results.add(r);
            }
        }
        return results;
    }

    /**
     * Returns the total number of registered users.
     * Equivalent to {@code SELECT COUNT(*) FROM users}.
     * @author Muhammad Ahmad
     */
    public int getUserCount() {
        return userTable.size();
    }

    // Match Record DTO

    /**
     * Lightweight data object representing a single completed match.
     * In a production system this would map to a row in the match_history table.
     */
    public static class MatchRecord {
        public final String gameType;
        public final String player1Email;
        public final String player2Email;
        public final String result;
        public final long timestampMillis;

        // Constructor for a match record with all required fields
        // @author Muhammad Ahmad
        public MatchRecord(String gameType, String player1Email, String player2Email,
                           String result, long timestampMillis) {
            this.gameType = gameType;
            this.player1Email = player1Email;
            this.player2Email = player2Email;
            this.result = result;
            this.timestampMillis = timestampMillis;
        }

        // Returns a readable string representation of this match record
        // @author Muhammad Ahmad
        @Override
        public String toString() {
            return gameType + " | " + player1Email + " vs " + player2Email
                    + " | " + result + " @ " + timestampMillis;
        }
    }
}
