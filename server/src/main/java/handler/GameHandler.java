package handler;

import model.GameData;
import service.AuthService;
import service.GameService;
import spark.Request;
import spark.Response;

import java.util.Collection;

public class GameHandler {
    private final GameService gameService;

    public GameHandler(GameService gameService) {
        this.gameService = gameService;
    }

    public Object handleListGames(Request req, Response resp){
        throw new RuntimeException("not yet implemented");
    }

    public Object handleCreateGame(Request req, Response resp){
        throw new RuntimeException("not yet implemented");
    }

    public Object handleJoinGame(Request req, Response resp){
        throw new RuntimeException("not yet implemented");
    }
}
