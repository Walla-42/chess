package handler;

import dataaccess.exceptions.BadRequestException;
import dataaccess.exceptions.DatabaseAccessException;
import dataaccess.exceptions.UnauthorizedAccessException;
import dataaccess.exceptions.UsernameTakenException;
import requests.LoginRequest;
import requests.LogoutRequest;
import requests.RegisterRequest;
import com.google.gson.Gson;

import responses.ErrorResponseClass;
import responses.LoginResponse;
import responses.LogoutResponse;
import responses.RegisterResponse;
import spark.Request;
import spark.Response;

import service.UserService;

public class UserHandler {
    private final UserService userService;

    public UserHandler(UserService userService) {
        this.userService = userService;
    }

    /**
     * Handler Method for the Register endpoint. Takes in Json Requests and Responses and parses them before sending them
     * to the UserService. This function is invoked when a client sends a POST request to the `/user` endpoint.
     *
     * @param registerReq  the HTTP request sent by the client
     * @param registerResp the HTTP response object, used to set the response code
     * @return a Json string representing a RegisterResponse object on success, or an ErrorResponseClass object on failure
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

        } catch (DatabaseAccessException e) {
            registerResp.status(500);
            return gson.toJson(new ErrorResponseClass(e.getMessage()));
        }
    }

    /**
     * Handler Method for the Login endpoint. Takes in Json Requests and Responses and parses them before sending them
     * to the UserService. This function is invoked when a client sends a POST request to the `/session` endpoint.
     *
     * @param loginReq  the HTTP request sent by the client
     * @param loginResp the HTTP response object, used to set the response code
     * @return a Json string representing a LoginResponse object on success, or an ErrorResponseClass object on failure
     */
    public Object handleLogin(Request loginReq, Response loginResp) {
        Gson gson = new Gson();
        try {
            LoginRequest loginRequest = gson.fromJson(loginReq.body(), LoginRequest.class);
            LoginResponse loginResponse = userService.loginUser(loginRequest);

            loginResp.status(200);
            return gson.toJson(loginResponse);

        } catch (BadRequestException e) {
            loginResp.status(400);
            return gson.toJson(new ErrorResponseClass(e.getMessage()));

        } catch (UnauthorizedAccessException e) {
            loginResp.status(401);
            return gson.toJson(new ErrorResponseClass(e.getMessage()));

        } catch (DatabaseAccessException e) {
            loginResp.status(500);
            return gson.toJson(new ErrorResponseClass(e.getMessage()));
        }
    }

    /**
     * Handler Method for the Logout endpoint. Takes in Json Requests and Responses and parses them before sending them
     * to the UserService. This function is invoked when a client sends a POST request to the `/user` endpoint.
     *
     * @param logoutReq  the HTTP request sent by the client
     * @param logoutResp the HTTP response object, used to set the response code
     * @return a Json string representing a LogoutResponse object on success, or an ErrorResponseClass object on failure
     */
    public Object handleLogout(Request logoutReq, Response logoutResp) {
        Gson gson = new Gson();
        try {
            LogoutRequest logoutRequest = new LogoutRequest(logoutReq.headers("Authorization"));
            LogoutResponse logoutResponse = userService.logoutUser(logoutRequest);

            logoutResp.status(200);
            return gson.toJson(logoutResponse);

        } catch (UnauthorizedAccessException e) {
            logoutResp.status(401);
            return gson.toJson(new ErrorResponseClass(e.getMessage()));

        } catch (DatabaseAccessException e) {
            logoutResp.status(500);
            return gson.toJson(new ErrorResponseClass(e.getMessage()));
        }
    }


}
