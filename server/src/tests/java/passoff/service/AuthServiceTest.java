package passoff.service;

import dataaccess.AuthDAO;
import dataaccess.MemoryAuthDAO;
import dataaccess.exceptions.BadRequestException;
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
    void createAuth_Positive() {
        AuthData authData = new AuthData("authToken", "user1");

        assertDoesNotThrow(() -> authService.createAuth(authData));
        assertEquals(authData, authDAO.getAuth("authToken"));
    }

    @Test
    void createAuth_No_Null_Data() {
        AuthData authData = new AuthData("authToken", "user1");

        assertNotNull(authData.getUsername());
        assertNotNull(authData.getAuthToken());
    }

    @Test
    void getAuth_Positive(){
        AuthData newAuth = new AuthData("authToken", "user1");
        authService.createAuth(newAuth);

        AuthData result = authService.getAuth(newAuth.getAuthToken());
        assertEquals(result, newAuth);
    }

    @Test
    void getAuth_Negative_Invalid_Token() {
        AuthData result = authService.getAuth("nonexistent-token");
        assertNull(result);
    }

    @Test
    void deleteAuth_Positive() {
        AuthData auth = new AuthData("authToken", "user1");
        authService.createAuth(auth);

        authService.deleteAuth("authToken");

        assertNull(authService.getAuth("authToken"));
    }

    @Test
    void deleteAuth_Negative_Invalid_Token(){
        AuthData originalAuth = new AuthData("valid-token", "user1");
        authService.createAuth(originalAuth);

        authService.deleteAuth("nonexistent-token");

        AuthData fetched = authService.getAuth("valid-token");

        assertEquals(originalAuth, fetched);
    }

    @Test
    void generateAuth_Positive() {
        String token = authService.generateAuth();
        assertNotNull(token);
        assertFalse(token.isEmpty());

        // Simulate using the token
        authService.createAuth(new AuthData(token, "someone"));
        assertEquals("someone", authService.getAuth(token).getUsername());
    }

    @Test
    void generateAuth_Uniqueness() {
        // Simulate storing multiple tokens to ensure no duplicates
        for (int i = 0; i < 10000; i++) {
            String authToken = authService.generateAuth();

            assertFalse(authDAO.tokenAlreadyExists(authToken));
            authService.createAuth(new AuthData(authToken, "user" + i));
        }
    }
}
