package handler;

import dataaccess.exceptions.BadRequestException;
import dataaccess.exceptions.GameTakenException;
import dataaccess.exceptions.UnauthorizedAccessException;
import requests.CreateGameRequest;
import requests.JoinGameRequest;
import requests.ListGamesRequest;
import responses.CreateGameResponse;
import responses.ErrorResponseClass;
import responses.JoinGameResponse;
import com.google.gson.Gson;
import dataaccess.exceptions.DataAccessException;
import responses.ListGamesResponse;
import service.GameService;
import spark.Request;
import spark.Response;


public class GameHandler {
    private final GameService gameService;

    public GameHandler(GameService gameService) {
        this.gameService = gameService;
    }

    /**
     * A method for processing Json requests and responses for the List Games endpoint
     *
     * @param listGamesReq Request Object containing the authToken for performing the request as well as the status
     * @param listGamesResp Response Object containing the list of games available as well as the status
     * @return
     */
    public Object handleListGames(Request listGamesReq, Response listGamesResp){
        Gson gson = new Gson();
        try{
            ListGamesRequest listGamesRequest = new ListGamesRequest(listGamesReq.headers("Authorization"));
            ListGamesResponse listGamesResponse = gameService.listGames(listGamesRequest);


            listGamesResp.status(200);
            return gson.toJson(listGamesResponse);

        } catch (DataAccessException e){
            listGamesResp.status(401);
            return gson.toJson(new ErrorResponseClass(e.getMessage()));

        } catch (Exception e) {
            listGamesResp.status(500);
            return gson.toJson(new ErrorResponseClass(e.getMessage()));
        }
    }

    /**
     * Handles the HTTP request to create a new game. This function is invoked when a client sends a POST request to the `/game` endpoint.
     *
     * It parses the request body into a CreateGameRequest object, extracts the authorization header,
     * and calls the gameService.createGame method to process the request. Depending on the outcome,
     * it returns a JSON response with either the created game information or an error message.
     *
     *
     * @param createGameReq  the HTTP request sent by the client, containing the authorization header and request body
     * @param createGameResp the HTTP response object, used to set the response status code
     * @return a JSON string representing either a CreateGameResponse object on success, or an ErrorResponseClass on failure
     */
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
            return gson.toJson(new ErrorResponseClass(e.getMessage()));

        } catch (UnauthorizedAccessException e) {
            createGameResp.status(401);
            return gson.toJson(new ErrorResponseClass(e.getMessage()));

        } catch (Exception e){
            createGameResp.status(500);
            return gson.toJson(new ErrorResponseClass(e.getMessage()));
        }
    }

    public Object handleJoinGame(Request joinGameReq, Response joinGameResp){
        Gson gson = new Gson();

        try{
            JoinGameRequest requestBody = gson.fromJson(joinGameReq.body(), JoinGameRequest.class);
            JoinGameRequest joinGameRequest = new JoinGameRequest(joinGameReq.headers("Authorization"), requestBody.playerColor(), requestBody.gameID());
            JoinGameResponse joinGameResponse = gameService.joinGame(joinGameRequest);

            joinGameResp.status(200);
            return gson.toJson(joinGameResponse);

        } catch (BadRequestException e) {
            joinGameResp.status(400);
            return gson.toJson(new ErrorResponseClass(e.getMessage()));

        } catch (UnauthorizedAccessException e){
            joinGameResp.status(401);
            return gson.toJson(new ErrorResponseClass(e.getMessage()));

        } catch (GameTakenException e){
            joinGameResp.status(403);
            return gson.toJson(new ErrorResponseClass(e.getMessage()));

        } catch (Exception e){
            joinGameResp.status(500);
            return gson.toJson(new ErrorResponseClass(e.getMessage()));
        }
    }
}
