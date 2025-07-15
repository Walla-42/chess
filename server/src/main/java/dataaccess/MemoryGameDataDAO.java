package dataaccess;

import model.GamesObject;
import chess.ChessGame;
import model.GameData;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

public class MemoryGameDataDAO implements GameDAO{
    HashMap<Integer, GameData> gameDatabase = new HashMap<>();
    HashSet<Integer> gameIDs = new HashSet<>();
    private static int gameID = 1;

    public Collection<GamesObject> listGames(){
        HashSet<GamesObject> activeGames = new HashSet<>();

        for (int gameID : gameIDs){
            GameData gameData = gameDatabase.get(gameID);
            GamesObject game = new GamesObject(gameData.getGameID(),
                    gameData.getWhiteUserName() != null ? gameData.getWhiteUserName() : "",
                    gameData.getBlackUserName() != null ? gameData.getBlackUserName() : "",
                    gameData.getGameName());

            activeGames.add(game);
        }
        return activeGames;
    }

    public GameData createGame(String gameName){
        GameData newGame = new GameData(gameID, null, null, gameName, new ChessGame());
        gameDatabase.put(gameID, newGame);
        gameIDs.add(gameID);
        gameID += 1;
        return newGame;
    }
}
