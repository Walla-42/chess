package handler;
import spark.Request;
import spark.Response;

import service.UserService;

public class UserHandler {
    private UserService userService;

    public UserHandler(UserService userService){
        this.userService = userService;
    }

    public Object handleRegister(Request req, Response resp){
        throw new RuntimeException("not yet implemented");
    }

    public Object handleLogin(Request req, Response resp){
        throw new RuntimeException("not yet implemented");
    }

    public Object handleLogout(Request req, Response resp){
        throw new RuntimeException("not yet implemented");
    }
}
