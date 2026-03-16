package ca.ucalgary.seng300;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class playerTest {
    private Player player;

        @BeforeEach
    void setUp() {
            player = new Player();
            player.playerId = "0001";
        }

        @Test
    void testCreatePlayer(){
            assertEquals("0001", player.playerId);
        }
}
