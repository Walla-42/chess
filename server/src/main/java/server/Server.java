package server;

import dataaccess.*;
import handler.GameHandler;
import handler.UserHandler;
import service.AuthService;
import service.GameService;
import service.UserService;
import spark.*;

import javax.xml.crypto.Data;


public class Server {
    private UserService userService;


    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // DAO objects:
        AuthDAO authDAO = new MemoryAuthDAO();
        UserDAO userDAO = new MemoryUserDataDAO();
        GameDAO gameDAO = new MemoryGameDataDAO();

        // Service Objects:
        AuthService authService = new AuthService(authDAO);
        GameService gameService = new GameService(gameDAO, authService);
        UserService userService = new UserService(userDAO, authService);

        // Handler objects:
        UserHandler userHandler = new UserHandler(userService);
        GameHandler GameHandler = new GameHandler(gameService);

        // Register your endpoints and handle exceptions here.
        Spark.post("/user", userHandler::handleRegister); // endpoint complete
        Spark.post("/session", userHandler::handleLogin); // endpoint complete
        Spark.delete("/session", userHandler::handleLogout); // endpoint complete
        Spark.get("/game", GameHandler::handleListGames); // endpoint complete
        Spark.post("/game", GameHandler::handleCreateGame); // endpoint complete
        Spark.put("/game", GameHandler::handleJoinGame);
//        Spark.delete("/db", ) need to figure how I want to integrate this one...

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
