package service;

import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDataDAO;
import dataaccess.exceptions.BadRequestException;
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
import service.AuthService;
import service.GameService;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

public class GameServiceTest {

    private GameService gameService;
    private GameDAO gameDAO;
    private AuthDAO authDAO;
    private AuthService authService;

    private final String validToken = "valid-token";
    private final String username = "test-user";

    @BeforeEach
    void setUp() {
        gameDAO = new MemoryGameDataDAO();
        authDAO = new MemoryAuthDAO();
        authService = new AuthService(authDAO);
        gameService = new GameService(gameDAO, authService);

        authService.createAuth(new AuthData(validToken, username));
    }

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

    @Test
    void joinGamePositive() throws Exception {
        int gameID = gameDAO.createGame("JoinGameTest").gameID();

        JoinGameRequest request = new JoinGameRequest(validToken, "white", gameID);
        gameService.joinGame(request);

        GameData updated = gameDAO.getGame(gameID);
        assertEquals(username, updated.whiteUserName());
    }

    @Test
    void joinGameNegativeInvalidToken() {
        int gameID = gameDAO.createGame("JoinGameFail").gameID();
        JoinGameRequest request = new JoinGameRequest("invalid-token", "black", gameID);

        assertThrows(UnauthorizedAccessException.class, () -> gameService.joinGame(request));
    }

    @Test
    void joinGameNegativeBadRequestMissingColor() {
        int gameID = gameDAO.createGame("JoinGameNoColor").gameID();
        JoinGameRequest request = new JoinGameRequest(validToken, null, gameID);

        assertThrows(BadRequestException.class, () -> gameService.joinGame(request));
    }

    @Test
    void joinGameNegativeBadRequestMissingGameID() {
        JoinGameRequest request = new JoinGameRequest(validToken, "white", null);

        assertThrows(BadRequestException.class, () -> gameService.joinGame(request));
    }

    @Test
    void joinGame_Negative_ColorAlreadyTaken() throws Exception {
        int gameID = gameDAO.createGame("TakenGame").gameID();

        gameService.joinGame(new JoinGameRequest(validToken, "white", gameID));

        String newToken = "new-user-token";
        String newUsername = "new-user";
        authService.createAuth(new AuthData(newToken, newUsername));

        JoinGameRequest secondJoin = new JoinGameRequest(newToken, "white", gameID);

        assertThrows(GameTakenException.class, () -> gameService.joinGame(secondJoin));
    }

    @Test
    void joinGame_Negative_InvalidColor() {
        int gameID = gameDAO.createGame("BadColorGame").gameID();
        JoinGameRequest request = new JoinGameRequest(validToken, "invalid-color", gameID);

        assertThrows(BadRequestException.class, () -> gameService.joinGame(request));
    }
}
