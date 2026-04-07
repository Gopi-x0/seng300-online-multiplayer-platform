package ca.ucalgary.seng300;

import ca.ucalgary.seng300.Stubs.DatabaseStub;

/**
 * This class coordinates validation and updates the underlying persistence
 * stub when a user changes their credentials.
 */
public class Settings {
    private final Player player;
    private final DatabaseStub database;

    public Settings(Player player, DatabaseStub database) {
        this.player = player;
        this.database = database;
    }

    public boolean deleteAccount(String password) {
        return false;
    }

    public boolean changeUsername(String newUsername) {
        return false;
    }

    public boolean changeEmail(String newEmail) {
        return false;
    }

    public boolean changePassword(String oldPassword, String newPassword) {
        return false;
    }

    public boolean logout() {
        return false;
    }

}

