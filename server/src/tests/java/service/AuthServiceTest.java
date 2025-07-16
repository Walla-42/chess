package service;

import dataaccess.AuthDAO;
import dataaccess.MemoryAuthDAO;
import model.AuthData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.AuthService;

import static org.junit.jupiter.api.Assertions.*;

public class AuthServiceTest {

    private AuthService authService;
    private AuthDAO authDAO;

    @BeforeEach
    void setUp() {
        authDAO = new MemoryAuthDAO();
        authService = new AuthService(authDAO);
    }

    @Test
    void createAuthPositive() {
        AuthData authData = new AuthData("authToken", "user");

        assertDoesNotThrow(() -> authService.createAuth(authData));
        assertEquals(authData, authDAO.getAuth("authToken"));
    }

    @Test
    void createAuthNoNullData() {
        AuthData authData = new AuthData("authToken", "user");

        assertNotNull(authData.username());
        assertNotNull(authData.authToken());
    }

    @Test
    void getAuthPositive(){
        AuthData newAuth = new AuthData("authToken", "user");
        authService.createAuth(newAuth);

        AuthData result = authService.getAuth(newAuth.authToken());
        assertEquals(result, newAuth);
    }

    @Test
    void getAuthNegativeInvalidToken() {
        AuthData result = authService.getAuth("authToken");
        assertNull(result);
    }

    @Test
    void deleteAuthPositive() {
        AuthData auth = new AuthData("authToken", "user");
        authService.createAuth(auth);

        authService.deleteAuth("authToken");

        assertNull(authService.getAuth("authToken"));
    }

    @Test
    void deleteAuthNegativeInvalidToken(){
        AuthData originalAuth = new AuthData("authToken", "user");
        authService.createAuth(originalAuth);

        authService.deleteAuth("invalidAuthToken");

        AuthData fetched = authService.getAuth("authToken");

        assertEquals(originalAuth, fetched);
    }

    @Test
    void generateAuthPositive() {
        String token = authService.generateAuth();
        assertNotNull(token);
        assertFalse(token.isEmpty());

        // Simulate using the token
        authService.createAuth(new AuthData(token, "username"));
        assertEquals("username", authService.getAuth(token).username());
    }

    @Test
    void generateAuthUniqueness() {
        // Simulate storing multiple tokens to ensure no duplicates
        for (int i = 0; i < 10000; i++) {
            String authToken = authService.generateAuth();

            assertFalse(authDAO.tokenAlreadyExists(authToken));
            authService.createAuth(new AuthData(authToken, "user" + i));
        }
    }
}
