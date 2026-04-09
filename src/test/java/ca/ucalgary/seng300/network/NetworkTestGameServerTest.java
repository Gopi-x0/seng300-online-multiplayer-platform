package ca.ucalgary.seng300.network;

import javafx.application.Platform;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

public class NetworkTestGameServerTest {

    private GameServer server;
    private GameClient client;

    private static int nextPort = 5500;
    private int serverPort;

    private static int acquirePort() { return nextPort++; }

    // start the JavaFX toolkit once so Platform.runLater works in tests
    @BeforeAll
    static void initJavaFX() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        try {
            Platform.startup(latch::countDown);
        } catch (IllegalStateException e) {
            latch.countDown();
        }
        latch.await(3, TimeUnit.SECONDS);
    }

    @BeforeEach
    void setUp() throws IOException {
        serverPort = acquirePort();
        server = new GameServer(serverPort);
        client = new GameClient();
    }

    @AfterEach
    void tearDown() {
        client.shutdown();
        server.shutdown();
    }

    // connect the server and client together and wait for the handshake
    private void connectPair() throws Exception {
        server.waitForClient();
        client.connect("localhost", serverPort);
        Thread.sleep(250);
    }

    private void writeLine(Socket s, String line) throws IOException {
        new PrintWriter(s.getOutputStream(), true).println(line);
    }

    private String readLine(Socket s) throws IOException {
        return new BufferedReader(new InputStreamReader(s.getInputStream())).readLine();
    }

    // gameServer tests

    // make sure the server is on the right port
    @Test
    void serverBindsToCorrectPort() {
        assertEquals(serverPort, server.getPort());
    }

    // new server should show up as the active instance
    @Test
    void newServerIsActiveInstance() {
        assertSame(server, GameServer.getActive());
    }

    // calling shutdownActive should clear the active server
    @Test
    void shutdownActiveClearsServer() {
        GameServer.shutdownActive();
        assertNull(GameServer.getActive());
    }
    // port scan should land on a valid port
    @Test
    void portScanFindsValidPort() throws IOException {
        GameServer scanned = GameServer.createWithPortScan();
        try {
            int p = scanned.getPort();
            assertTrue(p >= GameServer.DEFAULT_PORT && p <= GameServer.MAX_PORT);
        } finally {
            scanned.shutdown();
        }
    }

    // port scan should skip a port that is already taken
    @Test
    void portScanSkipsTakenPort() throws IOException {
        GameServer blocker = new GameServer(GameServer.DEFAULT_PORT);
        try {
            GameServer scanned = GameServer.createWithPortScan();
            try {
                assertTrue(scanned.getPort() > GameServer.DEFAULT_PORT);
            } finally {
                scanned.shutdown();
            }
        } finally {
            blocker.shutdown();
        }
    }

    // after shutdown the server socket is closed so nothing can connect to it
    @Test
    void serverClosedAfterShutdown() {
        server.shutdown();
        assertThrows(Exception.class, () -> new Socket("localhost", serverPort).close());
    }

    // server sends a message and the client gets it
    @Test
    void serverSendReachesClient() throws Exception {
        server.waitForClient();
        Socket raw = new Socket("localhost", serverPort);
        Thread.sleep(250);
        server.send("HELLO_CLIENT");
        assertEquals("HELLO_CLIENT", readLine(raw));
        raw.close();
    }



    // send with no client connected should not crash
    @Test
    void serverSendBeforeClientDoesNothing() {
        assertDoesNotThrow(() -> server.send("TOO_EARLY"));
    }

    // client sends a message and the server handler gets it
    @Test
    void serverHandlerGetsMessageFromClient() throws Exception {
        AtomicReference<String> received = new AtomicReference<>();
        CountDownLatch latch = new CountDownLatch(1);
        server.setMessageHandler(msg -> { received.set(msg); latch.countDown(); });

        server.waitForClient();
        Socket raw = new Socket("localhost", serverPort);
        Thread.sleep(250);
        writeLine(raw, "PING");

        assertTrue(latch.await(3, TimeUnit.SECONDS));
        assertEquals("PING", received.get());
        raw.close();
    }

    // server gets several messages and they come in the right order
    @Test
    void serverHandlerGetsMessagesInOrder() throws Exception {
        List<String> received = new ArrayList<>();
        CountDownLatch latch = new CountDownLatch(3);
        server.setMessageHandler(msg -> { received.add(msg); latch.countDown(); });

        server.waitForClient();
        Socket raw = new Socket("localhost", serverPort);
        Thread.sleep(250);
        writeLine(raw, "ONE");
        writeLine(raw, "TWO");
        writeLine(raw, "THREE");

        assertTrue(latch.await(3, TimeUnit.SECONDS));
        assertEquals(List.of("ONE", "TWO", "THREE"), received);
        raw.close();
    }

    // server should get DISCONNECTED when the client closes
    @Test
    void serverGetsDisconnectedWhenClientLeaves() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<String> lastMsg = new AtomicReference<>();
        server.setMessageHandler(msg -> {
            lastMsg.set(msg);
            if ("DISCONNECTED".equals(msg)) latch.countDown();
        });

        server.waitForClient();
        Socket raw = new Socket("localhost", serverPort);
        Thread.sleep(250);
        raw.close();

        assertTrue(latch.await(3, TimeUnit.SECONDS));
        assertEquals("DISCONNECTED", lastMsg.get());
    }

    // calling shutdown on purpose should not trigger DISCONNECTED
    @Test
    void serverShutdownDoesNotFireDisconnected() throws Exception {
        List<String> messages = new ArrayList<>();
        server.setMessageHandler(messages::add);

        server.waitForClient();
        Socket raw = new Socket("localhost", serverPort);
        Thread.sleep(250);
        server.shutdown();
        raw.close();
        Thread.sleep(400);

        assertFalse(messages.contains("DISCONNECTED"));
    }
    // resetClient should kick the client but keep the server running
    @Test
    void resetClientKeepsServerAlive() throws Exception {
        server.waitForClient();
        Socket raw = new Socket("localhost", serverPort);
        Thread.sleep(250);
        server.resetClient();
        assertEquals(serverPort, server.getPort());
        raw.close();
    }

    // shutting down twice should not throw
    @Test
    void serverDoubleShutdownIsSafe() {
        assertDoesNotThrow(() -> {
            server.shutdown();
            server.shutdown();
        });
    }

    // port should be free after shutdown so something else can use it
    @Test
    void serverShutdownFreesPort() throws IOException {
        server.shutdown();
        GameServer rebound = new GameServer(serverPort);
        try {
            assertEquals(serverPort, rebound.getPort());
        } finally {
            rebound.shutdown();
        }
    }

    //  GameClient tests

    // connecting should make this client the active one
    @Test
    void clientConnectBecomesActive() throws Exception {
        GameClient.shutdownActive();
        connectPair();
        assertSame(client, GameClient.getActive());
    }

    // connecting to nothing should throw
    @Test
    void clientConnectFailsWithNoServer() {
        assertThrows(IOException.class, () -> client.connect("localhost", 6499));
    }

    // shutdownActive should clear the client reference
    @Test
    void clientShutdownActiveClearsInstance() throws Exception {
        connectPair();
        GameClient.shutdownActive();
        assertNull(GameClient.getActive());
    }

    // client sends a message and the server gets it
    @Test
    void clientSendReachesServer() throws Exception {
        AtomicReference<String> received = new AtomicReference<>();
        CountDownLatch latch = new CountDownLatch(1);
        server.setMessageHandler(msg -> { received.set(msg); latch.countDown(); });

        connectPair();
        client.send("HELLO_SERVER");

        assertTrue(latch.await(3, TimeUnit.SECONDS));
        assertEquals("HELLO_SERVER", received.get());
    }

    // send before connecting should not crash
    @Test
    void clientSendBeforeConnectDoesNothing() {
        assertDoesNotThrow(() -> client.send("TOO_EARLY"));
    }

    // server pushes a message and the client handler gets it
    @Test
    void clientHandlerGetsMessageFromServer() throws Exception {
        AtomicReference<String> received = new AtomicReference<>();
        CountDownLatch latch = new CountDownLatch(1);
        client.setMessageHandler(msg -> { received.set(msg); latch.countDown(); });

        connectPair();
        server.send("SERVER_PUSH");

        assertTrue(latch.await(3, TimeUnit.SECONDS));
        assertEquals("SERVER_PUSH", received.get());
    }

    // client gets several messages and they come in the right order
    @Test
    void clientHandlerGetsMessagesInOrder() throws Exception {
        List<String> received = new ArrayList<>();
        CountDownLatch latch = new CountDownLatch(3);
        client.setMessageHandler(msg -> { received.add(msg); latch.countDown(); });

        connectPair();
        server.send("X");
        server.send("Y");
        server.send("Z");

        assertTrue(latch.await(3, TimeUnit.SECONDS));
        assertEquals(List.of("X", "Y", "Z"), received);
    }

    // client should get DISCONNECTED when the server closes
    @Test
    void clientGetsDisconnectedWhenServerLeaves() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<String> lastMsg = new AtomicReference<>();
        client.setMessageHandler(msg -> {
            lastMsg.set(msg);
            if ("DISCONNECTED".equals(msg)) latch.countDown();
        });

        connectPair();
        server.shutdown();

        assertTrue(latch.await(3, TimeUnit.SECONDS));
        assertEquals("DISCONNECTED", lastMsg.get());
    }

    // calling client shutdown on purpose should not trigger DISCONNECTED
    @Test
    void clientShutdownDoesNotFireDisconnected() throws Exception {
        List<String> messages = new ArrayList<>();
        client.setMessageHandler(messages::add);

        connectPair();
        client.shutdown();
        Thread.sleep(400);

        assertFalse(messages.contains("DISCONNECTED"));
    }

    // client sends, server echoes, client receives the echo
    @Test
    void fullRoundTripWorks() throws Exception {
        AtomicReference<String> clientReceived = new AtomicReference<>();
        CountDownLatch clientLatch = new CountDownLatch(1);
        client.setMessageHandler(msg -> { clientReceived.set(msg); clientLatch.countDown(); });
        server.setMessageHandler(msg -> server.send("ECHO:" + msg));

        connectPair();
        client.send("ROUND_TRIP");

        assertTrue(clientLatch.await(3, TimeUnit.SECONDS));
        assertEquals("ECHO:ROUND_TRIP", clientReceived.get());
    }

    @Test
    void resetClientAllowsNewClientToConnect() throws Exception {
        // first client connects
        server.waitForClient();
        GameClient firstClient = new GameClient();
        firstClient.connect("localhost", serverPort);
        Thread.sleep(250);

        // reset first client connection
        server.resetClient();
        firstClient.shutdown();

        // server waits for a new client again
        server.waitForClient();

        // simple holders :D
        String[] received = new String[1];
        boolean[] gotMessage = new boolean[1];

        server.setMessageHandler(msg -> {
            received[0] = msg;
            gotMessage[0] = true;
        });

        // second client connects
        GameClient secondClient = new GameClient();
        try {
            secondClient.connect("localhost", serverPort);
            Thread.sleep(250);

            secondClient.send("HELLO_AGAIN");
            Thread.sleep(250);

            assertTrue(gotMessage[0]);
            assertEquals("HELLO_AGAIN", received[0]);
        } finally {
            secondClient.shutdown();
        }
    }
}
