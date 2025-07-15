package service;

import dataaccess.exceptions.BadRequestException;
import dataaccess.exceptions.GameTakenException;
import dataaccess.exceptions.UnauthorizedAccessException;
import model.GamesObject;
import requests.CreateGameRequest;
import requests.JoinGameRequest;
import requests.ListGamesRequest;
import responses.CreateGameResponse;
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

    /**
     * Method for handling ListGames endpoint logic.
     *
     * @param listGamesRequest ListGamesRequest Object containing the authToken of the users session
     * @return ListGamesResponse object containing an array of available games on the server
     *
     * @throws UnauthorizedAccessException
     * @throws Exception
     */
    public ListGamesResponse listGames(ListGamesRequest listGamesRequest) throws UnauthorizedAccessException, Exception{
        String authToken = listGamesRequest.authToken();

        if (authToken == null || authService.getAuth(authToken) == null) {
            throw new UnauthorizedAccessException("Unauthorized Access");
        }

        Collection<GamesObject> games = gameDAO.listGames();

        return new ListGamesResponse(games);

    }

    /**
     * Method for handling CreateGame Logic
     *
     * @param createGameRequest CreateGameRequest taken from GameHandler
     * @return CreateGameResponse object containing GameID of created game
     *
     * @throws BadRequestException
     * @throws UnauthorizedAccessException
     */
    public CreateGameResponse createGame(CreateGameRequest createGameRequest) throws BadRequestException, UnauthorizedAccessException{
        if (createGameRequest == null){
            throw new BadRequestException("Must provide a game name");
        }

        String authToken = createGameRequest.authToken();

        if (authToken == null || authService.getAuth(authToken) == null) {
            throw new UnauthorizedAccessException("Unauthorized Access");
        }

        String gameName = createGameRequest.gameName();

        if (gameName == null){
            throw new BadRequestException("Username cannot be blank");
        }

        GameData gameData = gameDAO.createGame(gameName);

        return new CreateGameResponse(gameData.getGameID());
    }

    /**
     * Function for handling the JoinGame Logic
     *
     * @param joinGameRequest JoinGameRequest object taken from the GameHandler
     * @return empty JoinGameResponse object
     *
     * @throws BadRequestException
     * @throws UnauthorizedAccessException
     * @throws GameTakenException
     * @throws Exception
     */
    public JoinGameResponse joinGame(JoinGameRequest joinGameRequest) throws BadRequestException,
            UnauthorizedAccessException, GameTakenException, Exception {
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
