package service;

import dataaccess.exceptions.BadRequestException;
import dataaccess.exceptions.DatabaseAccessException;
import dataaccess.exceptions.GameTakenException;
import dataaccess.exceptions.UnauthorizedAccessException;
import model.AuthData;
import model.GamesObject;
import requests.CreateGameRequest;
import requests.JoinGameRequest;
import requests.ListGamesRequest;
import responses.CreateGameResponse;
import responses.JoinGameResponse;
import responses.ListGamesResponse;
import chess.ChessGame;
import dataaccess.Interfaces.GameDAO;
import model.GameData;

import java.util.Collection;

import static chess.ChessGame.TeamColor.WHITE;
import static chess.ChessGame.TeamColor.BLACK;


public class GameService {
    private final GameDAO gameDAO;
    private final AuthService authService;

    public GameService(GameDAO gameDAO, AuthService authService) {
        this.gameDAO = gameDAO;
        this.authService = authService;
    }

    /**
     * Service method for handling ListGames endpoint logic.
     *
     * @param listGamesRequest ListGamesRequest Object containing the authToken of the users session
     * @return ListGamesResponse object containing an array of available games on the server
     * @throws UnauthorizedAccessException invalid authToken given
     * @throws DatabaseAccessException     unable to access database
     * @throws BadRequestException         input is invalid
     */
    public ListGamesResponse listGames(ListGamesRequest listGamesRequest) throws UnauthorizedAccessException, DatabaseAccessException, BadRequestException {
        String authToken = listGamesRequest.authToken();

        if (authToken == null || authService.getAuth(authToken) == null) {
            throw new UnauthorizedAccessException("Error: Invalid Auth Token");
        }

        Collection<GamesObject> games = gameDAO.listGames();

        return new ListGamesResponse(games);

    }

    /**
     * Service method for handling CreateGame Logic
     *
     * @param createGameRequest CreateGameRequest object containing the users session authToken and the desired gameName
     * @return CreateGameResponse object containing GameID of created game
     * @throws BadRequestException         Missing gameName
     * @throws UnauthorizedAccessException Invalid authToken provided with request
     */
    public CreateGameResponse createGame(CreateGameRequest createGameRequest) throws BadRequestException, UnauthorizedAccessException, DatabaseAccessException {
        if (createGameRequest.gameName() == null) {
            throw new BadRequestException("Error:must provide a game name");
        }

        String authToken = createGameRequest.authToken();

        if (authToken == null || authService.getAuth(authToken) == null) {
            throw new UnauthorizedAccessException("Error: unauthorized Access");
        }

        String gameName = createGameRequest.gameName();

        GameData gameData = gameDAO.createGame(gameName);

        return new CreateGameResponse(gameData.gameID());
    }

    /**
     * Service method for handling the JoinGame Logic
     *
     * @param joinGameRequest JoinGameRequest object including the authToken and the gameID
     * @return JoinGameResponse Object
     * @throws BadRequestException         gameID or playerColor not provided in request
     * @throws UnauthorizedAccessException Invalid authToken provided with request
     * @throws GameTakenException          game is already taken
     * @throws DatabaseAccessException     Unable to access database
     */
    public JoinGameResponse joinGame(JoinGameRequest joinGameRequest) throws BadRequestException,
            UnauthorizedAccessException, GameTakenException, DatabaseAccessException {

        if (joinGameRequest.gameID() == null || joinGameRequest.playerColor() == null) {
            throw new BadRequestException("Error: bad request");
        }

        String authToken = joinGameRequest.authToken();
        AuthData authorization = authService.getAuth(authToken);

        if (authToken == null || authorization == null) {
            throw new UnauthorizedAccessException("Error: unauthorized");
        }

        ChessGame.TeamColor requestedColor;
        try {
            requestedColor = ChessGame.TeamColor.valueOf(joinGameRequest.playerColor().toUpperCase());

        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Error: invalid player color");
        }

        GameData requestedGame = gameDAO.getGame(joinGameRequest.gameID());
        checkColorAvailability(requestedGame, requestedColor);

        GameData updatedGame = (requestedColor == BLACK)
                ? requestedGame.updateBlackUsername(authorization.username())
                : requestedGame.updateWhiteUsername(authorization.username());

        gameDAO.updateGame(updatedGame);

        return new JoinGameResponse();

    }

    /**
     * Helper service method for checking if the color requested is available when joining a game
     *
     * @param requestedGame  the GameData of the requested game
     * @param requestedColor the TeamColor desired by the user
     * @throws GameTakenException Color already taken by another player
     */
    private void checkColorAvailability(GameData requestedGame, ChessGame.TeamColor requestedColor) throws GameTakenException {
        if (requestedColor == BLACK && requestedGame.blackUserName() != null) {
            throw new GameTakenException("Error: color already taken");
        }

        if (requestedColor == WHITE && requestedGame.whiteUserName() != null) {
            throw new GameTakenException("Error: color already taken");
        }
        ;
    }

}
