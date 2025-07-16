package service;

import dataaccess.AuthDAO;
import dataaccess.MemoryAuthDAO;
import dataaccess.exceptions.BadRequestException;
import model.AuthData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AuthServiceTest {

    private AuthService authService;
    private AuthDAO authDAO;

    private final String validAuthToken = "authToken";
    private final String validUsername = "username";

    @BeforeEach
    void setUp() {
        authDAO = new MemoryAuthDAO();
        authService = new AuthService(authDAO);
    }
//--------------------------------------------------createAuth Tests----------------------------------------------------
    @Test
    void createAuthPositive() {
        AuthData authData = new AuthData(validAuthToken, validUsername);

        assertDoesNotThrow(() -> authService.createAuth(authData));
        assertEquals(authData, authDAO.getAuth(validAuthToken));
    }

    @Test
    void createAuthNegativeNullToken() {
        AuthData authData = new AuthData(null, validUsername);
        assertThrows(BadRequestException.class, () -> authService.createAuth(authData));
    }

    @Test
    void createAuthNegativeNullUsername() {
        AuthData newAuthData = new AuthData(validAuthToken, null);
        assertThrows(BadRequestException.class, () -> authService.createAuth(newAuthData));
    }

//-----------------------------------------------------getAuth Tests----------------------------------------------------

    @Test
    void getAuthPositive() throws Exception {
        AuthData newAuth = new AuthData(validAuthToken, validUsername);
        authService.createAuth(newAuth);

        // Checks that the data returned from getAuth is the same as was passed into createAuth
        AuthData result = authService.getAuth(newAuth.authToken());
        assertEquals(result, newAuth);
    }

    @Test
    void getAuthNegativeInvalidToken() throws Exception {
        AuthData newAuth = new AuthData(validAuthToken, validUsername);
        authService.createAuth(newAuth);

        // checks that nothing is fetched with an invalid authToken. Error Handling done in specific use cases.
        AuthData result = authService.getAuth("invalidAuthToken");
        assertNull(result);
    }

//--------------------------------------------------deleteAuth Tests----------------------------------------------------


    @Test
    void deleteAuthPositive() throws Exception {
        AuthData auth = new AuthData(validAuthToken, validUsername);
        authService.createAuth(auth);

        authService.deleteAuth(validAuthToken);

        assertNull(authService.getAuth(validAuthToken));
    }

    @Test
    void deleteAuthNegativeInvalidToken() throws Exception {
        AuthData originalAuth = new AuthData(validAuthToken, validUsername);
        authService.createAuth(originalAuth);

        authService.deleteAuth("invalidAuthToken");

        AuthData fetchedData = authService.getAuth(validAuthToken);
        assertEquals(originalAuth, fetchedData);
    }

//--------------------------------------------------generateAuth Tests--------------------------------------------------

    @Test
    void generateAuthPositive() throws Exception {
        String token = authService.generateAuth();
        assertNotNull(token);
        assertFalse(token.isEmpty());

        authService.createAuth(new AuthData(token, validUsername));
        assertEquals(validUsername, authService.getAuth(token).username());
    }

    @Test
    void generateAuthUniqueness() throws Exception {
        for (int i = 0; i < 1000000; i++) {
            String authToken = authService.generateAuth();

            assertFalse(authDAO.tokenAlreadyExists(authToken));
            authService.createAuth(new AuthData(authToken, validUsername + i));
        }
    }
}
