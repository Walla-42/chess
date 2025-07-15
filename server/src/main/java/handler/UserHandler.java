package handler;
import dataaccess.exceptions.BadRequestException;
import dataaccess.exceptions.UnauthorizedAccessException;
import dataaccess.exceptions.UsernameTakenException;
import requests.LoginRequest;
import requests.LogoutRequest;
import requests.RegisterRequest;
import com.google.gson.Gson;
import dataaccess.exceptions.DataAccessException;
import model.AuthData;
import model.UserData;
import responses.ErrorResponseClass;
import responses.LoginResponse;
import responses.LogoutResponse;
import responses.RegisterResponse;
import service.AuthService;
import spark.Request;
import spark.Response;

import service.UserService;

import java.util.UUID;

public class UserHandler {
    private final UserService userService;

    public UserHandler(UserService userService){
        this.userService = userService;
    }

    /**
     * Function handling register requests sent by the server. Sends request objects to UserService.
     *
     * @param registerReq a request object containing a body with all data necessary to create a UserData object
     * @param registerResp a response object containing the response status
     * @return a json object containing all the data contained in AuthData
     */
    public Object handleRegister(Request registerReq, Response registerResp) {
        Gson gson = new Gson();
        try {
            RegisterRequest registerRequest = gson.fromJson(registerReq.body(), RegisterRequest.class);
            RegisterResponse registerResponse = userService.registerUser(registerRequest);

            registerResp.status(200);
            return gson.toJson(registerResponse);

        } catch (UsernameTakenException e) {
            registerResp.status(403);
            return gson.toJson(new ErrorResponseClass(e.getMessage()));

        } catch (BadRequestException e) {
            registerResp.status(400);
            return gson.toJson(new ErrorResponseClass(e.getMessage()));

        } catch (Exception e){
            registerResp.status(500);
            return gson.toJson(new ErrorResponseClass(e.getMessage()));
        }
    }

    /**
     * A function for handling the user login requests. Creates objects for the UserService.
     *
     * @param loginReq a request object containing an empty head and a body with user information.
     * @param loginResp a response object containing the request status code.
     * @return json of authData if successful or a user error message if not.
     */
    public Object handleLogin(Request loginReq, Response loginResp){
        Gson gson = new Gson();
        try {
            LoginRequest loginRequest = gson.fromJson(loginReq.body(), LoginRequest.class);
            LoginResponse loginResponse = userService.loginUser(loginRequest);

            loginResp.status(200);
            return gson.toJson(loginResponse);

        } catch (BadRequestException e) {
            loginResp.status(400);
            return gson.toJson(new ErrorResponseClass(e.getMessage()));

        } catch (UnauthorizedAccessException e){
            loginResp.status(401);
            return gson.toJson(new ErrorResponseClass(e.getMessage()));

        } catch (Exception e) {
            loginResp.status(500);
            return gson.toJson(new ErrorResponseClass(e.getMessage()));
        }
    }

    /**
     * A function for handling the logout request and responses. Creates objects to be used by UserService.
     *
     * @param logoutReq Request object containing a header with the auth token and an empty body
     * @param logoutResp Response object containing a request status
     * @return empty object if successful, else an error message object.
     */
    public Object handleLogout(Request logoutReq, Response logoutResp){
        Gson gson = new Gson();
        try{
            LogoutRequest logoutRequest = new LogoutRequest(logoutReq.headers("Authorization"));
            LogoutResponse logoutResponse = userService.logoutUser(logoutRequest);

            logoutResp.status(200);
            return gson.toJson(logoutResponse);

        } catch (UnauthorizedAccessException e){
            logoutResp.status(401);
            return gson.toJson(new ErrorResponseClass(e.getMessage()));

        } catch (Exception e){
            logoutResp.status(500);
            return gson.toJson(new ErrorResponseClass(e.getMessage()));
        }
    }


}
