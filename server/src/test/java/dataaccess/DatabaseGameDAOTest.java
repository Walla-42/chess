package dataaccess;

import chess.ChessGame;
import dataaccess.exceptions.DataAccessException;
import dataaccess.exceptions.DatabaseAccessException;
import model.AuthData;
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
    void createGamePositive() throws DataAccessException {
        GameData game = gameDAO.createGame(gameName);
        assertNotNull(game);
        assertEquals(gameName, game.gameName());
    }


    @Test
    void getGamePositive() throws DataAccessException {
        GameData game = gameDAO.createGame(gameName);
        GameData retrieved = gameDAO.getGame(game.gameID());
        assertNotNull(retrieved);
        assertEquals(gameName, retrieved.gameName());
    }

    @Test
    void getGameNegative() throws DataAccessException {
        assertNull(gameDAO.getGame(9999));
    }

    @Test
    void updateGamePositive() throws DataAccessException {
        GameData original = gameDAO.createGame(gameName);

        userDAO.putUser(new UserData("opponent", "password", "opponent@example.com"));

        GameData updated = new GameData(original.gameID(), validUsername, "opponent", "UpdatedGame", new ChessGame());

        gameDAO.updateGame(updated);

        GameData result = gameDAO.getGame(original.gameID());

        assertEquals("UpdatedGame", result.gameName());
        assertEquals(validUsername, result.whiteUserName());
        assertEquals("opponent", result.blackUserName());
    }


    @Test
    void updateGameNegative() {
        GameData invalid = new GameData(9999, validUsername, "opponent", "BadGame", new ChessGame());
        assertThrows(DatabaseAccessException.class, () -> gameDAO.updateGame(invalid));
    }

    @Test
    void listGamesPositive() throws DataAccessException {
        gameDAO.createGame("Game1");
        gameDAO.createGame("Game2");
        Collection<?> games = gameDAO.listGames();
        assertEquals(2, games.size());
    }

    @Test
    void listGamesNegative() {
        assertDoesNotThrow(() -> {
            Collection<GamesObject> games = gameDAO.listGames();
            assertNotNull(games);
        });
    }

    @Test
    void clearGameDB() throws DataAccessException {
        gameDAO.createGame("Game1");
        gameDAO.clearDB();
        assertTrue(gameDAO.listGames().isEmpty());
    }


}