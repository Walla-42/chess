package handler;

import dataaccess.exceptions.BadRequestException;
import dataaccess.exceptions.GameTakenException;
import dataaccess.exceptions.UnauthorizedAccessException;
import requests.CreateGameRequest;
import requests.JoinGameRequest;
import requests.ListGamesRequest;
import responses.CreateGameResponse;
import responses.JoinGameResponse;
import com.google.gson.Gson;
import dataaccess.exceptions.DataAccessException;
import responses.ListGamesResponse;
import service.AuthService;
import service.GameService;
import spark.Request;
import spark.Response;


public class GameHandler {
    private final GameService gameService;

    public GameHandler(GameService gameService) {
        this.gameService = gameService;
    }

    public Object handleListGames(Request listGamesReq, Response listGamesResp){
        Gson gson = new Gson();
        try{
            ListGamesRequest listGamesRequest = new ListGamesRequest(listGamesReq.headers("Authorization"));
            ListGamesResponse listGamesResponse = gameService.listGames(listGamesRequest);


            listGamesResp.status(200);
            return gson.toJson(listGamesResponse);

        } catch (DataAccessException e){
            listGamesResp.status(401);
            return gson.toJson(e.toString());

        } catch (Exception e) {
            listGamesResp.status(500);
            return gson.toJson(e.toString());
        }

    }

    public Object handleCreateGame(Request createGameReq, Response createGameResp){
        Gson gson = new Gson();

        try {
            CreateGameRequest requestBody = gson.fromJson(createGameReq.body(), CreateGameRequest.class);
            CreateGameRequest createGameRequest = new CreateGameRequest(createGameReq.headers("Authorization"), requestBody.gameName());
            CreateGameResponse createGameResponse = gameService.createGame(createGameRequest);

            createGameResp.status(200);
            return gson.toJson(createGameResponse);

        } catch (BadRequestException e){
            createGameResp.status(400);
            return gson.toJson(e.toString());

        } catch (UnauthorizedAccessException e) {
            createGameResp.status(401);
            return gson.toJson(e.toString());

        } catch (Exception e){
            createGameResp.status(500);
            return gson.toJson(e.toString());
        }
    }

    public Object handleJoinGame(Request joinGameReq, Response joinGameResp){
        Gson gson = new Gson();

        try{
            JoinGameRequest joinGameRequest = gson.fromJson(joinGameReq.body(), JoinGameRequest.class);
            JoinGameResponse joinGameResponse = gameService.joinGame(joinGameRequest);

            joinGameResp.status(200);
            return gson.toJson(joinGameResponse);

        } catch (BadRequestException e) {
            joinGameResp.status(400);
            return gson.toJson(e.toString());

        } catch (UnauthorizedAccessException e){
            joinGameResp.status(401);
            return gson.toJson(e.toString());

        } catch (GameTakenException e){
            joinGameResp.status(403);
            return gson.toJson(e.toString());

        } catch (Exception e){
            joinGameResp.status(500);
            return gson.toJson(e.toString());
        }
    }
}
