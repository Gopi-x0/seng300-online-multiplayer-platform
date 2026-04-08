package ca.ucalgary.seng300.user;

import ca.ucalgary.seng300.model.UserData;
import ca.ucalgary.seng300.service.UserManager;
import org.junit.jupiter.api.*;

import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test cases for UserManager.
 * Each public method has a correct test, an incorrect test, and edge cases where appropriate.
 *
 * UserManager uses static state and file-based persistence (sgp_users.dat).
 * The static initializer pre-loads admin and dummy users, and tests are designed to work alongside them.
 * @author Dima Levshukov
 * @email dima.levshukov@ucalgary.ca
 */

public class UserManagerTest {

    @BeforeEach
    void setUp() {
        // Ensure no user is logged in before each test
        UserManager.logout();
    }

    // register() Tests

    @Test
    void testRegisterCorrect_ValidEmailAndPassword() {
        UserData user = UserManager.register("registertest_valid@test.com", "validPass");
        assertNotNull(user);
        assertEquals("registertest_valid@test.com", user.getEmail());
        assertEquals("registertest_valid", user.getUsername());
    }

    @Test
    void testRegisterIncorrect_NullEmail() {
        UserData user = UserManager.register(null, "validPass");
        assertNull(user, "Registering with a null email should return null");
    }

    @Test
    void testRegisterEdge_DuplicateEmail() {
        // admin@sgp.com is created by the static initializer
        UserData user = UserManager.register("admin@sgp.com", "somePass");
        assertNull(user, "An already taken email should return null");
    }

    @Test
    void testRegisterEdge_EmailWithPipeCharacter() {
        UserData user = UserManager.register("bad|email@test.com", "validPass");
        assertNull(user, "Emails containing '|' should be rejected (pipe is the data delimiter)");
    }

    @Test
    void testRegisterEdge_PasswordTooShort() {
        UserData user = UserManager.register("shortpw@test.com", "ab");
        assertNull(user, "Password shorter than 3 characters should be rejected");
    }

    @Test
    void testRegisterEdge_EmailMissingAtSymbol() {
        UserData user = UserManager.register("invalidemail.com", "validPass");
        assertNull(user, "Email without '@' should be rejected");
    }

    @Test
    void testRegisterEdge_NullPassword() {
        UserData user = UserManager.register("nullpw@test.com", null);
        assertNull(user, "Null password should be rejected");
    }

    @Test
    void testRegisterEdge_PasswordWithPipeCharacter() {
        UserData user = UserManager.register("pipepw@test.com", "pass|word");
        assertNull(user, "Password containing '|' should be rejected");
    }
}