package service;

import dataaccess.exceptions.BadRequestException;
import dataaccess.exceptions.DataAccessException;
import dataaccess.exceptions.GameTakenException;
import dataaccess.exceptions.UnauthorizedAccessException;
import model.GamesObject;
import requests.JoinGameRequest;
import requests.ListGamesRequest;
import responses.JoinGameResponse;
import responses.ListGamesResponse;
import chess.ChessGame;
import dataaccess.GameDAO;
import model.GameData;

import java.util.Collection;

public class GameService {
    private final GameDAO gameDAO;
    private final AuthService authService;

    public GameService(GameDAO gameDAO, AuthService authService){
        this.gameDAO = gameDAO;
        this.authService = authService;
    }

    public ListGamesResponse listGames(ListGamesRequest listGamesRequest) throws UnauthorizedAccessException, Exception{
        String authToken = listGamesRequest.authToken();

        if (authToken == null || authService.getAuth(authToken) == null) {
            throw new UnauthorizedAccessException("Unauthorized Access");
        }

        Collection<GamesObject> games = gameDAO.listGames();

        return new ListGamesResponse(games);

    }

    public JoinGameResponse joinGame(JoinGameRequest joinGameRequest) throws BadRequestException,
            UnauthorizedAccessException, GameTakenException, Exception {
        throw new RuntimeException("not yet implemented");
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
