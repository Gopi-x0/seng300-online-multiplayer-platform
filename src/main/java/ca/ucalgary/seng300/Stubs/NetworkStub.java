package ca.ucalgary.seng300.Stubs;

import ca.ucalgary.seng300.GameState;

/**
 * Simulates connecting to a matchmaking server or cloud host
 */

public class NetworkStub {

    // Pretends to connect to server
    public boolean connectToServer() {
        System.out.println("Attempting to connect to matchmaking server...");
        System.out.println("Connection established. Latency: 24ms.");
        System.out.println("----------------------\n");

        return true;
    }

    // Broadcasts message of game state change and displays sessionID
    public void broadcastStateUpdate(GameState state) {
        System.out.println("Broadcasting new board state to current game....");
    }

    // Drops the connection when the app closes
    public void disconnect() {
        System.out.println("Disconnecting from server...");
    }
}
