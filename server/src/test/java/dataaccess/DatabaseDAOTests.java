package dataaccess;

import chess.ChessGame;
import dataaccess.exceptions.BadRequestException;
import dataaccess.exceptions.DataAccessException;
import dataaccess.exceptions.DatabaseAccessException;
import model.AuthData;
import model.GameData;
import model.GamesObject;
import model.UserData;
import org.junit.jupiter.api.*;

import java.sql.SQLException;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

class CombinedDatabaseDAOTests {
    private final DatabaseAuthDAO authDao = new DatabaseAuthDAO();
    private final DatabaseUserDAO userDao = new DatabaseUserDAO();
    private final DatabaseGameDAO gameDao = new DatabaseGameDAO();

    private final String validAuth = "validAuthToken";
    private final String validUsername = "username";
    private final String validPassword = "password";
    private final String validEmail = "email@example.com";
    private final String gameName = "TestGame";

    @BeforeAll
    public static void databaseSetup() {
        try {
            DatabaseManager.createDatabase();
            DatabaseAuthDAO.setup();
            DatabaseUserDAO.setup();
            DatabaseGameDAO.setup();
        } catch (DataAccessException e) {
            throw new RuntimeException("Error: failed to create database", e);
        }
    }

    @BeforeEach
    public void clear() {
        try (var conn = DatabaseManager.getConnection(); var stmt = conn.createStatement()) {
            stmt.executeUpdate("DELETE FROM authdatabase");
            stmt.executeUpdate("DELETE FROM userdatabase");
            stmt.executeUpdate("DELETE FROM gamedatabase");
            userDao.putUser(new UserData(validUsername, validPassword, validEmail));
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException("Failed to clear the database", e);
        }
    }

    @AfterAll
    public static void cleanupTables() {
        try (var conn = DatabaseManager.getConnection(); var stmt = conn.createStatement()) {
            stmt.executeUpdate("DELETE FROM authdatabase");
            stmt.executeUpdate("DELETE FROM userdatabase");
            stmt.executeUpdate("DELETE FROM gamedatabase");
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException("Failed to drop test tables", e);
        }
    }

//-------------------------------------------GameDAO Tests--------------------------------------------------------------

    @Test
    void createGamePositive() throws DataAccessException {
        GameData game = gameDao.createGame(gameName);
        assertNotNull(game);
        assertEquals(gameName, game.gameName());
    }


    @Test
    void getGamePositive() throws DataAccessException {
        GameData game = gameDao.createGame(gameName);
        GameData retrieved = gameDao.getGame(game.gameID());
        assertNotNull(retrieved);
        assertEquals(gameName, retrieved.gameName());
    }

    @Test
    void getGameNegative() throws DataAccessException {
        assertNull(gameDao.getGame(9999));
    }

    @Test
    void updateGamePositive() throws DataAccessException {
        GameData original = gameDao.createGame(gameName);

        userDao.putUser(new UserData("opponent", "password", "opponent@example.com"));

        GameData updated = new GameData(original.gameID(), validUsername, "opponent", "UpdatedGame", new ChessGame());

        gameDao.updateGame(updated);

        GameData result = gameDao.getGame(original.gameID());

        assertEquals("UpdatedGame", result.gameName());
        assertEquals(validUsername, result.whiteUserName());
        assertEquals("opponent", result.blackUserName());
    }


    @Test
    void updateGameNegative() {
        GameData invalid = new GameData(9999, validUsername, "opponent", "BadGame", new ChessGame());
        assertThrows(DatabaseAccessException.class, () -> gameDao.updateGame(invalid));
    }

    @Test
    void listGamesPositive() throws DataAccessException {
        gameDao.createGame("Game1");
        gameDao.createGame("Game2");
        Collection<?> games = gameDao.listGames();
        assertEquals(2, games.size());
    }

    @Test
    void listGamesNegative() {
        assertDoesNotThrow(() -> {
            Collection<GameData> games = gameDao.listGames();
            assertNotNull(games);
        });
    }

    @Test
    void clearGameDB() throws DataAccessException {
        gameDao.createGame("Game1");
        gameDao.clearDB();
        assertTrue(gameDao.listGames().isEmpty());
    }

//------------------------------------------AuthDAO Tests---------------------------------------------------------------

    @Test
    void addAuthPositive() throws DataAccessException {
        AuthData auth = new AuthData(validAuth, validUsername);
        authDao.addAuth(auth);

        AuthData result = authDao.getAuth(validAuth);
        assertNotNull(result);
        assertEquals(validUsername, result.username());
    }

    @Test
    void addAuthNegative() {
        AuthData badAuth1 = new AuthData(null, validUsername);
        AuthData badAuth2 = new AuthData(validAuth, null);

        assertThrows(BadRequestException.class, () -> authDao.addAuth(badAuth1));
        assertThrows(BadRequestException.class, () -> authDao.addAuth(badAuth2));
    }

    @Test
    public void addAuthDuplicateFails() throws DataAccessException {
        AuthData auth = new AuthData(validAuth, validUsername);
        authDao.addAuth(auth);

        assertThrows(DataAccessException.class, () -> authDao.addAuth(auth));
    }

    @Test
    void getAuthPositive() throws DataAccessException {
        AuthData auth = new AuthData(validAuth, validUsername);
        authDao.addAuth(auth);

        AuthData result = authDao.getAuth(validAuth);
        assertNotNull(result);
        assertEquals(validAuth, result.authToken());
        assertEquals(validUsername, result.username());
    }

    @Test
    void getAuthNegative() throws DataAccessException {
        AuthData result = authDao.getAuth("nonexistentToken");
        assertNull(result);
    }

    @Test
    void deleteAuthPositive() throws DataAccessException {
        AuthData auth = new AuthData(validAuth, validUsername);
        authDao.addAuth(auth);

        authDao.deleteAuth(validAuth);
        AuthData result = authDao.getAuth(validAuth);
        assertNull(result);
    }

    @Test
    void deleteAuthNegative() throws DataAccessException {
        authDao.deleteAuth("nonexistentToken");
    }

    @Test
    void tokenAlreadyExistsPositive() throws DataAccessException {
        AuthData auth = new AuthData(validAuth, validUsername);
        assertDoesNotThrow(() -> authDao.addAuth(auth));

        AuthData retrieved = authDao.getAuth(validAuth);
        assertNotNull(retrieved);
        assertEquals(validUsername, retrieved.username());

    }

    @Test
    void tokenAlreadyExistsNegative() throws DataAccessException {
        AuthData auth = new AuthData(validAuth, validUsername);
        authDao.addAuth(auth);

        AuthData duplicate = new AuthData(validAuth, validUsername);
        assertThrows(DataAccessException.class, () -> authDao.addAuth(duplicate));
    }

    @Test
    void clearDBPositive() throws DataAccessException {
        authDao.addAuth(new AuthData(validAuth, validUsername));
        authDao.addAuth(new AuthData("anotherAuth", validUsername));

        authDao.clearDB();

        assertNull(authDao.getAuth(validAuth));
        assertNull(authDao.getAuth("anotherAuth"));
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
        assertDoesNotThrow(() -> authDao.addAuth(newAuth));
    }

    @Test
    void setupNegative() {
        assertDoesNotThrow(() -> authDao.setup());
    }

    //-------------------------------------------UserDAO Tests--------------------------------------------------------------
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
