package dataaccess;

import chess.ChessGame;
import dataaccess.exceptions.DataAccessException;
import dataaccess.exceptions.DatabaseAccessException;
import model.GameData;
import model.GamesObject;
import model.UserData;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

class DatabaseGameDAOTest {
    private static final DatabaseGameDAO gameDao = new DatabaseGameDAO();
    private static final DatabaseUserDAO userDao = new DatabaseUserDAO();
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
            Collection<GamesObject> games = gameDao.listGames();
            assertNotNull(games);
        });
    }

    @Test
    void clearGameDB() throws DataAccessException {
        gameDao.createGame("Game1");
        gameDao.clearDB();
        assertTrue(gameDao.listGames().isEmpty());
    }


}