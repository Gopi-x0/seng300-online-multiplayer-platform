package ca.ucalgary.seng300.network;

import javafx.application.Platform;

import java.io.*;
import java.net.Socket;
import java.util.function.Consumer;

/**
 * TCP client wrapper for the guest side of a multiplayer game.
 * Connects to a GameServer on localhost, then provides a simple
 * line-based send/receive API.
 *
 * Usage:
 *   GameClient client = new GameClient();
 *   client.setMessageHandler(msg -> { ... });
 *   client.connect("localhost", 5300);
 *   client.send("JOIN:CODE:username");
 *   // later:
 *   client.shutdown();
 *
 * @author Muhammad Ahmad
 * @email muham.ahmad@ucalgary.ca
 */
public class GameClient {

    private static GameClient activeInstance;

    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private Consumer<String> messageHandler;
    private boolean running;

    // Singleton Access

    /**
     * Returns the currently active GameClient instance, or null if none.
     * @author Muhammad Ahmad
     */
    public static GameClient getActive() {
        return activeInstance;
    }

    /**
     * Shuts down the active client instance if one exists.
     * @author Muhammad Ahmad
     */
    public static void shutdownActive() {
        if (activeInstance != null) {
            activeInstance.shutdown();
            activeInstance = null;
        }
    }

    // Connection

    /**
     * Opens a TCP connection to the given host and port, then starts listening for messages.
     * @author Muhammad Ahmad
     */
    public void connect(String host, int port) throws IOException {
        socket = new Socket(host, port);
        out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        running = true;
        activeInstance = this;
        startListening();
    }

    /**
     * Sends a message to the server if the connection is still active.
     * @author Muhammad Ahmad
     */
    public void send(String message) {
        if (out != null && running) {
            out.println(message);
        }
    }

    /**
     * Sets the callback that gets invoked when a message is received from the server.
     * @author Muhammad Ahmad
     */
    public void setMessageHandler(Consumer<String> handler) {
        this.messageHandler = handler;
    }

    /**
     * Closes the socket and streams, and clears the active instance.
     * @author Muhammad Ahmad
     */
    public void shutdown() {
        running = false;
        try { if (out != null) out.close(); } catch (Exception ignored) {}
        try { if (in != null) in.close(); } catch (Exception ignored) {}
        try { if (socket != null) socket.close(); } catch (Exception ignored) {}
        if (activeInstance == this) activeInstance = null;
    }

    // Message Listening

    /**
     * Spawns a daemon thread that reads lines from the server and dispatches them
     * to the message handler on the JavaFX thread.
     * @author Muhammad Ahmad
     */
    private void startListening() {
        Thread listenThread = new Thread(() -> {
            try {
                String line;
                while (running && (line = in.readLine()) != null) {
                    String msg = line;
                    Platform.runLater(() -> {
                        if (messageHandler != null) messageHandler.accept(msg);
                    });
                }
                if (running) {
                    Platform.runLater(() -> {
                        if (messageHandler != null) messageHandler.accept("DISCONNECTED");
                    });
                }
            } catch (IOException e) {
                if (running) {
                    Platform.runLater(() -> {
                        if (messageHandler != null) messageHandler.accept("DISCONNECTED");
                    });
                }
            }
        });
        listenThread.setDaemon(true);
        listenThread.start();
    }
}
