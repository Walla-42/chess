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
        GameService gameService = new GameService(gameDAO, authService); // I dont think gameService needs access to userdb
        UserService userService = new UserService(userDAO, authService);

        // Handler objects:
        UserHandler userHandler = new UserHandler(userService, authService);
        GameHandler GameHandler = new GameHandler(gameService);

        // Register your endpoints and handle exceptions here.
        Spark.post("/user", userHandler::handleRegister);
        Spark.post("/session", userHandler::handleLogin);
        Spark.delete("/session", userHandler::handleLogout);
        Spark.get("/game", GameHandler::handleListGames);
        Spark.post("/game", GameHandler::handleCreateGame);
        Spark.put("/game", GameHandler::handleJoinGame);
//        Spark.delete("/db", ) need to figure how I want to integrate this one...


        //This line initializes the server and can be removed once you have a functioning endpoint 
        Spark.init();

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
