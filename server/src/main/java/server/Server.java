package server;

import dataaccess.AuthDAO;
import dataaccess.UserDAO;
import handler.GameHandler;
import handler.UserHandler;
import service.UserService;
import spark.*;


public class Server {
    private UserService userService;


    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        AuthDAO authDAO = new AuthDAO();
        UserDAO userDAO = new UserDAO();
        // establish handler objects:
        UserHandler userHandler = new UserHandler(userService);
        GameHandler GameHandler = new GameHandler(userService);

        // Register your endpoints and handle exceptions here.
        Spark.get("/user", userHandler::handleRegister);

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
