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

    // login() Tests

    @Test
    void testLoginCorrect_ValidEmailAndPassword() {
        // admin account exists with password "admin"
        UserData user = UserManager.login("admin@sgp.com", "admin");
        assertNotNull(user, "Logging in with correct email and password should succeed");
        assertEquals("admin@sgp.com", user.getEmail());
    }

    @Test
    void testLoginIncorrect_WrongPassword() {
        UserData user = UserManager.login("admin@sgp.com", "wrongpassword");
        assertNull(user, "Logging in with an incorrect password should return null");
    }

    @Test
    void testLoginEdge_LoginByUsername() {
        // Dummy user "MuhammadA" with password "123"
        UserData user = UserManager.login("MuhammadA", "123");
        assertNotNull(user, "Logging in with a valid username (instead of email) should succeed");
        assertEquals("muhammad.ahmad@sgp.com", user.getEmail());
    }

    @Test
    void testLoginEdge_CaseInsensitiveEmail() {
        UserData user = UserManager.login("ADMIN@SGP.COM", "admin");
        assertNotNull(user, "Email matching should be case-insensitive");
    }

    @Test
    void testLoginEdge_NonexistentUser() {
        UserData user = UserManager.login("nobody@nowhere.com", "anything");
        assertNull(user, "Login with a non-existent email should return null");
    }


    // getCurrentUser() Tests


    @Test
    void testGetCurrentUserCorrect_AfterLogin() {
        UserManager.login("admin@sgp.com", "admin");
        UserData current = UserManager.getCurrentUser();
        assertNotNull(current, "getCurrentUser should return the logged-in user");
        assertEquals("admin@sgp.com", current.getEmail());
    }

    @Test
    void testGetCurrentUserIncorrect_BeforeLogin() {
        UserData current = UserManager.getCurrentUser();
        assertNull(current);
    }


    // getAllUsers() Tests


    @Test
    void testGetAllUsersCorrect_ReturnsNonEmptyList() {
        List<UserData> allUsers = UserManager.getAllUsers();
        assertNotNull(allUsers, "getAllUsers shouldnt return null");
        assertFalse(allUsers.isEmpty(), "getAllUsers should contain at least the admin and dummy user");
    }

    @Test
    void testGetAllUsersEdge_ListIsUnmodifiable() {
        List<UserData> allUsers = UserManager.getAllUsers();
        assertThrows(UnsupportedOperationException.class, () -> {
            allUsers.add(new UserData("edit@test.com", "hacker", "12345"));
        }, "getAllUsers should return an unmodifiable list");
    }


    // changeUsername() Tests


    @Test
    void testChangeUsernameCorrect_ValidNewUsername() {
        UserManager.register("newuser@test.com", "password123");
        boolean result = UserManager.changeUsername("UniqueNewName");
        assertTrue(result);
        assertEquals("UniqueNewName", UserManager.getCurrentUser().getUsername());
    }

    @Test
    void testChangeUsernameIncorrect_NoUserLoggedIn() {
        // setup already calls logout
        boolean result = UserManager.changeUsername("AnyName");
        assertFalse(result, "changeUsername should return false, no user is logged in");
    }

    @Test
    void testChangeUsernameEdge_NullUsername() {
        UserManager.login("admin@sgp.com", "admin");
        boolean result = UserManager.changeUsername(null);
        assertFalse(result, "changeUsername should return false for a null username");
    }

    @Test
    void testChangeUsernameEdge_EmptyUsername() {
        UserManager.login("admin@sgp.com", "admin");
        boolean result = UserManager.changeUsername("   ");
        assertFalse(result, "changeUsername should return false for a blank/whitespace-only username");
    }

    @Test
    void testChangeUsernameEdge_UsernameWithPipe() {
        UserManager.register("pipeusr@test.com", "password123");
        boolean result = UserManager.changeUsername("name|pipe");
        assertFalse(result, "changeUsername should reject usernames containing '|'");
    }

    @Test
    void testChangeUsernameEdge_UsernameTakenByAnother() {
        UserManager.register("takenuser@test.com", "password123");
        // "admin" username is taken by the admin account
        boolean result = UserManager.changeUsername("admin");
        assertFalse(result, "changeUsername should fail when the name is already taken by another user");
    }


    // changeEmail() Tests


    @Test
    void testChangeEmailCorrect_ValidNewEmail() {
        UserManager.register("chagemail@test.com", "password123");
        UserManager.login("chagemail@test.com", "password123");
        boolean result = UserManager.changeEmail("newemail@test.com");
        assertTrue(result, "Changing to an available valid email should succeed");
        assertEquals("newemail@test.com", UserManager.getCurrentUser().getEmail());
    }

    @Test
    void testChangeEmailIncorrect_NoAtSymbol() {
        UserManager.register("chagemail@test.com", "password123");
        boolean result = UserManager.changeEmail("invalidemail.com");
        assertFalse(result, "changeEmail should reject emails without '@'");
    }

    @Test
    void testChangeEmailEdge_NoUserLoggedIn() {
        boolean result = UserManager.changeEmail("new@test.com");
        assertFalse(result, "changeEmail should return false when no user is logged in");
    }

    @Test
    void testChangeEmailEdge_EmailWithPipe() {
        UserManager.register("chagemail@test.com", "password123");
        boolean result = UserManager.changeEmail("bad|pipe@test.com");
        assertFalse(result, "changeEmail should reject emails containing '|'");
    }

    @Test
    void testChangeEmailEdge_EmailTakenByAnother() {
        UserManager.register("chagemail@test.com", "password123");
        // admin@sgp.com is taken
        boolean result = UserManager.changeEmail("admin@sgp.com");
        assertFalse(result, "changeEmail should fail when the email is already taken by another user");
    }


    // changePassword() Tests


    @Test
    void testChangePasswordCorrect_ValidOldAndNewPassword() {
        UserManager.register("changepw@test.com", "oldPass123");
        boolean result = UserManager.changePassword("oldPass123", "newPass456");
        assertTrue(result, "Changing password with correct old password and valid new password should succeed");
    }

    @Test
    void testChangePasswordIncorrect_WrongOldPassword() {
        UserManager.register("changepw@test.com", "correctOld");
        boolean result = UserManager.changePassword("wrongOld", "newPass456");
        assertFalse(result, "changePassword should fail when the old password does not match");
    }

    @Test
    void testChangePasswordEdge_NoUserLoggedIn() {
        boolean result = UserManager.changePassword("old", "new123");
        assertFalse(result, "changePassword should return false when no user is logged in");
    }

    @Test
    void testChangePasswordEdge_NewPasswordTooShort() {
        UserManager.register("changepw@test.com", "oldPass123");
        boolean result = UserManager.changePassword("oldPass123", "ab");
        assertFalse(result, "changePassword should reject a new password shorter than 3 characters");
    }

    @Test
    void testChangePasswordEdge_NewPasswordWithPipe() {
        UserManager.register("changepw@test.com", "oldPass123");
        boolean result = UserManager.changePassword("oldPass123", "new|pass");
        assertFalse(result, "changePassword should reject a new password containing '|'");
    }

    @Test
    void testChangePasswordEdge_NullNewPassword() {
        UserManager.register("changepw@test.com", "oldPass123");
        boolean result = UserManager.changePassword("oldPass123", null);
        assertFalse(result, "changePassword should reject a null new password");
    }


    // logout() Tests


    @Test
    void testLogoutCorrect_ClearsCurrentUser() {
        UserManager.login("admin@sgp.com", "admin");
        assertNotNull(UserManager.getCurrentUser());

        UserManager.logout();
        assertNull(UserManager.getCurrentUser(), "After logout, getCurrentUser should return null");
    }

    @Test
    void testLogoutEdge_LogoutWhenAlreadyLoggedOut() {
        // Already logged out from setUp
        assertDoesNotThrow(() -> {
            UserManager.logout();
        }, "Calling logout when already logged out should not throw an exception");
        assertNull(UserManager.getCurrentUser());
    }


    // getUserRank() Tests


    @Test
    void testGetUserRankCorrect_ReturnsPositiveRank() {
        UserManager.login("admin@sgp.com", "admin");
        UserData admin = UserManager.getCurrentUser();
        int rank = UserManager.getUserRank(admin);
        assertTrue(rank >= 1, "User rank should be a positive integer (1-based)");
        assertTrue(rank <= UserManager.getAllUsers().size(), "Rank should not exceed total number of users");
    }

    @Test
    void testGetUserRankCorrect_HighestPointsIsRankOne() {
        // MuhammadA has the most total points (1200 TTT + 500 C4 = 1700)
        UserManager.login("MuhammadA", "123");
        UserData topUser = UserManager.getCurrentUser();
        int rank = UserManager.getUserRank(topUser);
        assertEquals(1, rank, "The user with the highest total points should be ranked #1");
    }

    @Test
    void testGetUserRankEdge_NewUserHasLowestRank() {
        UserData newUser = UserManager.register("newranked@test.com", "password123");
        int rank = UserManager.getUserRank(newUser);
        // A brand new user with 0 points should be among the lowest ranks
        assertTrue(rank > 1);
    }


    // loadUsers() and saveUsers() Tests


    @Test
    void testLoadUsersCorrect_PersistsAfterSaveAndReload() {
        UserManager.register("persist@test.com", "password123");
        UserManager.saveUsers();
        UserManager.loadUsers();

        // The registered user should still exist after reload
        UserData user = UserManager.login("persist@test.com", "password123");
        assertNotNull(user, "A registered user should persist after saveUsers + loadUsers");
    }

    @Test
    void testLoadUsersEdge_RelinksCurrentUserAfterReload() {
        UserManager.login("admin@sgp.com", "admin");
        assertNotNull(UserManager.getCurrentUser());

        // Reload should re-link the current user by email
        UserManager.loadUsers();
        UserData current = UserManager.getCurrentUser();
        assertNotNull(current, "currentUser should be re-linked after loadUsers");
        assertEquals("admin@sgp.com", current.getEmail());
    }

}