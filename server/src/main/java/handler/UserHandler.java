package handler;
import Requests.LoginRequest;
import Requests.RegisterRequest;
import com.google.gson.Gson;
import dataaccess.DataAccessException;
import model.AuthData;
import model.UserData;
import service.AuthService;
import spark.Request;
import spark.Response;

import service.UserService;

import java.util.UUID;

public class UserHandler {
    private final UserService userService;
    private final AuthService authService;

    public UserHandler(UserService userService, AuthService authService){
        this.userService = userService;
        this.authService = authService;
    }

    public Object handleRegister(Request registerReq, Response registerResp) throws DataAccessException {
        Gson gson = new Gson();
        RegisterRequest request = gson.fromJson(registerReq.body(), RegisterRequest.class);
        try {
            if (userService.getUser(request.username()) == null) {

                UserData userData = new UserData(request.username(), request.password(), request.email());
                userService.createUser(userData);

                String authToken = generateAuth();

                AuthData authData = new AuthData(authToken, request.username());
                authService.createAuth(authData);

                registerResp.status(200);
                return gson.toJson(authData);

            } else {
                throw new DataAccessException("User already exists");
            }
        } catch (DataAccessException e){
            registerResp.status(403);
            return gson.toJson(e.toString());
        }
    }

    public Object handleLogin(Request loginReq, Response loginResp){
        Gson gson = new Gson();
        LoginRequest request = gson.fromJson(loginReq.body(), LoginRequest.class);
        try {
            UserData userData = userService.getUser(request.username());
            if (userData != null){
                if (userService.comparePasswords(request.password(), userData.getPassword())){
                    String authToken = generateAuth();

                    AuthData authData = new AuthData(authToken, request.username());
                    authService.createAuth(authData);

                    loginResp.status(200);
                    return gson.toJson(authData);
                } else {
                    throw new DataAccessException("Username or password are incorrect");
                }
            } else {
                throw new DataAccessException("Username or password are incorrect");
            }
        } catch (DataAccessException e){
            loginResp.status(401);
            return gson.toJson(e.toString());
        }

    }

    public Object handleLogout(Request logoutReq, Response logoutResp){
        throw new RuntimeException("not yet implemented");
    }

    private String generateAuth() {
        return UUID.randomUUID().toString();
    }
}
