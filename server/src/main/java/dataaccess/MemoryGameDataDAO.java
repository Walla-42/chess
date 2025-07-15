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

    /**
     * Method to fetch all active games in the GameDatabase
     *
     * @return Collectin of GamesObject that can be used by the UserHandlerClass
     */
    public Collection<GamesObject> listGames(){
        HashSet<GamesObject> activeGames = new HashSet<>();

        for (int gameID : gameIDs){
            GameData gameData = gameDatabase.get(gameID);
            GamesObject game = new GamesObject(gameData.gameID(),
                    gameData.whiteUserName(),
                    gameData.blackUserName(),
                    gameData.gameName());

            activeGames.add(game);
        }
        return activeGames;
    }

    /**
     * A method for creating a new game and storing it in the GameDatabase
     *
     * @param gameName Desired name of the game
     * @return GameData for the new Game.
     */
    public GameData createGame(String gameName){
        GameData newGame = new GameData(gameID, null, null, gameName, new ChessGame());
        gameDatabase.put(gameID, newGame);
        gameIDs.add(gameID);
        gameID += 1;
        return newGame;
    }

    /**
     * A method for fetching GameData from the GameDatabase
     *
     * @param gameID ID of the game to be fetched
     * @return GameData for the Game
     */
    public GameData getGame(int gameID){
        return gameDatabase.get(gameID);
    }

    /**
     * Method for updating game data in database
     *
     * @param gameData GameData Object with updated game information
     */
    public void updateGame(GameData gameData){
        gameDatabase.remove(gameData.gameID());
        gameDatabase.put(gameData.gameID(), gameData);
        gameIDs.add(gameData.gameID());
    }

    /**
     * Function for clearing the GameDatabase
     */
    public void clearDB(){
        gameDatabase.clear();
        gameIDs.clear();
    }
}
