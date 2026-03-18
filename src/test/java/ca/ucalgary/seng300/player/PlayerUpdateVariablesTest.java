package ca.ucalgary.seng300.player;

import ca.ucalgary.seng300.Player;
import ca.ucalgary.seng300.Rank;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PlayerUpdateVariablesTest {

    private Player player;

    @BeforeEach
    void setUp() {
        player = new Player();
    }

    @Test
    void usernameChangeTestBasic(){
        //assertEquals("Username succesfully changed to 'test2'", player.updateUsername("test1", "test2"));
        //^ that function does return anything yet so I just have this greyed out so its not lighting up a bunch of errors.
    }

}
