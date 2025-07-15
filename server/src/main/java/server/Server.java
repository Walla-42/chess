package server;

import dataaccess.*;
import handler.ClearHandler;
import handler.GameHandler;
import handler.UserHandler;
import org.eclipse.jetty.server.Authentication;
import service.AuthService;
import service.ClearDbService;
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
        ClearDbService clearService = new ClearDbService(authDAO, gameDAO, userDAO);

        // Handler objects:
        UserHandler userHandler = new UserHandler(userService);
        GameHandler gameHandler = new GameHandler(gameService);
        ClearHandler clearHandler = new ClearHandler(clearService);

        // Register your endpoints and handle exceptions here.
        Spark.post("/user", userHandler::handleRegister); // endpoint complete
        Spark.post("/session", userHandler::handleLogin); // endpoint complete
        Spark.delete("/session", userHandler::handleLogout); // endpoint complete
        Spark.get("/game", gameHandler::handleListGames); // endpoint complete
        Spark.post("/game", gameHandler::handleCreateGame); // endpoint complete
        Spark.put("/game", gameHandler::handleJoinGame); // endpoint complete
        Spark.delete("/db", clearHandler::handleClear); // endpoint complete

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
