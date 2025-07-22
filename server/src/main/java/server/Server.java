package server;

import dataaccess.*;
import dataaccess.interfaces.AuthDAO;
import dataaccess.interfaces.GameDAO;
import dataaccess.interfaces.UserDAO;
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

    private final UserDAO userDAO;
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;

    public Server() {
        if (!useMemory) {
            try {
                databaseSetup();
            } catch (DataAccessException e) {
                throw new RuntimeException(e);
            }
        }

        this.userDAO = (useMemory) ? new MemoryUserDataDAO() : new DatabaseUserDAO();
        this.authDAO = (useMemory) ? new MemoryAuthDAO() : new DatabaseAuthDAO();
        this.gameDAO = (useMemory) ? new MemoryGameDataDAO() : new DatabaseGameDAO();

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

    private void databaseSetup() throws DataAccessException {
        try {
            DatabaseManager.createDatabase();

            System.out.println("setting up User Table....");
            DatabaseUserDAO.setup();
            System.out.println("Complete UserTable setup!");
            System.out.println("setting up Auth Table....");
            DatabaseAuthDAO.setup();
            System.out.println("Complete AuthTable setup!");
            System.out.println("setting up GameData Table....");
            DatabaseGameDAO.setup();
            System.out.println("Complete GameDatTable setup!");
        } catch (DataAccessException e) {
            throw new RuntimeException("Error: failed to create database", e);
        }
    }
}
