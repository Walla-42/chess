package handler;

import requests.CreateGameRequest;
import requests.JoinGameRequest;
import responses.CreateGameResponse;
import responses.ListGameResponse;
import com.google.gson.Gson;
import dataaccess.exceptions.DataAccessException;
import service.AuthService;
import service.GameService;
import spark.Request;
import spark.Response;

import java.util.Collection;


public class GameHandler {
    private final GameService gameService;
    private final AuthService authService;

    public GameHandler(GameService gameService, AuthService authService) {
        this.gameService = gameService;
        this.authService = authService;
    }

    public Object handleListGames(Request listGamesReq, Response listGamesResp){
        Gson gson = new Gson();
        String authToken = listGamesReq.headers("authorization");
        try{
            if (authToken == null || authService.getAuth(authToken) == null) {
                throw new DataAccessException("Unauthorized Access");
            }

            Collection<ListGameResponse> games = gameService.listGames();

            listGamesResp.status(200);
            return gson.toJson(games);

        } catch (DataAccessException e){
            listGamesResp.status(401);
            return gson.toJson(e.toString());
        }

    }

    public Object handleCreateGame(Request createGameReq, Response createGameResp){
        Gson gson = new Gson();
        CreateGameRequest request = gson.fromJson(createGameReq.body(), CreateGameRequest.class);

        try{
            if (request == null){
                throw new DataAccessException("Must provide a game name");
            }

            CreateGameResponse createGameResponse = new CreateGameResponse(gameService.createGame(request.gameName()).getGameID());

            createGameResp.status(200);
            return gson.toJson(createGameResponse);

        } catch (DataAccessException e){
            createGameResp.status(401);
            return gson.toJson(e.toString());
        }
    }

    public Object handleJoinGame(Request joinGameReq, Response joinGameResp){
        Gson gson = new Gson();
        JoinGameRequest joinGameRequest = gson.fromJson(joinGameReq.body(), JoinGameRequest.class);

        try {
            if (joinGameRequest == null) {
                throw new DataAccessException("")
            }
        } catch (DataAccessException e) {
            joinGameResp.status(403);
            return gson.toJson(e.toString());
        }
    }
}
