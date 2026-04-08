package ca.ucalgary.seng300.network;

import ca.ucalgary.seng300.model.GameType;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Static manager for lobby creation, joining, and lifecycle.
 * Generates unique 8-character hex lobby codes and tracks all active sessions.
 *
 * @author Muhammad Ahmad
 * @email muham.ahmad@ucalgary.ca
 */
public class LobbyManager {

    private static final Map<String, LobbySession> activeLobbies = new HashMap<>();
    private static final Random random = new Random();
    private static final String CHARS = "0123456789ABCDEF";

    /**
     * Creates a new lobby with a unique code and adds it to the active lobbies map.
     * @author Muhammad Ahmad
     */
    public static LobbySession createLobby(GameType gameType, String gameName, String hostUsername) {
        String code = generateCode();
        LobbySession session = new LobbySession(code, gameType, gameName, hostUsername);
        activeLobbies.put(code, session);
        return session;
    }

    /**
     * Removes a lobby from the active lobbies map by its code.
     * @author Muhammad Ahmad
     */
    public static void removeLobby(String code) {
        if (code != null) activeLobbies.remove(code.toUpperCase().trim());
    }

    /**
     * Generates a unique 8-character hex code that doesn't collide with any existing lobby.
     * Tries up to 100 times before throwing an exception.
     * @author Muhammad Ahmad
     */
    private static String generateCode() {
        for (int attempt = 0; attempt < 100; attempt++) {
            StringBuilder sb = new StringBuilder(8);
            for (int i = 0; i < 8; i++) {
                sb.append(CHARS.charAt(random.nextInt(CHARS.length())));
            }
            String code = sb.toString();
            if (!activeLobbies.containsKey(code)) return code;
        }
        throw new IllegalStateException("Could not generate unique lobby code after 100 attempts");
    }
}
