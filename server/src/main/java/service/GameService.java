package service;

import Responses.ListGameResponse;
import chess.ChessGame;
import dataaccess.GameDAO;
import model.GameData;

import java.util.ArrayList;
import java.util.Collection;

public class GameService {
    private final GameDAO gameDAO;

    public GameService(GameDAO gameDAO){
        this.gameDAO = gameDAO;
    }

    public Collection<ListGameResponse> listGames(){
        return gameDAO.listGames();
    }

    public GameData createGame(String gameName){
        return gameDAO.createGame(gameName);
    }

    public GameData getGame(int gameID){
        throw new RuntimeException("not yet implemented");
    }

    public GameData updateGame(String playerColor, int gameID, String userName){
        throw new RuntimeException("not yet implemented");
    }

    public boolean checkColorAvailability(ChessGame.TeamColor gameColor, ChessGame.TeamColor plaayerColor){
        throw new RuntimeException("not yet implemented");
    }

}
