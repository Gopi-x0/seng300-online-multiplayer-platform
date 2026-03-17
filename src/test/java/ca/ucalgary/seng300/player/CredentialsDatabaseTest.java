package ca.ucalgary.seng300.player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class CredentialsDatabaseTest {

    private CredentialsDatabase database;

    @BeforeEach
    void setUp() {
        database = new CredentialsDatabase();
    }

    // ========== Initial State Tests ==========

    @Test
    void testNewDatabaseIsEmpty() {
        assertEquals(0, database.size(),
                "A newly created database should have zero players");
    }
}
