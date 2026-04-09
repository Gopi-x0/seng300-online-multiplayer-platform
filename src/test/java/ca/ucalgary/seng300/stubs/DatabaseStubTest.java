package ca.ucalgary.seng300.stubs;

import ca.ucalgary.seng300.model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class DatabaseStubTest {

    DatabaseStub stub;
    UserData userData;

    @BeforeEach
    void setup(){
        stub = new DatabaseStub();
        userData = stub.createUser("SampleEmail@g.c","user1","passwd");
    }

    @Test
    void createUserTest(){

        // User already created in before each method so just assert
        assertEquals("SampleEmail@g.c", userData.getEmail());
        assertEquals("user1",userData.getUsername());
        assertEquals("passwd",userData.getPassword());
    }

    @Test
    void authenticateUserTest(){

        // Authenticate User by email
        UserData authenticatedUserData = stub.authenticateUser("SampleEmail@g.c","passwd");

        // Assert
        assertEquals("SampleEmail@g.c", authenticatedUserData.getEmail());
        assertEquals("passwd", authenticatedUserData.getPassword());

        // Authenticate User by username
        UserData authenticatedUserData2 = stub.authenticateUser("user1","passwd");
        assertEquals("user1", authenticatedUserData2.getUsername());
        assertEquals("passwd", authenticatedUserData2.getPassword());
    }

    @ParameterizedTest
    @CsvSource({
            "SampleEmail2@g.c, passwd",
            "user2, passwd",
            "user1, passwd2"
    })
    void authenticateUserTest_Failure(String identifier, String passwordUsed){
        // Authenticate User
        UserData authenticatedUserData = stub.authenticateUser(identifier,passwordUsed);

        // Assert
        assert authenticatedUserData == null;
    }

    @Test
    void findUserByEmailTest_Success(){

        // Find user by email
        UserData user = stub.findUserByEmail("SampleEmail@g.c");

        // Assert that user is found
        assertNotNull(user);
    }

    @Test
    void findUserByEmailTest_Failure(){

        // Find user by email
        UserData user = stub.findUserByEmail("SampleEmail2@g.c");

        // Assert that user is not found
        assertNull(user);
    }

    @ParameterizedTest
    @CsvSource({
            "SampleEmail@g.c, user1, true",
            "SampleEmail1@g.c, user1, false"
    })
    void updateUsernameTest(String email, String newUsername, boolean expected){
        boolean actual = stub.updateUsername(email,newUsername);
        assertEquals(expected,actual);
    }

    @ParameterizedTest
    @CsvSource({
            "SampleEmail@g.c, SampleEmail@g.c, true",
            "SampleEmail1@g.c, SampleEmail@g.c, false"
    })
    void updateEmailTest(String oldEmail, String newEmail, boolean expected){
        boolean actual = stub.updateEmail(oldEmail,newEmail);
        assertEquals(expected,actual);
    }

    @ParameterizedTest
    @CsvSource({
            "SampleEmail@g.c, passwd, newPasswd, true",
            "SampleEmail1@g.c, passwd, newPasswd, false",
            "SampleEmail@g.c, passwd2, newPasswd, false"
    })
    void updatePasswordTest(String email, String oldPassword, String newPassword, boolean expected){
        boolean actual = stub.updatePassword(email,oldPassword,newPassword);
        assertEquals(expected,actual);
    }

    @ParameterizedTest
    @CsvSource({
            "SampleEmail@g.c, passwd, true",
            "SampleEmail1@g.c, passwd,false",
            "SampleEmail@g.c, passwd2, false"
    })
    void deleteUserTest(String email, String password, boolean expected){
        boolean actual = stub.deleteUser(email,password);
        assertEquals(expected,actual);
    }

    @Test
    void getUserCountTest(){
        stub.createUser("SampleEmail2@g.c", "user2", "passwd2");
        int actualUserCount = stub.getUserCount();
        assertEquals(2, actualUserCount);
    }

    @Test
    void getGlobalLeaderboardTest(){
        // Create 2 more users with some points
        UserData userData2 = stub.createUser("SampleEmail2@g.c", "user2", "passwd2");
        UserData userData3 = stub.createUser("SampleEmail3@g.c", "user3", "passwd3");

        // Set Points for users
        userData.setTttPoints(14);
        userData.setC4Points(3); // 17

        userData2.setTttPoints(0);
        userData2.setC4Points(3); // 3

        userData3.setTttPoints(10);
        userData3.setC4Points(1); // 11

        // Get Global Leaderboard list
        List<UserData> actualSortedUsers = stub.getGlobalLeaderboard();

        // assert list size is 3
        assertEquals(3, actualSortedUsers.size());

        // assert if list is sorted in Descending Order according to total points
        assertEquals(17, actualSortedUsers.get(0).getTotalPoints());
        assertEquals(11, actualSortedUsers.get(1).getTotalPoints());
        assertEquals(3, actualSortedUsers.get(2).getTotalPoints());
    }

    @Test
    void getTttLeaderboardTest(){
        // Create 2 more users with some points
        UserData userData2 = stub.createUser("SampleEmail2@g.c", "user2", "passwd2");
        UserData userData3 = stub.createUser("SampleEmail3@g.c", "user3", "passwd3");

        // Set Points for users
        userData.setTttPoints(1);
        userData2.setTttPoints(41);
        userData3.setTttPoints(10);

        // Get Global Leaderboard list
        List<UserData> actualSortedUsers = stub.getTttLeaderboard();

        // assert list size is 3
        assertEquals(3, actualSortedUsers.size());

        // assert if list is sorted in Descending Order according to total points
        assertEquals(41, actualSortedUsers.get(0).getTttPoints());
        assertEquals(10, actualSortedUsers.get(1).getTttPoints());
        assertEquals(1, actualSortedUsers.get(2).getTttPoints());
    }

    @Test
    void getC4LeaderboardTest(){
        // Create 2 more users with some points
        UserData userData2 = stub.createUser("SampleEmail2@g.c", "user2", "passwd2");
        UserData userData3 = stub.createUser("SampleEmail3@g.c", "user3", "passwd3");

        // Set Points for users
        userData.setC4Points(1);
        userData2.setC4Points(41);
        userData3.setC4Points(10);

        // Get Global Leaderboard list
        List<UserData> actualSortedUsers = stub.getC4Leaderboard();

        // assert list size is 3
        assertEquals(3, actualSortedUsers.size());

        // assert if list is sorted in Descending Order according to total points
        assertEquals(41, actualSortedUsers.get(0).getC4Points());
        assertEquals(10, actualSortedUsers.get(1).getC4Points());
        assertEquals(1, actualSortedUsers.get(2).getC4Points());
    }

    @Test
    void getUserRankTest_Success(){
        // Create 2 more users with some points
        UserData userData2 = stub.createUser("SampleEmail2@g.c", "user2", "passwd2");
        UserData userData3 = stub.createUser("SampleEmail3@g.c", "user3", "passwd3");

        // Set Points for users
        userData.setTttPoints(14);
        userData.setC4Points(3); // 17

        userData2.setTttPoints(0);
        userData2.setC4Points(3); // 3

        userData3.setTttPoints(10);
        userData3.setC4Points(1); // 11

        // assert on user rank
        int user1Rank = stub.getUserRank("SampleEmail@g.c");
        assertEquals(1,user1Rank);

        int user2Rank = stub.getUserRank("SampleEmail2@g.c");
        assertEquals(3,user2Rank);

        int user3Rank = stub.getUserRank("SampleEmail3@g.c");
        assertEquals(2,user3Rank);
    }

    @Test
    void getUserRankTest_Failure(){
        // assert on user rank where user email doesnt exist
        int userRank = stub.getUserRank("SampleEmail5@g.c");
        assertEquals(-1,userRank);
    }

    @Test
    void saveMatchRecordTest(){

        // Create 1 more users with some points
        UserData userData2 = stub.createUser("SampleEmail2@g.c", "user2", "passwd2");

        // save match record for c4 and ttt
        stub.saveMatchRecord("c4","SampleEmail@g.c","SampleEmail2@g.c","WIN");
        stub.saveMatchRecord("ttt","SampleEmail@g.c","SampleEmail3@g.c","LOSS");

        // Assert on matchRecord list
        List<DatabaseStub.MatchRecord> matchRecordPlayer1 = stub.getMatchHistory("SampleEmail@g.c",2);
        List<DatabaseStub.MatchRecord> matchRecordPlayer2 = stub.getMatchHistory("SampleEmail2@g.c",2);

        assertEquals(2,matchRecordPlayer1.size());
        assertEquals(1,matchRecordPlayer2.size());
    }

    @Test
    void getMatchHistoryTest(){
        // Create 1 more users with some points
        UserData userData2 = stub.createUser("SampleEmail2@g.c", "user2", "passwd2");

        // save match record for c4 and ttt
        stub.saveMatchRecord("c4","SampleEmail@g.c","SampleEmail2@g.c","WIN");
        stub.saveMatchRecord("ttt","SampleEmail@g.c","SampleEmail3@g.c","LOSS");

        // Assert on matchRecord list
        List<DatabaseStub.MatchRecord> matchRecordPlayer1 = stub.getMatchHistory("SampleEmail@g.c",2);
        List<DatabaseStub.MatchRecord> matchRecordPlayer2 = stub.getMatchHistory("SampleEmail3@g.c",2);

        assertEquals(2,matchRecordPlayer1.size());
        assertEquals(1,matchRecordPlayer2.size());
    }

    @Test
    void recordGameResultTest(){

    }
}

