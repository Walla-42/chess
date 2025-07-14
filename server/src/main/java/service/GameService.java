package service;

import chess.ChessGame;
import dataaccess.GameDAO;
import model.GameData;

import java.util.ArrayList;

public class GameService {
    private final GameDAO gameDAO;
    private final AuthService authService;

    public GameService(GameDAO gameDAO, AuthService authService){
        this.gameDAO = gameDAO;
        this.authService = authService;
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

    public GameData updateGame(String playerColor, int gameID, String userName){
        throw new RuntimeException("not yet implemented");
    }

    public boolean checkColorAvailability(ChessGame.TeamColor gameColor, ChessGame.TeamColor plaayerColor){
        throw new RuntimeException("not yet implemented");
    }

}
