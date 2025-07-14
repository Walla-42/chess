package handler;
import Requests.LoginRequest;
import Requests.LogoutRequest;
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

    /**
     * Function handling register requests sent by the server. Sends request objects to User and Auth Service.
     *
     * @param registerReq a request object containing a body with all data necessary to create a UserData object
     * @param registerResp a response object containing the response status
     * @return a json object containing all the data contained in AuthData
     */
    public Object handleRegister(Request registerReq, Response registerResp) {
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

    /**
     * A function for handling the user login requests. Creates objects for the User and Auth Services.
     *
     * @param loginReq a request object containing an empty head and a body with user information.
     * @param loginResp a response object containing the request status code.
     * @return json of authData if successful or a user error message if not.
     */
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

    /**
     * A function for handling the logout request and responses. Creates objects to be used by User and Auth Services.
     *
     * @param logoutReq Request object containing a header with the auth token and an empty body
     * @param logoutResp Response object containing a request status
     * @return empty object if successful, else an error message object.
     */
    public Object handleLogout(Request logoutReq, Response logoutResp){
        Gson gson = new Gson();
        try{
            String authToken = logoutReq.headers("Authorization");

            if (authToken == null || authService.getAuth(authToken) == null){
                throw new DataAccessException("Invalid Auth Token");
            }

            authService.deleteAuth(authToken);
            logoutResp.status(200);
            return gson.toJson(new Object());

        } catch (DataAccessException e){
            logoutResp.status(401);
            return gson.toJson(e.toString());
        }
    }

    /**
     * A function for generating a random authToken
     *
     * @return random generated authToken
     */
    private String generateAuth() {
        return UUID.randomUUID().toString();
    }
}
