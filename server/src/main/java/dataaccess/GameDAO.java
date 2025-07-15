package dataaccess;

import Responses.ListGameResponse;
import model.GameData;

import java.util.Collection;

public interface GameDAO {
    Collection<ListGameResponse> listGames();

    GameData createGame(String gameName);
}
