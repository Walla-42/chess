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
            GamesObject game = new GamesObject(gameData.gameID(),
                    gameData.whiteUserName() != null ? gameData.whiteUserName() : "",
                    gameData.blackUserName() != null ? gameData.blackUserName() : "",
                    gameData.gameName());

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

    public GameData getGame(int gameID){
        return gameDatabase.get(gameID);
    }

    public void updateGame(GameData gameData){
        gameDatabase.remove(gameData.gameID());
        gameDatabase.put(gameData.gameID(), gameData);
        gameIDs.add(gameData.gameID());


    }
}
