package service;

import dataaccess.UserDAO;
import dataaccess.exceptions.BadRequestException;
import dataaccess.exceptions.UnauthorizedAccessException;
import dataaccess.exceptions.UsernameTakenException;
import model.AuthData;
import model.UserData;
import requests.LoginRequest;
import requests.LogoutRequest;
import requests.RegisterRequest;
import responses.LoginResponse;
import responses.LogoutResponse;
import responses.RegisterResponse;

import java.util.Objects;

public class UserService {
    private final UserDAO userDAO;
    private final AuthService authService;

    public UserService(UserDAO userDAO, AuthService authService) {
        this.userDAO = userDAO;
        this.authService = authService;
    }

    /**
     * Service method for handling user registration logic.
     *
     * @param registerRequest RegisterRequest object given by the UserHandler
     * @return RegisterResponse object which holds the users username and authToken
     * @throws UsernameTakenException User already Exists
     * @throws BadRequestException    Missing Data in Request
     * @throws Exception              all other exceptions
     */
    public RegisterResponse registerUser(RegisterRequest registerRequest) throws UsernameTakenException, BadRequestException, Exception {
        if (registerRequest.username() == null || registerRequest.password() == null || registerRequest.email() == null) {
            throw new BadRequestException("Error: Missing Data in Request");
        }

        if (userDAO.getUser(registerRequest.username()) != null) {
            throw new UsernameTakenException("Error: User already exists");
        }

        UserData userData = new UserData(registerRequest.username(), registerRequest.password(), registerRequest.email());
        userDAO.createUser(userData);

        String authToken = authService.generateAuth();

        AuthData authData = new AuthData(authToken, registerRequest.username());
        authService.createAuth(authData);

        return new RegisterResponse(registerRequest.username(), authToken);
    }

    /**
     * Service method for handling the User Login logic
     *
     * @param loginRequest LoginRequest object given by the UserHandler
     * @return LoginResponse object which holds the users username and the assigned authToken
     * @throws BadRequestException         Missing Username or Password
     * @throws UnauthorizedAccessException Username or Password are incorrect
     * @throws Exception                   all other exceptions
     */
    public LoginResponse loginUser(LoginRequest loginRequest) throws BadRequestException, UnauthorizedAccessException, Exception {
        if (loginRequest.username() == null || loginRequest.password() == null) {
            throw new BadRequestException("Error: Must enter username and password");
        }

        UserData userData = userDAO.getUser(loginRequest.username());

        if (userData == null) {
            throw new UnauthorizedAccessException("Error: Username or password is incorrect");
        }

        if (!comparePasswords(loginRequest.password(), userData.password())) {
            throw new UnauthorizedAccessException("Error: Username or password is incorrect");
        }

        String authToken = authService.generateAuth();

        AuthData authData = new AuthData(authToken, loginRequest.username());
        authService.createAuth(authData);

        return new LoginResponse(loginRequest.username(), authToken);
    }

    /**
     * Service method for handling logout logic
     *
     * @param logoutRequest LogoutRequest object given by the UserHandler
     * @return LogoutResponse object
     * @throws UnauthorizedAccessException Invalid authToken sent with request
     */
    public LogoutResponse logoutUser(LogoutRequest logoutRequest) throws UnauthorizedAccessException {
        String authToken = logoutRequest.authToken();

        if (authToken == null || authService.getAuth(authToken) == null) {
            throw new UnauthorizedAccessException("Error: Invalid Auth Token");
        }

        authService.deleteAuth(authToken);

        return new LogoutResponse();
    }

    /**
     * A method to check if the correct password is given by the user.
     *
     * @param inputPassword Password given by the user
     * @param userPassword  Password stored by the user in the database
     * @return boolean True if password match, false otherwise
     */
    private boolean comparePasswords(String inputPassword, String userPassword) {
        return inputPassword.equals(userPassword);
    }
}
