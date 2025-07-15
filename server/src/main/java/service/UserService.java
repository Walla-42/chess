package service;

import dataaccess.UserDAO;
import dataaccess.exceptions.BadRequestException;
import dataaccess.exceptions.DataAccessException;
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

    public UserService(UserDAO userDAO, AuthService authService){
        this.userDAO = userDAO;
        this.authService = authService;
    }

    /**
     * Method for handling user registration logic
     *
     * @param registerRequest RegisterRequest object given by the UerHandler
     * @return RegisterResponse object which holds the users username and authToken
     *
     * @throws UsernameTakenException
     * @throws BadRequestException
     * @throws Exception
     */
    public RegisterResponse registerUser(RegisterRequest registerRequest) throws UsernameTakenException, BadRequestException, Exception {
        if (registerRequest.username() == null || registerRequest.password() == null || registerRequest.email() == null){
            throw new BadRequestException("Missing Data in Request");
        }

        if (getUser(registerRequest.username()) != null){
            throw new UsernameTakenException("User already exists");
        }

        UserData userData = new UserData(registerRequest.username(), registerRequest.password(), registerRequest.email());
        createUser(userData);

        String authToken = authService.generateAuth();

        AuthData authData = new AuthData(authToken, registerRequest.username());
        authService.createAuth(authData);

        return new RegisterResponse(registerRequest.username(), authToken);
    }

    /**
     * Method for handling the User Login logic
     *
     * @param loginRequest LoginRequest object given by the UserHandler
     * @return LoginResponse object which holds the users username and the assigned authToken
     *
     * @throws BadRequestException
     * @throws UnauthorizedAccessException
     * @throws Exception
     */
    public LoginResponse loginUser(LoginRequest loginRequest) throws BadRequestException, UnauthorizedAccessException, Exception {
        if (loginRequest.username() == null || loginRequest.password() == null) {
            throw new BadRequestException("Must enter username and password");
        }

        UserData userData = getUser(loginRequest.username());

        if (userData == null){
            throw new UnauthorizedAccessException("Username or password is incorrect");
        }

        if (!comparePasswords(loginRequest.password(), userData.getPassword())){
            throw new UnauthorizedAccessException("Username or password is incorrect");
        }

        String authToken = authService.generateAuth();

        AuthData authData = new AuthData(authToken, loginRequest.username());
        authService.createAuth(authData);

        return new LoginResponse(loginRequest.username(), authToken);
    }

    /**
     * Method for handling logout logic
     *
     * @param logoutRequest LogoutRequest object given by the UserHandler
     * @return empty LogoutResponse object
     *
     * @throws UnauthorizedAccessException
     */
    public LogoutResponse logoutUser(LogoutRequest logoutRequest) throws UnauthorizedAccessException {
        String authToken = logoutRequest.authToken();

        if (authToken == null || authService.getAuth(authToken) == null){
            throw new UnauthorizedAccessException("Invalid Auth Token");
        }

        authService.deleteAuth(authToken);

        return new LogoutResponse();
    }

    /**
     * communicates with the UserDAO to add userdata to the database
     *
     * @param userData UserData object containing all user information to be stored in the database
     */
    public void createUser(UserData userData) {
        userDAO.createUser(userData);
    }

    /**
     * communicates with the UserDAO to get UserData from the database
     *
     * @param username username of user
     * @return UserData object containing all information of the user
     */
    public UserData getUser(String username){
        return userDAO.getUser(username);
    }

    /**
     * A function to check if the correct password is given by the user.
     *
     * @param inputPassword Password given by the user
     * @param userPassword Password stored by the user in the database
     * @return boolean True if password match, false otherwise
     */
    public boolean comparePasswords(String inputPassword, String userPassword){
        return inputPassword.equals(userPassword);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        UserService that = (UserService) o;
        return Objects.equals(userDAO, that.userDAO);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(userDAO);
    }
}
