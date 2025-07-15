package service;

import dataaccess.exceptions.BadRequestException;
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
import dataaccess.GameDAO;
import model.GameData;

import java.util.Collection;
import java.util.Objects;

import static chess.ChessGame.TeamColor.WHITE;
import static chess.ChessGame.TeamColor.BLACK;
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
            throw new UnauthorizedAccessException("Error: Unauthorized Access");
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
            throw new BadRequestException("Error:must provide a game name");
        }

        String authToken = createGameRequest.authToken();

        if (authToken == null || authService.getAuth(authToken) == null) {
            throw new UnauthorizedAccessException("Error: unauthorized Access");
        }

        String gameName = createGameRequest.gameName();

        if (gameName == null){
            throw new BadRequestException("Error: username cannot be blank");
        }

        GameData gameData = gameDAO.createGame(gameName);

        return new CreateGameResponse(gameData.gameID());
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
        if (joinGameRequest == null || joinGameRequest.gameID() == null || joinGameRequest.playerColor() == null){
            throw new BadRequestException("Error: bad request");
        }

        String authToken = joinGameRequest.authToken();
        AuthData authorization = authService.getAuth(authToken);

        if (authToken == null || authorization == null){
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

        if (requestedColor == BLACK){
            GameData updatedGameData = requestedGame.updateBlackUsername(authorization.getUsername());
            gameDAO.updateGame(updatedGameData);
        } else if (requestedColor == WHITE){
            GameData updatedGameData = requestedGame.updateWhiteUsername(authorization.getUsername());
            gameDAO.updateGame(updatedGameData);
        }

        return new JoinGameResponse();

    }

    private void checkColorAvailability(GameData requestedGame, ChessGame.TeamColor requestedColor) throws GameTakenException{
        if (requestedColor == BLACK && requestedGame.blackUserName() != null){
            throw new GameTakenException("Error: color already taken");
        }

        if (requestedColor == WHITE && requestedGame.whiteUserName() != null){
            throw new GameTakenException("Error: color already taken");
        };
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        GameService that = (GameService) o;
        return Objects.equals(gameDAO, that.gameDAO) && Objects.equals(authService, that.authService);
    }

    @Override
    public int hashCode() {
        return Objects.hash(gameDAO, authService);
    }
}
