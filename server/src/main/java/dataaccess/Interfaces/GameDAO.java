package dataaccess.Interfaces;

import dataaccess.exceptions.DatabaseAccessException;
import model.GamesObject;
import model.GameData;

import java.util.Collection;

public interface GameDAO {
    Collection<GamesObject> listGames() throws DatabaseAccessException;

    GameData createGame(String gameName) throws DatabaseAccessException;

    GameData getGame(Integer gameID);

    void updateGame(GameData updatedGameData);

    void clearDB() throws DatabaseAccessException;
}
