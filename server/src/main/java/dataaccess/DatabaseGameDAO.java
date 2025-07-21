package dataaccess;

import dataaccess.Interfaces.GameDAO;
import model.GameData;
import model.GamesObject;

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

    private final String createStatement = """
            CREATE TABLE IF NOT EXISTS User_Data (
                gameID INTEGER PRIMARY KEY,
                white_username VARCHAR(50),
                black_username VARCHAR(50),
                game_name VARCHAR(100),
                chess_game TEXT NOT NULL,
                FOREIGN KEY (white_username) REFERENCES UserData(username) ON DELETE SET NULL,
                FOREIGN KEY (black_username) REFERENCES UserData(username) ON DELETE SET NULL
                );
            """;
}
