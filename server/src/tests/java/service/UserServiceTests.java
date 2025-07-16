package service;

import dataaccess.AuthDAO;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryUserDataDAO;
import dataaccess.UserDAO;
import dataaccess.exceptions.BadRequestException;
import dataaccess.exceptions.UnauthorizedAccessException;
import dataaccess.exceptions.UsernameTakenException;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import requests.LoginRequest;
import requests.LogoutRequest;
import requests.RegisterRequest;
import responses.LoginResponse;
import responses.LogoutResponse;
import responses.RegisterResponse;


import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTests {

    private UserService userService;
    private UserDAO userDAO;
    private AuthService authService;
    private AuthDAO authDAO;

    private final String validUsername = "username";
    private final String validPassword = "password";
    private final String validEmail = "user@email.com";

    @BeforeEach
    void setUp() {
        userDAO = new MemoryUserDataDAO();
        authDAO = new MemoryAuthDAO();
        authService = new AuthService(authDAO);
        userService = new UserService(userDAO, authService);
    }

//----------------------------------------registerUser Tests------------------------------------------------------------

    @Test
    void registerUserPositive() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest(validUsername, validPassword, validEmail);
        RegisterResponse response = userService.registerUser(registerRequest);

        // check registration returns the correct user with a valid authToken
        assertEquals(validUsername, response.username());
        assertNotNull(response.authToken());

        UserData user = userDAO.getUser(validUsername);

        // check that registered user is stored correctly in database
        assertNotNull(user);
        assertEquals(validPassword, user.password());
    }

    @Test
    void registerUserNegativeUsernameTaken() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest(validUsername, validPassword, validEmail);
        userService.registerUser(registerRequest);

        RegisterRequest duplicate = new RegisterRequest(validUsername, validPassword, validEmail);
        assertThrows(UsernameTakenException.class, () -> userService.registerUser(duplicate));
    }

    @Test
    void registerUserNegativeMissingField() {
        // Register user missing username
        RegisterRequest badRequest = new RegisterRequest(null, validPassword, validEmail);
        assertThrows(BadRequestException.class, () -> userService.registerUser(badRequest));

        // Register user missing password
        RegisterRequest badRequest2 = new RegisterRequest(validUsername, null, validEmail);
        assertThrows(BadRequestException.class, () -> userService.registerUser(badRequest2));

        // Register user missing email
        RegisterRequest badRequest3 = new RegisterRequest(validUsername, validPassword, null);
        assertThrows(BadRequestException.class, () -> userService.registerUser(badRequest3));
    }

//--------------------------------------------loginUser Tests-----------------------------------------------------------


    @Test
    void loginUserPositiveMultipleAuth() throws Exception {
        RegisterRequest register = new RegisterRequest(validUsername, validPassword, validEmail);
        RegisterResponse registerResponse = userService.registerUser(register);

        // check authData returned not null and username is username that was given
        assertNotNull(registerResponse.authToken());
        assertNotNull(registerResponse.username());
        assertEquals(validUsername, registerResponse.username());

        LoginRequest loginRequest = new LoginRequest(validUsername, validPassword);
        LoginResponse response = userService.loginUser(loginRequest);

        // check that second authToken given still accesses the correct user
        assertEquals(validUsername, response.username());
        assertNotNull(response.authToken());

        // check that auth stored in database still gives the correct user
        AuthData auth = authDAO.getAuth(response.authToken());
        assertEquals(validUsername, auth.username());

        // check that original auth still gets the correct user
        AuthData auth2 = authDAO.getAuth(registerResponse.authToken());
        assertEquals(validUsername, auth2.username());
    }

    @Test
    void loginUserNegativeWrongPassword() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest(validUsername, validPassword, validEmail);
        userService.registerUser(registerRequest);

        LoginRequest loginRequest = new LoginRequest(validUsername, validEmail);

        assertThrows(UnauthorizedAccessException.class, () -> userService.loginUser(loginRequest));
    }

    @Test
    void loginUserNegativeUserDoesNotExist() {
        LoginRequest loginRequest = new LoginRequest("NonexistentUser", "InvalidPassword");

        assertThrows(UnauthorizedAccessException.class, () -> userService.loginUser(loginRequest));
    }

    @Test
    void loginUserNegativeMissingData() {
        LoginRequest badRequest = new LoginRequest(null, validPassword);
        assertThrows(BadRequestException.class, () -> userService.loginUser(badRequest));

        LoginRequest badRequest2 = new LoginRequest(validUsername, null);
        assertThrows(BadRequestException.class, () -> userService.loginUser(badRequest2));
    }

//--------------------------------------------logoutUser Tests----------------------------------------------------------

    @Test
    void logoutUserPositive() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest(validUsername, validPassword, validEmail);
        RegisterResponse regResponse = userService.registerUser(registerRequest);

        LogoutRequest logoutRequest = new LogoutRequest(regResponse.authToken());

        LogoutResponse logoutResponse = userService.logoutUser(logoutRequest);

        assertNotNull(logoutResponse);
        assertNull(authDAO.getAuth(regResponse.authToken()));
    }

    @Test
    void logoutUserNegativeInvalidToken() {
        LogoutRequest logoutRequest = new LogoutRequest("invalidToken");

        assertThrows(UnauthorizedAccessException.class, () -> userService.logoutUser(logoutRequest));
    }
}
