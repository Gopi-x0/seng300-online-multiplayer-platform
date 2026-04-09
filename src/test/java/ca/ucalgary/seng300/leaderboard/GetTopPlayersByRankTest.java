//package ca.ucalgary.seng300.leaderboard;
//
//import ca.ucalgary.seng300.PlayerProfile;
//import ca.ucalgary.seng300.Service.LeaderboardManager;
//import ca.ucalgary.seng300.Stubs.DatabaseStub;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//
//import java.util.List;
//
//public class GetTopPlayersByRankTest {
//
//
//    LeaderboardManager leaderboardManager;
//    DatabaseStub databaseStub;
//
//    @BeforeEach
//    void setUp() {
//        databaseStub = new DatabaseStub();
//        leaderboardManager = new LeaderboardManager(databaseStub);
//    }
//
//    @Test
//    void getTopPlayersByRankTest(){
//
//
//        // arrange
//        String randomGameType = "randomGametype";
//
//        // act
//        List<PlayerProfile> playersProfile = leaderboardManager.getTopPlayers(randomGameType);
//
//        // assert
//        assert !playersProfile.isEmpty();
//        assert playersProfile.size() == 2;
//    }
//}
