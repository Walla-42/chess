package service;

import dataaccess.GameDAO;
import model.GameData;

import java.util.ArrayList;

public class GameService {
    private GameDAO gameDAO;

    GameService(GameDAO gameDAO){
        this.gameDAO = gameDAO;
    }

    public ArrayList<GameData> listGames(){
        throw new RuntimeException("not yet implemented");
    }

    public GameData createGame(String gameName){
        throw new RuntimeException("not yet implemented");
    }

    public GameData getGame(int gameID){
        throw new RuntimeException("not yet implemented");
    }

}
