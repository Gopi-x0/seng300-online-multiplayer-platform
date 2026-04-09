//package ca.ucalgary.seng300.player;
//
//import ca.ucalgary.seng300.Player;
//import ca.ucalgary.seng300.Rank;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//
///**
// * Tests for Player object creation, field initialization, and basic getter/setter behavior.
// * Validates that player profiles are constructed correctly with all expected fields.
// */
//
//public class PlayerCreationTest {
//
//    private Player player;
//
//    @BeforeEach
//    void setUp() {
//        player = new Player();
//        player.playerId = "1111";
//        player.setUsername("testUser");
//        player.setEmail("test@example.com");
//        player.setRanking(Rank.BRONZE);
//    }
//
//    @Test
//    void testConstructorSetsUsernameCorrectly() {
//        assertEquals("testUser", player.getUsername(),
//                "Constructor should set the username to the provided value");
//    }
//
//    @Test
//    void testConstructorSetsEmailCorrectly() {
//        assertEquals("test@example.com", player.getEmail(),
//                "Constructor should set the email to the provided value");
//    }
//
//    @Test
//    void testConstructorSetsPasswordCorrectly() {
//        assertEquals("Pass123", player.getPassword(),
//                "Constructor should set the password to the provided value");
//    }
//
//    @Test
//    void testConstructorSetsRankingCorrectly() {
//        assertEquals(Rank.BRONZE, player.getRanking(),
//                "Constructor should set the ranking to the provided value");
//    }
//
//    @Test
//    void testConstructorWithHighRankSetsRankingCorrectly() {
//        player = new Player();
//        player.playerId = "1111";
//        player.setUsername("testUser");
//        player.setEmail("test@example.com");
//        player.setRanking(Rank.GRANDMASTER);
//        assertEquals(Rank.GRANDMASTER, grandmaster.getRanking(),
//                "Constructor should accept any valid Rank enum value including GRANDMASTER");
//    }
//
//    // ========== Default Constructor Tests ==========
//
//    @Test
//    void testDefaultConstructorCreatesPlayerWithNullUsername() {
//        Player defaultPlayer = new Player();
//        assertNull(defaultPlayer.getUsername(),
//                "Default constructor should leave username as null");
//    }
//
//    @Test
//    void testDefaultConstructorCreatesPlayerWithNullEmail() {
//        Player defaultPlayer = new Player();
//        assertNull(defaultPlayer.getEmail(),
//                "Default constructor should leave email as null");
//    }
//
//    @Test
//    void testDefaultConstructorCreatesPlayerWithNullPassword() {
//        Player defaultPlayer = new Player();
//        assertNull(defaultPlayer.getPassword(),
//                "Default constructor should leave password as null");
//    }
//
//    @Test
//    void testDefaultConstructorCreatesPlayerWithNullRanking() {
//        Player defaultPlayer = new Player();
//        assertNull(defaultPlayer.getRanking(),
//                "Default constructor should leave ranking as null");
//    }
//
//    // ========== PlayerId Tests ==========
//
//    @Test
//    void testPlayerIdCanBeSetDirectly() {
//        player.playerId = "PLAYER_001";
//        assertEquals("PLAYER_001", player.playerId,
//                "playerId is public and should be settable directly");
//    }
//
//    @Test
//    void testPlayerIdDefaultsToNull() {
//        player = new Player();
//        assertNull(player.playerId,
//                "playerId should default to null when not explicitly set");
//    }
//
//    /*  NOT SURE HOW WE WANT TO DEAL WITH EMPTY OR WEIRD IDS YET, WILL COME BACK LATER
//    @Test
//    void testPlayerIdAcceptsEmptyString() {
//        player.playerId = "";
//        assertEquals("", player.playerId,
//                "playerId should accept an empty string value");
//    }
//     */
//
//    @Test
//    void testPlayerIdCanBeOverwritten() {
//        player.playerId = "FIRST_ID";
//        player.playerId = "SECOND_ID";
//        assertEquals("SECOND_ID", player.playerId,
//                "playerId should reflect the most recently assigned value");
//    }
//
//    // ========== Edge Case Tests ==========
//
//
//    // Use this test case and adjust for edge cases.
//    @Test
//    void testConstructorWithEmptyStringUsername() { // ALSO NOT SURE IF WE SHOULD BE ACCEPTING BLANK USERNAMES
//        Player emptyNamePlayer = new Player();
//        player.playerId = "1111";
//        player.setUsername("");
//        player.setEmail("e@e.com");
//        player.setRanking(Rank.SILVER);
//        assertEquals("", emptyNamePlayer.getUsername(),
//                "Constructor should accept an empty string username without error");
//    }
//}