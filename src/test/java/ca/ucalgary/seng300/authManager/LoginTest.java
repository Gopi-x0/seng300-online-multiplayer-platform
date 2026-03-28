package ca.ucalgary.seng300.authManager;

import ca.ucalgary.seng300.PlayerProfile;
import ca.ucalgary.seng300.Service.AuthManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class LoginTest {

    private AuthManager authManager;

    @BeforeEach
    void setUp() {
        authManager = new AuthManager();
    }

    @Test
    void LoginTest() {

        // act
        String username = "someTestUser";

        // action
        PlayerProfile playerProfile = authManager.login(username);

        // assert
        assert playerProfile != null;
    }

}
