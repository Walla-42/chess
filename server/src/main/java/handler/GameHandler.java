package handler;

import dataaccess.exceptions.*;
import requests.CreateGameRequest;
import requests.JoinGameRequest;
import requests.ListGamesRequest;
import responses.CreateGameResponse;
import responses.ErrorResponseClass;
import responses.JoinGameResponse;
import com.google.gson.Gson;
import responses.ListGamesResponse;
import service.GameService;
import spark.Request;
import spark.Response;


public class GameHandler {
    private final GameService gameService;
    private static final Gson gson = new Gson();

    public GameHandler(GameService gameService) {
        this.gameService = gameService;
    }

    /**
     * Handler Method for the ListGames endpoint. Takes in Json Requests and Responses and parses them before sending them
     * to the GameService. This function is invoked when a client sends a GET request to the `/game` endpoint.
     *
     * @param listGamesReq  the HTTP request sent by the client
     * @param listGamesResp the HTTP response object, used to set the response code
     * @return a Json string representing a listGamesResponse object on success, or an ErrorResponseClass object on failure
     */
    public Object handleListGames(Request listGamesReq, Response listGamesResp) {
        try {
            ListGamesRequest listGamesRequest = new ListGamesRequest(listGamesReq.headers("Authorization"));
            ListGamesResponse listGamesResponse = gameService.listGames(listGamesRequest);


            listGamesResp.status(200);
            return gson.toJson(listGamesResponse);

        } catch (DatabaseAccessException e) {
            listGamesResp.status(500);
            return gson.toJson(new ErrorResponseClass(e.getMessage(), 500));

        } catch (DataAccessException e) {
            listGamesResp.status(401);
            return gson.toJson(new ErrorResponseClass(e.getMessage(), 401));

        }
    }

    /**
     * Handles the HTTP request to create a new game. This function is invoked when a client sends a POST request to
     * the `/game` endpoint.
     *
     * @param createGameReq  the HTTP request sent by the client, containing the authorization header and request body
     * @param createGameResp the HTTP response object, used to set the response status code
     * @return a JSON string representing either a CreateGameResponse object on success, or an ErrorResponseClass on failure
     */
    public Object handleCreateGame(Request createGameReq, Response createGameResp) {
        try {
            CreateGameRequest requestBody = gson.fromJson(createGameReq.body(), CreateGameRequest.class);
            CreateGameRequest createGameRequest = new CreateGameRequest(createGameReq.headers("Authorization"), requestBody.gameName());
            CreateGameResponse createGameResponse = gameService.createGame(createGameRequest);

            createGameResp.status(200);
            return gson.toJson(createGameResponse);

        } catch (BadRequestException e) {
            createGameResp.status(400);
            return gson.toJson(new ErrorResponseClass(e.getMessage(), 400));

        } catch (UnauthorizedAccessException e) {
            createGameResp.status(401);
            return gson.toJson(new ErrorResponseClass(e.getMessage(), 401));

        } catch (DatabaseAccessException e) {
            createGameResp.status(500);
            return gson.toJson(new ErrorResponseClass(e.getMessage(), 500));
        }
    }

    /**
     * Handler Method for the JoinGame endpoint. Takes in Json Requests and Responses and parses them before sending them
     * to the GameService. This function is invoked when a client sends a PUT request to the `/game` endpoint.
     *
     * @param joinGameReq  the HTTP request sent by the client
     * @param joinGameResp the HTTP response object, used to set the response code
     * @return a Json string representing a joinGameResponse object on success, or an ErrorResponseClass object on failure
     */
    public Object handleJoinGame(Request joinGameReq, Response joinGameResp) {
        try {
            String authToken = joinGameReq.headers("Authorization");

            JoinGameRequest requestBody = gson.fromJson(joinGameReq.body(), JoinGameRequest.class);
            JoinGameRequest joinGameRequest = new JoinGameRequest(authToken, requestBody.playerColor(), requestBody.gameID());
            JoinGameResponse joinGameResponse = gameService.joinGame(joinGameRequest);

            joinGameResp.status(200);
            return gson.toJson(joinGameResponse);

        } catch (BadRequestException e) {
            joinGameResp.status(400);
            return gson.toJson(new ErrorResponseClass(e.getMessage(), 400));

        } catch (UnauthorizedAccessException e) {
            joinGameResp.status(401);
            return gson.toJson(new ErrorResponseClass(e.getMessage(), 401));

        } catch (GameTakenException e) {
            joinGameResp.status(403);
            return gson.toJson(new ErrorResponseClass(e.getMessage(), 403));

        } catch (DatabaseAccessException e) {
            joinGameResp.status(500);
            return gson.toJson(new ErrorResponseClass(e.getMessage(), 500));
        }
    }
}
