package dataaccess;

import chess.ChessGame;
import dataaccess.exceptions.DataAccessException;
import dataaccess.exceptions.DatabaseAccessException;
import model.GameData;
import model.GamesObject;
import model.UserData;
import org.junit.jupiter.api.*;

import java.sql.SQLException;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

class DatabaseUserDAOTests {
    private static final DatabaseGameDAO gameDAO = new DatabaseGameDAO();
    private static final DatabaseUserDAO userDAO = new DatabaseUserDAO();
    private final String validUsername = "username";
    private final String validPassword = "password";
    private final String validEmail = "email@example.com";
    private final String gameName = "TestGame";

    @BeforeAll
    static void setupDB() {
        try {
            DatabaseManager.createDatabase();
            DatabaseUserDAO.setup();
            DatabaseGameDAO.setup();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeEach
    void clearTables() {
        try (var conn = DatabaseManager.getConnection(); var stmt = conn.createStatement()) {
            stmt.executeUpdate("DELETE FROM gamedatabase");
            stmt.executeUpdate("DELETE FROM userdatabase");
            userDAO.putUser(new UserData(validUsername, validPassword, validEmail));
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException("Failed to clear tables", e);
        }
    }

    @AfterAll
    static void cleanupTables() {
        try (var conn = DatabaseManager.getConnection(); var stmt = conn.createStatement()) {
            stmt.executeUpdate("DELETE FROM gamedatabase");
            stmt.executeUpdate("DELETE FROM userdatabase");
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException("Cleanup failed", e);
        }
    }

    @Test
    void putUserPositive() throws DataAccessException {
        UserData user = new UserData("newUser", "pass", "new@example.com");
        assertDoesNotThrow(() -> userDAO.putUser(user));
        assertNotNull(userDAO.getUser("newUser"));
    }

    @Test
    void putUserNegative() throws DataAccessException {
        UserData user = new UserData(null, validPassword, validEmail);
        assertThrows(DatabaseAccessException.class, () -> userDAO.putUser(user));
    }

    @Test
    void getUserPositive() throws DataAccessException {
        UserData user = userDAO.getUser(validUsername);
        assertNotNull(user);
        assertEquals(validUsername, user.username());
    }

    @Test
    void getUserNegative() throws DataAccessException {
        assertNull(userDAO.getUser("nonexistent"));
    }

    @Test
    void clearUserDB() throws DataAccessException {
        userDAO.clearDB();
        assertNull(userDAO.getUser(validUsername));
    }


}
