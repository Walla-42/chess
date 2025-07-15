package dataaccess;

import chess.ChessGame;
import model.GamesObject;
import model.GameData;

import java.util.Collection;

public interface GameDAO {
    Collection<GamesObject> listGames();

    GameData createGame(String gameName);

    GameData getGame(int gameID);

    void updateGame(GameData updatedGameData);
}
