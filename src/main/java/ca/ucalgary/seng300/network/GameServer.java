package ca.ucalgary.seng300.network;

import javafx.application.Platform;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.function.Consumer;

/**
 * TCP server wrapper for the host side of a multiplayer game.
 * Opens a ServerSocket, accepts one client connection, and provides
 * a simple line-based send/receive API.
 *
 * Usage:
 *   GameServer server = new GameServer(5300);
 *   server.setMessageHandler(msg -> { ... });
 *   server.waitForClient();
 *   server.send("HELLO");
 *   // later:
 *   server.shutdown();
 *
 * @author Muhammad Ahmad
 * @email muham.ahmad@ucalgary.ca
 */
public class GameServer {

    public static final int DEFAULT_PORT = 5300;
    public static final int MAX_PORT = 5325;

    private static GameServer activeInstance;

    private ServerSocket serverSocket;
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    private Consumer<String> messageHandler;
    private boolean running;

    /**
     * Creates a new server bound to the given port.
     * @author Muhammad Ahmad
     */
    public GameServer(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        running = true;
        activeInstance = this;
    }

    // Factory and Singleton Access

    /**
     * Scans ports from DEFAULT_PORT to MAX_PORT and creates a server on the first available one.
     * @author Muhammad Ahmad
     */
    public static GameServer createWithPortScan() throws IOException {
        for (int port = DEFAULT_PORT; port <= MAX_PORT; port++) {
            try {
                return new GameServer(port);
            } catch (IOException e) {
                if (port == MAX_PORT) {
                    System.err.println("GameServer: no available port in range " + DEFAULT_PORT + "-" + MAX_PORT);
                    throw e;
                }
            }
        }
        throw new IOException("No available port");
    }

    /**
     * Returns the currently active GameServer instance, or null if none.
     * @author Muhammad Ahmad
     */
    public static GameServer getActive() {
        return activeInstance;
    }

    /**
     * Shuts down the active server instance if one exists.
     * @author Muhammad Ahmad
     */
    public static void shutdownActive() {
        if (activeInstance != null) {
            activeInstance.shutdown();
            activeInstance = null;
        }
    }

    // Client Handling

    /**
     * Starts a background thread that waits for one client to connect.
     * Once connected, begins listening for messages.
     * @author Muhammad Ahmad
     */
    public void waitForClient() {
        Thread acceptThread = new Thread(() -> {
            try {
                clientSocket = serverSocket.accept();
                out = new PrintWriter(clientSocket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                startListening();
            } catch (IOException e) {
                if (running) {
                    fireMessage("SYS_ERROR:Could not accept connection");
                }
            }
        });
        acceptThread.setDaemon(true);
        acceptThread.start();
    }

    /**
     * Sends a message to the connected client if the connection is still active.
     * @author Muhammad Ahmad
     */
    public void send(String message) {
        if (out != null && running) {
            out.println(message);
        }
    }

    /**
     * Sets the callback that gets invoked when a message is received from the client.
     * @author Muhammad Ahmad
     */
    public void setMessageHandler(Consumer<String> handler) {
        this.messageHandler = handler;
    }

    /**
     * Returns the port this server is listening on, or -1 if not bound.
     * @author Muhammad Ahmad
     */
    public int getPort() {
        return serverSocket != null ? serverSocket.getLocalPort() : -1;
    }

    /**
     * Closes the client connection but keeps the server socket open for a new client.
     * @author Muhammad Ahmad
     */
    public void resetClient() {
        try { if (out != null) out.close(); } catch (Exception ignored) {}
        try { if (in != null) in.close(); } catch (Exception ignored) {}
        try { if (clientSocket != null) clientSocket.close(); } catch (Exception ignored) {}
        out = null;
        in = null;
        clientSocket = null;
    }

    /**
     * Fully shuts down the server, closing both client and server sockets.
     * @author Muhammad Ahmad
     */
    public void shutdown() {
        running = false;
        resetClient();
        try { if (serverSocket != null) serverSocket.close(); } catch (Exception ignored) {}
        if (activeInstance == this) activeInstance = null;
    }

    // Message Listening

    /**
     * Spawns a daemon thread that reads lines from the client and dispatches them
     * to the message handler on the JavaFX thread.
     * @author Muhammad Ahmad
     */
    private void startListening() {
        Thread listenThread = new Thread(() -> {
            try {
                String line;
                while (running && (line = in.readLine()) != null) {
                    String msg = line;
                    fireMessage(msg);
                }
                if (running) {
                    fireMessage("DISCONNECTED");
                }
            } catch (IOException e) {
                if (running) {
                    fireMessage("DISCONNECTED");
                }
            }
        });
        listenThread.setDaemon(true);
        listenThread.start();
    }

    /**
     * Delivers a message to the handler on the JavaFX application thread.
     * @author Muhammad Ahmad
     */
    private void fireMessage(String msg) {
        Platform.runLater(() -> {
            if (messageHandler != null) messageHandler.accept(msg);
        });
    }
}
