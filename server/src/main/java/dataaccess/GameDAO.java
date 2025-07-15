package dataaccess;

import model.GamesObject;
import model.GameData;

import java.util.Collection;

public interface GameDAO {
    Collection<GamesObject> listGames();

    GameData createGame(String gameName);
}
