package dataaccess;

import dataaccess.exceptions.BadRequestException;
import dataaccess.exceptions.DataAccessException;
import dataaccess.exceptions.DatabaseAccessException;
import dataaccess.interfaces.AuthDAO;
import dataaccess.interfaces.UserDAO;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.*;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class DatabaseAuthDAOTest {
    private static DatabaseAuthDAO authDAO = new DatabaseAuthDAO();
    private static DatabaseUserDAO userDAO = new DatabaseUserDAO();
    private final String validAuth = "validAuthToken";
    private final String validUsername = "username";
    private final String validPassword = "password";

    @BeforeAll
    public static void databaseSetup() {
        try {
            DatabaseManager.createDatabase();

            DatabaseAuthDAO.setup();
            DatabaseUserDAO.setup();

        } catch (DataAccessException e) {
            throw new RuntimeException("Error: failed to create database", e);
        }
    }


    @BeforeEach
    public void clear() {
        try (var conn = DatabaseManager.getConnection(); var statement = conn.createStatement()) {
            statement.executeUpdate("DELETE FROM authdatabase");
            statement.executeUpdate("DELETE FROM userdatabase");
            UserData user = new UserData(validUsername, validPassword, "email@example.com");
            userDAO.putUser(user);

        } catch (SQLException e) {
            throw new RuntimeException("Failed to clear the database", e);
        } catch (DataAccessException e) {
            throw new RuntimeException("could not access the database", e);
        }
    }

    @AfterAll
    public static void cleanupTables() {
        try (var conn = DatabaseManager.getConnection()) {
            try (var stmt = conn.createStatement()) {
                stmt.executeUpdate("DELETE FROM authdatabase");
                stmt.executeUpdate("DELETE FROM userdatabase");
            }
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException("Failed to drop test tables", e);
        }
    }


    @Test
    void addAuthPositive() throws DataAccessException {
        AuthData auth = new AuthData(validAuth, validUsername);
        authDAO.addAuth(auth);

        AuthData result = authDAO.getAuth(validAuth);
        assertNotNull(result);
        assertEquals(validUsername, result.username());
    }

    @Test
    void addAuthNegative() {
        AuthData badAuth1 = new AuthData(null, validUsername);
        AuthData badAuth2 = new AuthData(validAuth, null);

        assertThrows(BadRequestException.class, () -> authDAO.addAuth(badAuth1));
        assertThrows(BadRequestException.class, () -> authDAO.addAuth(badAuth2));
    }

    @Test
    public void addAuthDuplicateFails() throws DataAccessException {
        AuthData auth = new AuthData(validAuth, validUsername);
        authDAO.addAuth(auth);

        assertThrows(DataAccessException.class, () -> authDAO.addAuth(auth));
    }

    @Test
    void getAuthPositive() throws DataAccessException {
        AuthData auth = new AuthData(validAuth, validUsername);
        authDAO.addAuth(auth);

        AuthData result = authDAO.getAuth(validAuth);
        assertNotNull(result);
        assertEquals(validAuth, result.authToken());
        assertEquals(validUsername, result.username());
    }

    @Test
    void getAuthNegative() throws DataAccessException {
        AuthData result = authDAO.getAuth("nonexistentToken");
        assertNull(result);
    }

    @Test
    void deleteAuthPositive() throws DataAccessException {
        AuthData auth = new AuthData(validAuth, validUsername);
        authDAO.addAuth(auth);

        authDAO.deleteAuth(validAuth);
        AuthData result = authDAO.getAuth(validAuth);
        assertNull(result);
    }

    @Test
    void deleteAuthNegative() throws DataAccessException {
        authDAO.deleteAuth("nonexistentToken");
    }

    @Test
    void tokenAlreadyExistsPositive() throws DataAccessException {
        AuthData auth = new AuthData(validAuth, validUsername);
        assertDoesNotThrow(() -> authDAO.addAuth(auth));

        AuthData retrieved = authDAO.getAuth(validAuth);
        assertNotNull(retrieved);
        assertEquals(validUsername, retrieved.username());

    }

    @Test
    void tokenAlreadyExistsNegative() throws DataAccessException {
        AuthData auth = new AuthData(validAuth, validUsername);
        authDAO.addAuth(auth);

        AuthData duplicate = new AuthData(validAuth, validUsername);
        assertThrows(DataAccessException.class, () -> authDAO.addAuth(duplicate));
    }

    @Test
    void clearDBPositive() throws DataAccessException {
        authDAO.addAuth(new AuthData(validAuth, validUsername));
        authDAO.addAuth(new AuthData("anotherAuth", validUsername));

        authDAO.clearDB();

        assertNull(authDAO.getAuth(validAuth));
        assertNull(authDAO.getAuth("anotherAuth"));
    }


    @Test
    void setupPositive() {
        try (var conn = DatabaseManager.getConnection(); var stmt = conn.createStatement()) {
            stmt.executeUpdate("DROP TABLE IF EXISTS authdatabase");
        } catch (SQLException e) {
            fail("Failed to drop table: " + e.getMessage());
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }

        assertDoesNotThrow(DatabaseAuthDAO::setup);

        AuthData newAuth = new AuthData("newToken", validUsername);
        assertDoesNotThrow(() -> authDAO.addAuth(newAuth));
    }

    @Test
    void setupNegative() {
        assertDoesNotThrow(() -> authDAO.setup());
    }

}