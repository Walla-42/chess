package service;

import dataaccess.interfaces.AuthDAO;
import dataaccess.interfaces.GameDAO;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDataDAO;
import dataaccess.exceptions.BadRequestException;
import dataaccess.exceptions.DatabaseAccessException;
import dataaccess.exceptions.GameTakenException;
import dataaccess.exceptions.UnauthorizedAccessException;
import model.AuthData;
import model.GameData;
import model.GamesObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import requests.CreateGameRequest;
import requests.JoinGameRequest;
import requests.ListGamesRequest;
import responses.CreateGameResponse;
import responses.ListGamesResponse;


import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

public class GameServiceTests {

    private GameService gameService;
    private GameDAO gameDAO;
    private AuthService authService;
    private AuthDAO authDAO;

    private final String validToken = "valid-token";
    private final String username = "test-user";

    @BeforeEach
    void setUp() throws Exception {
        gameDAO = new MemoryGameDataDAO();
        authDAO = new MemoryAuthDAO();

        authService = new AuthService(authDAO);
        gameService = new GameService(gameDAO, authService);

        authService.createAuth(new AuthData(validToken, username));
    }

//-------------------------------------------listGames Tests------------------------------------------------------------

    @Test
    void listGamesPositive() throws Exception {
        gameDAO.createGame("game1");
        gameDAO.createGame("game2");

        ListGamesRequest request = new ListGamesRequest(validToken);
        ListGamesResponse response = gameService.listGames(request);

        Collection<GamesObject> games = response.games();
        assertEquals(2, games.size());
    }

    @Test
    void listGamesNegativeInvalidToken() {
        ListGamesRequest request = new ListGamesRequest("invalid-token");

        assertThrows(UnauthorizedAccessException.class, () -> gameService.listGames(request));
    }

//-------------------------------------------createGame Tests-----------------------------------------------------------


    @Test
    void createGamePositive() throws Exception {
        CreateGameRequest request = new CreateGameRequest(validToken, "MyGame");
        CreateGameResponse response = gameService.createGame(request);

        assertNotNull(gameDAO.getGame(response.gameID()));
    }

    @Test
    void createGameNegativeMissingGameName() {
        CreateGameRequest request = new CreateGameRequest(validToken, null);

        assertThrows(BadRequestException.class, () -> gameService.createGame(request));
    }

    @Test
    void createGameNegativeInvalidToken() {
        CreateGameRequest request = new CreateGameRequest("invalid-token", "MyGame");

        assertThrows(UnauthorizedAccessException.class, () -> gameService.createGame(request));
    }

//-------------------------------------------joinGame Tests-------------------------------------------------------------


    @Test
    void joinGamePositiveOnePlayerWhite() throws Exception {
        int gameID = gameDAO.createGame("JoinGameTest").gameID();

        JoinGameRequest request = new JoinGameRequest(validToken, "white", gameID);
        gameService.joinGame(request);

        GameData updated = gameDAO.getGame(gameID);
        assertEquals(username, updated.whiteUserName());
    }

    @Test
    void joinGamePositiveOnePlayerBlack() throws Exception {
        int gameID = gameDAO.createGame("JoinGameTest").gameID();

        JoinGameRequest request = new JoinGameRequest(validToken, "black", gameID);
        gameService.joinGame(request);

        GameData updated = gameDAO.getGame(gameID);
        assertEquals(username, updated.blackUserName());
    }

    @Test
    void joinGamePositiveTwoPlayers() throws Exception {
        int gameID = gameDAO.createGame("TakenGame").gameID();

        JoinGameRequest firstJoin = new JoinGameRequest(validToken, "white", gameID);
        gameService.joinGame(firstJoin);

        String newToken = "newAuthToken";
        String newUsername = "newUser";
        authService.createAuth(new AuthData(newToken, newUsername));

        JoinGameRequest secondJoin = new JoinGameRequest(newToken, "black", gameID);
        gameService.joinGame(secondJoin);

        GameData gameData = gameDAO.getGame(gameID);

        assertEquals(username, gameData.whiteUserName());
        assertEquals(newUsername, gameData.blackUserName());
    }

    @Test
    void joinGameNegativeInvalidToken() throws DatabaseAccessException {
        int gameID = gameDAO.createGame("JoinGameFail").gameID();
        JoinGameRequest request = new JoinGameRequest("invalid-token", "black", gameID);

        assertThrows(UnauthorizedAccessException.class, () -> gameService.joinGame(request));
    }

    @Test
    void joinGameNegativeBadRequestMissingColor() throws DatabaseAccessException {
        int gameID = gameDAO.createGame("JoinGameNoColor").gameID();
        JoinGameRequest request = new JoinGameRequest(validToken, null, gameID);

        assertThrows(BadRequestException.class, () -> gameService.joinGame(request));
    }

    @Test
    void joinGameNegativeBadRequestMissingGameID() throws DatabaseAccessException {
        JoinGameRequest request = new JoinGameRequest(validToken, "white", null);

        assertThrows(BadRequestException.class, () -> gameService.joinGame(request));
    }

    @Test
    void joinGameNegativeColorAlreadyTaken() throws Exception {
        int gameID = gameDAO.createGame("TakenGame").gameID();

        gameService.joinGame(new JoinGameRequest(validToken, "white", gameID));

        String newToken = "newAuthToken";
        String newUsername = "newUser";
        authService.createAuth(new AuthData(newToken, newUsername));

        JoinGameRequest secondJoin = new JoinGameRequest(newToken, "white", gameID);

        assertThrows(GameTakenException.class, () -> gameService.joinGame(secondJoin));
    }

    @Test
    void joinGameNegativeInvalidColor() throws DatabaseAccessException {
        int gameID = gameDAO.createGame("BadColorGame").gameID();
        JoinGameRequest request = new JoinGameRequest(validToken, "red", gameID);

        assertThrows(BadRequestException.class, () -> gameService.joinGame(request));
    }
}
