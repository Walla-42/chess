package client;

import model.GamesObject;
import org.junit.jupiter.api.*;
import requests.*;
import server.Server;
import server.ServerFacade;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade serverFacade;
    private final static String testUsername = "username";
    private final static String testPassword = "password";
    private final static String testEmail = "testemail@email.com";

    private static String authToken;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);

//        String baseURL = "http://localhost:" + port;
        serverFacade = new ServerFacade(port);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @BeforeEach
    public void setupUser() {
        serverFacade.clearCall(new ClearRequestBody());
        var request = new RegisterRequestBody(testUsername, testPassword, testEmail);
        var response = serverFacade.registerCall(request);
        authToken = response.authToken();
    }

    // ---------------------------------- Register Tests ---------------------------------------------------------------

    @Test
    public void register_valid_returnsAuthToken() {
        var request = new RegisterRequestBody("newUsername", "newPassword", "newemil@email.com");
        var response = serverFacade.registerCall(request);
        assertNotNull(response.authToken());
        assertEquals("newUsername", response.username());
    }

    @Test
    public void register_duplicateUsername_throws() {
        var request = new RegisterRequestBody(testUsername, "anotherPassword", "anotheremail@email.com");
        assertThrows(RuntimeException.class, () -> serverFacade.registerCall(request));
    }

    // ------------------------------------ Login Tests ----------------------------------------------------------------

    @Test
    public void login_valid_returnsAuthToken() {
        var request = new LoginRequestBody(testUsername, testPassword);
        var response = serverFacade.loginCall(request);
        assertNotNull(response.authToken());
        assertEquals(testUsername, response.username());
    }

    @Test
    public void login_invalidPassword_throws() {
        var request = new LoginRequestBody(testUsername, "wrongPassword");
        assertThrows(RuntimeException.class, () -> serverFacade.loginCall(request));
    }

    // -------------------------------------- Clear Tests --------------------------------------------------------------

    @Test
    public void clear_database_isCleared() {
        var response = serverFacade.clearCall(new ClearRequestBody());
        assertNotNull(response);

        // Try to log in after clearing (should fail)
        var request = new LoginRequestBody(testUsername, testPassword);
        assertThrows(RuntimeException.class, () -> serverFacade.loginCall(request));
    }

    @Test
    public void clear_multipleTimes_stillSucceeds() {
        serverFacade.clearCall(new ClearRequestBody());
        var response = serverFacade.clearCall(new ClearRequestBody());
        assertNotNull(response);
    }

    // -------------------------------------- ListGames Tests ----------------------------------------------------------

    @Test
    public void listGames_validToken_returnsEmptyList() {
        var response = serverFacade.listGamesCall(authToken);
        Collection<GamesObject> games = response.games();
        assertNotNull(games);
        assertEquals(0, games.size());
    }

    @Test
    public void listGames_invalidToken_throws() {
        assertThrows(RuntimeException.class, () -> serverFacade.listGamesCall("badToken"));
    }

    // --------------------------------------- Logout Tests ------------------------------------------------------------

    @Test
    public void logout_validToken_succeeds() {
        var request = new LogoutRequestBody(testUsername);
        var response = serverFacade.logoutCall(request, authToken);
        assertNotNull(response);

        // Token should now be invalid
        assertThrows(RuntimeException.class, () -> serverFacade.listGamesCall(authToken));
    }

    @Test
    public void logout_invalidToken_throws() {
        var request = new LogoutRequestBody(testUsername);
        assertThrows(RuntimeException.class, () -> serverFacade.logoutCall(request, "fake-token"));
    }
}

