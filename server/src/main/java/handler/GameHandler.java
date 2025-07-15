package handler;

import Requests.CreateGameRequest;
import Requests.ListGamesRequest;
import Responses.CreateGameResponse;
import Responses.ListGameResponse;
import com.google.gson.Gson;
import dataaccess.DataAccessException;
import model.GameData;
import service.AuthService;
import service.GameService;
import spark.Request;
import spark.Response;

import javax.xml.crypto.Data;
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

    public Object handleJoinGame(Request req, Response resp){
        throw new RuntimeException("not yet implemented");
    }
}
