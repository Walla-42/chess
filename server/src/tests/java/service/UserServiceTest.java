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
import service.AuthService;
import service.UserService;

import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTest {

    private UserService userService;
    private UserDAO userDAO;
    private AuthDAO authDAO;
    private AuthService authService;

    @BeforeEach
    void setUp() {
        userDAO = new MemoryUserDataDAO();
        authDAO = new MemoryAuthDAO();
        authService = new AuthService(authDAO);
        userService = new UserService(userDAO, authService);
    }

    @Test
    void registerUser_Positive() throws Exception {
        RegisterRequest request = new RegisterRequest("user", "password", "user@email.com");
        RegisterResponse response = userService.registerUser(request);

        assertEquals("user", response.username());
        assertNotNull(response.authToken());

        UserData user = userDAO.getUser("user");
        assertNotNull(user);
        assertEquals("password", user.password());
    }

    @Test
    void registerUser_Negative_UsernameTaken() throws Exception {
        RegisterRequest request = new RegisterRequest("user", "password", "user@email.com");
        userService.registerUser(request);

        RegisterRequest duplicate = new RegisterRequest("user", "password", "user@email.com");

        assertThrows(UsernameTakenException.class, () -> userService.registerUser(duplicate));
    }

    @Test
    void registerUser_Negative_MissingField() {
        RegisterRequest badRequest = new RegisterRequest(null, "password", "user@email.com");

        assertThrows(BadRequestException.class, () -> userService.registerUser(badRequest));
    }

    @Test
    void loginUser_Positive() throws Exception {
        // Register first
        RegisterRequest reg = new RegisterRequest("user", "password", "user@email.com");
        userService.registerUser(reg);

        LoginRequest loginRequest = new LoginRequest("user", "password");
        LoginResponse response = userService.loginUser(loginRequest);

        assertEquals("user", response.username());
        assertNotNull(response.authToken());

        AuthData auth = authDAO.getAuth(response.authToken());
        assertEquals("user", auth.username());
    }

    @Test
    void loginUser_Negative_WrongPassword() throws Exception {
        RegisterRequest reg = new RegisterRequest("user", "password", "user@email.com");
        userService.registerUser(reg);

        LoginRequest loginRequest = new LoginRequest("user", "InvalidPassword");

        assertThrows(UnauthorizedAccessException.class, () -> userService.loginUser(loginRequest));
    }

    @Test
    void loginUser_Negative_UserDoesNotExist() {
        LoginRequest loginRequest = new LoginRequest("NonexistentUser", "InvalidPassword");

        assertThrows(UnauthorizedAccessException.class, () -> userService.loginUser(loginRequest));
    }

    @Test
    void loginUser_Negative_MissingUsername() {
        LoginRequest badRequest = new LoginRequest(null, "password");

        assertThrows(BadRequestException.class, () -> userService.loginUser(badRequest));
    }

    @Test
    void logoutUser_Positive() throws Exception {
        RegisterRequest reg = new RegisterRequest("user", "password", "user@email.com");
        RegisterResponse regResponse = userService.registerUser(reg);

        LogoutRequest logoutRequest = new LogoutRequest(regResponse.authToken());

        LogoutResponse logoutResponse = userService.logoutUser(logoutRequest);
        assertNotNull(logoutResponse);

        assertNull(authDAO.getAuth(regResponse.authToken()));
    }

    @Test
    void logoutUser_Negative_InvalidToken() {
        LogoutRequest logoutRequest = new LogoutRequest("invalidToken");

        assertThrows(UnauthorizedAccessException.class, () -> userService.logoutUser(logoutRequest));
    }
}
