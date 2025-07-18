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
}
