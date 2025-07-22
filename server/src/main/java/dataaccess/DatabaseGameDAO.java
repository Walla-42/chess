package dataaccess;

import dataaccess.Interfaces.GameDAO;
import dataaccess.exceptions.DataAccessException;
import model.GameData;
import model.GamesObject;

import java.sql.SQLException;
import java.util.Collection;

public class DatabaseGameDAO implements GameDAO {

    @Override
    public Collection<GamesObject> listGames() {
        throw new RuntimeException("not yet implemented");
    }

    @Override
    public GameData createGame(String gameName) {
        throw new RuntimeException("not yet implemented");
    }

    @Override
    public GameData getGame(Integer gameID) {
        throw new RuntimeException("not yet implemented");
    }

    @Override
    public void updateGame(GameData updatedGameData) {
        throw new RuntimeException("not yet implemented");
    }

    @Override
    public void clearDB() {
        throw new RuntimeException("not yet implemented");
    }

    public static void setup() {
        try (var conn = DatabaseManager.getConnection()) {
            var createTable = conn.prepareStatement("""
                    CREATE TABLE IF NOT EXISTS gamedatabase (
                        gameID INTEGER PRIMARY KEY,
                        white_username VARCHAR(50),
                        black_username VARCHAR(50),
                        game_name VARCHAR(100),
                        chess_game TEXT NOT NULL,
                        FOREIGN KEY (white_username) REFERENCES userdatabase(username) ON DELETE SET NULL,
                        FOREIGN KEY (black_username) REFERENCES userdatabase(username) ON DELETE SET NULL
                        );
                    """);
            createTable.executeUpdate();

        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException("Error: failed to create gameData table", e);
        }
    }
}
