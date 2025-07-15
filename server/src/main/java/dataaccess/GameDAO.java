package dataaccess;

import responses.ListGameResponse;
import model.GameData;

import java.util.Collection;

public interface GameDAO {
    Collection<ListGameResponse> listGames();

    GameData createGame(String gameName);
}
