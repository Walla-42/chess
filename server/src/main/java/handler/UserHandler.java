package handler;
import Requests.RegisterRequest;
import com.google.gson.Gson;
import dataaccess.DataAccessException;
import model.AuthData;
import model.UserData;
import service.AuthService;
import spark.Request;
import spark.Response;

import service.UserService;

import javax.xml.crypto.Data;
import java.security.SecureRandom;
import java.util.Base64;

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
            if (userService.getUser(request.userName()) == null) {

                UserData userData = new UserData(request.userName(), request.password(), request.email());
                userService.createUser(userData);

                String authToken = generateAuth();

                AuthData authData = new AuthData(authToken, request.userName());
                authService.creatAuth(authData);

                registerResp.status(200);
                return gson.toJson(authData);

            } else {
                throw new DataAccessException("User already exists");
            }
        } catch (DataAccessException e){
            registerResp.status(403);
            return gson.toJson(e);
        }
    }

    public Object handleLogin(Request loginReq, Response loginResp){
        throw new RuntimeException("not yet implemented");
    }

    public Object handleLogout(Request logoutReq, Response logoutResp){
        throw new RuntimeException("not yet implemented");
    }

    private String generateAuth() {
        throw new RuntimeException("not yet implemented");
    }
}
