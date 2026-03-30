package ca.ucalgary.seng300.Service;

import ca.ucalgary.seng300.PlayerProfile;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Generates a temporary list of whoever is on the computer currently and playing
 */

public class AuthManager {
    // Active player list
    private List<PlayerProfile> activePlayers = new ArrayList<>();
    private Random random = new Random(); // Sets up the generator we'll use to generate playerIDs

    // Logs in player to session
    public PlayerProfile login(String username) {
        // Generate playerIDs with a limit of 5 digits from 00000 to 99999
        String generatedId = String.format("%05d", random.nextInt(100000));

        // Create the profile
        PlayerProfile newPlayer = new PlayerProfile(generatedId, username);

        activePlayers.add(newPlayer);
        System.out.println(username + " (" + generatedId + ")" + " has joined the session!");

        return newPlayer;
    }

    // Shows list of active players in session
    public List<PlayerProfile> getActivePlayers() {
        return activePlayers;
    }
}