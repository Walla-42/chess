package server;

import dataaccess.*;
import dataaccess.Interfaces.AuthDAO;
import dataaccess.Interfaces.GameDAO;
import dataaccess.Interfaces.UserDAO;
import handler.ClearHandler;
import handler.GameHandler;
import handler.UserHandler;
import service.AuthService;
import service.ClearDbService;
import service.GameService;
import service.UserService;
import spark.*;


public class Server {
    private final AuthDAO authDAO = new MemoryAuthDAO();
    private final UserDAO userDAO = new MemoryUserDataDAO();
    private final GameDAO gameDAO = new MemoryGameDataDAO();

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

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
        Spark.post("/user", userHandler::handleRegister);
        Spark.post("/session", userHandler::handleLogin);
        Spark.delete("/session", userHandler::handleLogout);
        Spark.get("/game", gameHandler::handleListGames);
        Spark.post("/game", gameHandler::handleCreateGame);
        Spark.put("/game", gameHandler::handleJoinGame);
        Spark.delete("/db", clearHandler::handleClear);

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
