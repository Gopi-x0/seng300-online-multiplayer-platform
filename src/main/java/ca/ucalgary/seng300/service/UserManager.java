package ca.ucalgary.seng300.service;

import ca.ucalgary.seng300.model.UserData;

import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Manages user accounts with file-based persistence.
 * Handles registration, login, profile updates, and game stat recording.
 * Data is stored in sgp_users.dat (pipe-delimited, one user per line).
 * 
 * @author Muhammad Ahmad
 * @email muham.ahmad@ucalgary.ca
 */
public class UserManager {

    private static final String DATA_FILE = "sgp_users.dat";
    private static final List<UserData> users = new ArrayList<>();
    private static UserData currentUser = null;

    // Load users from file on class init, and make sure admin + dummy users exist
    static {
        loadUsers();
        ensureAdminExists();
        ensureDummyUsersExist();
    }

    // File I/O

    // Reads all user records from sgp_users.dat into the in-memory list.
    // Also re-links currentUser if they were logged in before the reload.
    // @author Muhammad Ahmad
    public static void loadUsers() {
        String currentEmail = (currentUser != null) ? currentUser.getEmail() : null;
        File file = new File(DATA_FILE);
        if (!file.exists()) {
            users.clear();
            return;
        }
        List<UserData> loaded = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    UserData u = UserData.deserialize(line);
                    if (u != null) loaded.add(u);
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading users: " + e.getMessage());
            return;
        }
        users.clear();
        users.addAll(loaded);
        if (currentEmail != null) {
            for (UserData u : users) {
                if (u.getEmail().equalsIgnoreCase(currentEmail)) {
                    currentUser = u;
                    return;
                }
            }
        }
    }

    // Writes all in-memory users to sgp_users.dat with a file lock
    // @author Muhammad Ahmad
    public static void saveUsers() {
        File file = new File(DATA_FILE);
        try (RandomAccessFile raf = new RandomAccessFile(file, "rw");
             FileChannel channel = raf.getChannel();
             FileLock lock = channel.lock()) {

            raf.seek(0);
            raf.setLength(0);
            StringBuilder sb = new StringBuilder();
            for (UserData user : users) {
                sb.append(user.serialize()).append(System.lineSeparator());
            }
            raf.write(sb.toString().getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            System.err.println("Error saving users: " + e.getMessage());
        }
    }

    /**
     * Atomic read-modify-write with exclusive file lock.
     * Reads the latest file contents, replaces only the current user's
     * entry with in-memory data, writes everything back - all under a
     * single lock so two concurrent instances cannot clobber each other.
     * @author Muhammad Ahmad
     */
    private static void mergeSaveCurrentUser() {
        if (currentUser == null) return;
        String currentEmail = currentUser.getEmail();

        File file = new File(DATA_FILE);
        try (RandomAccessFile raf = new RandomAccessFile(file, "rw");
             FileChannel channel = raf.getChannel();
             FileLock lock = channel.lock()) {

            List<UserData> freshUsers = new ArrayList<>();
            long fileLen = raf.length();
            if (fileLen > 0) {
                byte[] data = new byte[(int) fileLen];
                raf.readFully(data);
                String content = new String(data, StandardCharsets.UTF_8);
                for (String line : content.split("\\R")) {
                    if (!line.trim().isEmpty()) {
                        UserData u = UserData.deserialize(line.trim());
                        if (u != null) freshUsers.add(u);
                    }
                }
            }

            boolean found = false;
            for (int i = 0; i < freshUsers.size(); i++) {
                if (freshUsers.get(i).getEmail().equalsIgnoreCase(currentEmail)) {
                    freshUsers.set(i, currentUser);
                    found = true;
                    break;
                }
            }
            if (!found) freshUsers.add(currentUser);

            raf.seek(0);
            raf.setLength(0);
            StringBuilder sb = new StringBuilder();
            for (UserData u : freshUsers) {
                sb.append(u.serialize()).append(System.lineSeparator());
            }
            raf.write(sb.toString().getBytes(StandardCharsets.UTF_8));

            users.clear();
            users.addAll(freshUsers);
        } catch (IOException e) {
            System.err.println("Error merge-saving user: " + e.getMessage());
        }
    }

    // Default Data Setup

    // Makes sure the admin account exists on first run
    // @author Muhammad Ahmad
    private static void ensureAdminExists() {
        for (UserData user : users) {
            if (user.getEmail().equalsIgnoreCase("admin@sgp.com")) return;
        }
        UserData admin = new UserData("admin@sgp.com", "admin", "admin");
        admin.setTttGamesPlayed(15);
        admin.setTttWins(10);
        admin.setTttLosses(3);
        admin.setTttDraws(2);
        admin.setTttCurrentStreak(3);
        admin.setTttBestStreak(5);
        admin.setTttPoints(500);
        admin.setC4GamesPlayed(12);
        admin.setC4Wins(7);
        admin.setC4Losses(4);
        admin.setC4Draws(1);
        admin.setC4CurrentStreak(2);
        admin.setC4BestStreak(4);
        admin.setC4Points(350);
        users.add(admin);
        saveUsers();
    }

    // Creates dummy/test users with preset stats if they don't already exist
    // @author Muhammad Ahmad
    private static void ensureDummyUsersExist() {
        boolean added = false;
        added |= addDummyUser("muhammad.ahmad@sgp.com","MuhammadA","123", 20,14,3,3,4,6,1200, 18,12,4,2,3,5,500);
        added |= addDummyUser("olivia.park@sgp.com","OliviaP","123", 12,8,2,2,2,3,400, 10,6,3,1,1,3,350);
        added |= addDummyUser("bilal.ahmed@sgp.com","BilalA","123", 12,7,3,2,1,3,350, 10,6,3,1,2,3,300);
        added |= addDummyUser("muhammad.waqar@sgp.com","MuhammadW","123", 10,6,3,1,2,3,300, 10,6,3,1,1,2,300);
        added |= addDummyUser("logan.fedoroshyn@sgp.com","LoganF","123", 14,9,3,2,2,4,450, 8,5,2,1,1,2,250);
        added |= addDummyUser("shayan.shaikh@sgp.com","ShayanS","123", 14,9,3,2,2,4,450, 12,7,3,2,2,3,350);
        added |= addDummyUser("mohammed.syed@sgp.com","MohammedS","123", 10,6,3,1,2,3,300, 8,5,2,1,1,2,250);
        added |= addDummyUser("esha.esha@sgp.com","EshaE","123", 10,6,3,1,1,3,300, 7,4,2,1,2,2,200);
        added |= addDummyUser("landon.liknes@sgp.com","LandonL","123", 10,7,2,1,3,4,350, 6,3,2,1,1,2,150);
        added |= addDummyUser("jack@sgp.com","Jack","123", 8,5,2,1,2,2,250, 8,5,2,1,1,3,250);
        added |= addDummyUser("gurparvesh.golan@sgp.com","GurparveshG","123", 8,5,2,1,2,3,250, 10,7,2,1,3,4,350);
        added |= addDummyUser("rishav.gautam@sgp.com","RishavG","123", 10,6,3,1,1,3,300, 8,6,1,1,2,3,300);
        added |= addDummyUser("prantap.sharma@sgp.com","PrantapS","123", 8,5,2,1,1,2,250, 6,4,1,1,2,2,200);
        added |= addDummyUser("dima@sgp.com","Dima","123", 8,5,2,1,2,3,250, 6,4,1,1,1,2,200);
        added |= addDummyUser("jimmy.la@sgp.com","JimmyL","123", 7,4,2,1,1,2,200, 8,5,2,1,1,3,250);
        added |= addDummyUser("zonaira.omar@sgp.com","ZonairaO","123", 10,6,3,1,2,3,300, 6,3,2,1,1,2,150);
        added |= addDummyUser("haris.naveed@sgp.com","HarisN","123", 6,3,2,1,1,2,150, 8,5,2,1,2,3,250);
        if (added) saveUsers();
    }

    // Helper to add a single dummy user with all their TTT and C4 stats
    // @author Muhammad Ahmad
    private static boolean addDummyUser(String email, String username, String password,
            int tttGP, int tttW, int tttL, int tttD, int tttCS, int tttBS, int tttPts,
            int c4GP, int c4W, int c4L, int c4D, int c4CS, int c4BS, int c4Pts) {
        for (UserData u : users) {
            if (u.getEmail().equalsIgnoreCase(email)) return false;
        }
        UserData u = new UserData(email, username, password);
        u.setTttGamesPlayed(tttGP); u.setTttWins(tttW); u.setTttLosses(tttL); u.setTttDraws(tttD);
        u.setTttCurrentStreak(tttCS); u.setTttBestStreak(tttBS); u.setTttPoints(tttPts);
        u.setC4GamesPlayed(c4GP); u.setC4Wins(c4W); u.setC4Losses(c4L); u.setC4Draws(c4D);
        u.setC4CurrentStreak(c4CS); u.setC4BestStreak(c4BS); u.setC4Points(c4Pts);
        users.add(u);
        return true;
    }

    // Registration and Login

    /**
     * Registers a new user. Email is the primary key.
     * Default username is the part of the email before '@'.
     * @return the new UserData on success, null if email already taken
     * @author Muhammad Ahmad
     */
    public static UserData register(String email, String password) {
        if (email == null || !email.contains("@") || email.contains("|")) return null;
        if (password == null || password.length() < 3 || password.contains("|")) return null;
        for (UserData user : users) {
            if (user.getEmail().equalsIgnoreCase(email)) return null;
        }
        String baseUsername = email.substring(0, email.indexOf('@'));
        if (baseUsername.isEmpty()) return null;

        String username = baseUsername;
        int suffix = 1;
        while (isUsernameTaken(username)) {
            username = baseUsername + suffix;
            suffix++;
        }

        UserData newUser = new UserData(email, username, password);
        users.add(newUser);
        saveUsers();
        currentUser = newUser;
        return newUser;
    }

    // Checks if a username is already in use by another user
    // @author Muhammad Ahmad
    private static boolean isUsernameTaken(String username) {
        for (UserData user : users) {
            if (user.getUsername().equalsIgnoreCase(username)) return true;
        }
        return false;
    }

    /**
     * Logs in with email OR username + password.
     * @return the UserData on success, null on failure
     * @author Muhammad Ahmad
     */
    public static UserData login(String emailOrUsername, String password) {
        for (UserData user : users) {
            if ((user.getEmail().equalsIgnoreCase(emailOrUsername) ||
                 user.getUsername().equalsIgnoreCase(emailOrUsername)) &&
                 user.getPassword().equals(password)) {
                currentUser = user;
                return user;
            }
        }
        return null;
    }

    // @author Muhammad Ahmad
    public static UserData getCurrentUser() { return currentUser; }
    // @author Muhammad Ahmad
    public static List<UserData> getAllUsers() { return Collections.unmodifiableList(users); }

    // Profile Updates

    // Changes the current user's username if it's not already taken
    // @author Muhammad Ahmad
    public static boolean changeUsername(String newUsername) {
        if (currentUser == null || newUsername == null || newUsername.trim().isEmpty()) return false;
        if (newUsername.contains("|")) return false;
        for (UserData user : users) {
            if (user != currentUser && user.getUsername().equalsIgnoreCase(newUsername)) return false;
        }
        currentUser.setUsername(newUsername.trim());
        saveUsers();
        return true;
    }

    // Changes the current user's email if it's not already taken
    // @author Muhammad Ahmad
    public static boolean changeEmail(String newEmail) {
        if (currentUser == null || newEmail == null || !newEmail.contains("@")) return false;
        if (newEmail.contains("|")) return false;
        for (UserData user : users) {
            if (user != currentUser && user.getEmail().equalsIgnoreCase(newEmail)) return false;
        }
        currentUser.setEmail(newEmail.trim());
        saveUsers();
        return true;
    }

    // Changes the current user's password after verifying the old one
    // @author Muhammad Ahmad
    public static boolean changePassword(String oldPassword, String newPassword) {
        if (currentUser == null) return false;
        if (!currentUser.getPassword().equals(oldPassword)) return false;
        if (newPassword == null || newPassword.trim().isEmpty()) return false;
        if (newPassword.contains("|")) return false;
        if (newPassword.length() < 3) return false;
        currentUser.setPassword(newPassword);
        saveUsers();
        return true;
    }

    // Game Stats

    /**
     * Records a game result for the currently logged-in user.
     * Updates wins/losses/draws, streak, and points accordingly.
     * @param gameType "Tic Tac Toe" or "Connect 4"
     * @param isWin   true if the user won
     * @param isDraw  true if the game was a draw (overrides isWin)
     * @author Muhammad Ahmad
     */
    public static void recordGameResult(String gameType, boolean isWin, boolean isDraw) {
        if (currentUser == null) return;

        boolean isTTT = gameType.toLowerCase().contains("tictactoe") ||
                         gameType.toLowerCase().contains("tic tac toe");

        if (isTTT) {
            currentUser.setTttGamesPlayed(currentUser.getTttGamesPlayed() + 1);
            if (isDraw) {
                currentUser.setTttDraws(currentUser.getTttDraws() + 1);
                currentUser.setTttCurrentStreak(0);
            } else if (isWin) {
                currentUser.setTttWins(currentUser.getTttWins() + 1);
                int streak = currentUser.getTttCurrentStreak() + 1;
                currentUser.setTttCurrentStreak(streak);
                if (streak > currentUser.getTttBestStreak()) currentUser.setTttBestStreak(streak);
                currentUser.setTttPoints(currentUser.getTttPoints() + 50);
            } else {
                currentUser.setTttLosses(currentUser.getTttLosses() + 1);
                currentUser.setTttCurrentStreak(0);
                currentUser.setTttPoints(Math.max(0, currentUser.getTttPoints() - 50));
            }
        } else {
            currentUser.setC4GamesPlayed(currentUser.getC4GamesPlayed() + 1);
            if (isDraw) {
                currentUser.setC4Draws(currentUser.getC4Draws() + 1);
                currentUser.setC4CurrentStreak(0);
            } else if (isWin) {
                currentUser.setC4Wins(currentUser.getC4Wins() + 1);
                int streak = currentUser.getC4CurrentStreak() + 1;
                currentUser.setC4CurrentStreak(streak);
                if (streak > currentUser.getC4BestStreak()) currentUser.setC4BestStreak(streak);
                currentUser.setC4Points(currentUser.getC4Points() + 50);
            } else {
                currentUser.setC4Losses(currentUser.getC4Losses() + 1);
                currentUser.setC4CurrentStreak(0);
                currentUser.setC4Points(Math.max(0, currentUser.getC4Points() - 50));
            }
        }
        mergeSaveCurrentUser();
    }

    // Logs out the current user by clearing the reference
    // @author Muhammad Ahmad
    public static void logout() {
        currentUser = null;
    }

    // Ranking

    // Returns the 1-based global rank of the given user sorted by total points
    // @author Muhammad Ahmad
    public static int getUserRank(UserData user) {
        List<UserData> sorted = new ArrayList<>(users);
        sorted.sort((a, b) -> Integer.compare(b.getTotalPoints(), a.getTotalPoints()));
        for (int i = 0; i < sorted.size(); i++) {
            if (sorted.get(i) == user) return i + 1;
        }
        return users.size();
    }
}
