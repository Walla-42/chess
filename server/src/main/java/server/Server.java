package server;

import dataaccess.*;
import dataaccess.Interfaces.AuthDAO;
import dataaccess.Interfaces.GameDAO;
import dataaccess.Interfaces.UserDAO;
import dataaccess.exceptions.DataAccessException;
import handler.ClearHandler;
import handler.GameHandler;
import handler.UserHandler;
import service.AuthService;
import service.ClearDbService;
import service.GameService;
import service.UserService;
import spark.*;


public class Server {

    //use this to toggle in memory or Database usage;
    private final boolean useMemory = false;
    private final AuthDAO authDAO = (useMemory) ? new MemoryAuthDAO() : new DatabaseAuthDAO();
    private final UserDAO userDAO = (useMemory) ? new MemoryUserDataDAO() : new DatabaseUserDAO();
    private final GameDAO gameDAO = (useMemory) ? new MemoryGameDataDAO() : new DatabaseGameDAO();

    static {
        try {
            DatabaseManager.createDatabase();
        } catch (DataAccessException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

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
