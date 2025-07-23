package dataaccess;

import dataaccess.exceptions.DataAccessException;
import dataaccess.exceptions.DatabaseAccessException;
import model.UserData;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class DatabaseUserDAOTests {
    private static final DatabaseUserDAO userDao = new DatabaseUserDAO();
    private final String validUsername = "username";
    private final String validPassword = "password";
    private final String validEmail = "email@example.com";

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

            userDao.putUser(new UserData(validUsername, validPassword, validEmail));

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
        assertDoesNotThrow(() -> userDao.putUser(user));
        assertNotNull(userDao.getUser("newUser"));
    }

    @Test
    void putUserNegative() throws DataAccessException {
        UserData user = new UserData(null, validPassword, validEmail);
        assertThrows(DatabaseAccessException.class, () -> userDao.putUser(user));
    }

    @Test
    void getUserPositive() throws DataAccessException {
        UserData user = userDao.getUser(validUsername);
        assertNotNull(user);
        assertEquals(validUsername, user.username());
    }

    @Test
    void getUserNegative() throws DataAccessException {
        assertNull(userDao.getUser("nonexistent"));
    }

    @Test
    void clearUserDB() throws DataAccessException {
        userDao.clearDB();
        assertNull(userDao.getUser(validUsername));
    }
}