package client;

import model.GameData;
import org.junit.jupiter.api.*;
import requests.*;
import server.Server;
import server.ServerFacade;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade serverFacade;
    private final static String TEST_USERNAME = "username";
    private final static String TEST_PASSWORD = "password";
    private final static String TEST_EMAIL = "testemail@email.com";

    private static String authToken;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);

        serverFacade = new ServerFacade(port);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @BeforeEach
    public void setupUser() {
        serverFacade.clearCall(new ClearRequestBody());
        var request = new RegisterRequestBody(TEST_USERNAME, TEST_PASSWORD, TEST_EMAIL);
        var response = serverFacade.registerCall(request);
        authToken = response.authToken();
    }

    // ---------------------------------- Register Tests ---------------------------------------------------------------

    @Test
    public void registerValidReturnsAuthToken() {
        var request = new RegisterRequestBody("newUsername", "newPassword", "newemil@email.com");
        var response = serverFacade.registerCall(request);
        assertNotNull(response.authToken());
        assertEquals("newUsername", response.username());
    }

    @Test
    public void registerDuplicateUsernameThrows() {
        var request = new RegisterRequestBody(TEST_USERNAME, "anotherPassword", "anotheremail@email.com");
        assertThrows(RuntimeException.class, () -> serverFacade.registerCall(request));
    }

    // ------------------------------------ Login Tests ----------------------------------------------------------------

    @Test
    public void loginValidReturnsAuthToken() {
        var request = new LoginRequestBody(TEST_USERNAME, TEST_PASSWORD);
        var response = serverFacade.loginCall(request);
        assertNotNull(response.authToken());
        assertEquals(TEST_USERNAME, response.username());
    }

    @Test
    public void loginInvalidPasswordThrows() {
        var request = new LoginRequestBody(TEST_USERNAME, "wrongPassword");
        assertThrows(RuntimeException.class, () -> serverFacade.loginCall(request));
    }

    // -------------------------------------- Clear Tests --------------------------------------------------------------

    @Test
    public void clearDatabaseIsCleared() {
        var response = serverFacade.clearCall(new ClearRequestBody());
        assertNotNull(response);

        // Try to log in after clearing (should fail)
        var request = new LoginRequestBody(TEST_USERNAME, TEST_PASSWORD);
        assertThrows(RuntimeException.class, () -> serverFacade.loginCall(request));
    }

    @Test
    public void clearMultipleTimesStillSucceeds() {
        serverFacade.clearCall(new ClearRequestBody());
        var response = serverFacade.clearCall(new ClearRequestBody());
        assertNotNull(response);
    }

    // -------------------------------------- ListGames Tests ----------------------------------------------------------

    @Test
    public void listGamesValidTokenReturnsEmptyList() {
        var response = serverFacade.listGamesCall(authToken);
        Collection<GameData> games = response.games();
        assertNotNull(games);
        assertEquals(0, games.size());
    }

    @Test
    public void listGamesInvalidTokenThrows() {
        assertThrows(RuntimeException.class, () -> serverFacade.listGamesCall("badToken"));
    }

    // --------------------------------------- Logout Tests ------------------------------------------------------------

    @Test
    public void logoutValidTokenSucceeds() {
        var request = new LogoutRequestBody();
        var response = serverFacade.logoutCall(request, authToken);
        assertNotNull(response);

        // Token should now be invalid
        assertThrows(RuntimeException.class, () -> serverFacade.listGamesCall(authToken));
    }

    @Test
    public void logoutInvalidTokenThrows() {
        var request = new LogoutRequestBody();
        assertThrows(RuntimeException.class, () -> serverFacade.logoutCall(request, "fake-token"));
    }


// --------------------------------------- Create Game Tests -----------------------------------------------------------

    @Test
    public void testCreateGameSuccess() {
        var request = new CreateGameRequestBody("Cool_Game");
        var response = serverFacade.createGameCall(request, authToken);

        assertNotNull(response);
        assertTrue(response.gameID() > 0);
    }

    @Test
    public void testCreateGameFailsWithNullAuth() {
        var request = new CreateGameRequestBody("Invalid Game");

        var exception = assertThrows(RuntimeException.class, () -> {
            serverFacade.createGameCall(request, null);
        });

        assertTrue(exception.getMessage().toLowerCase().contains("unauthorized"));
    }

// --------------------------------------- Join Game Tests -----------------------------------------------------------

    @Test
    public void testJoinGameSuccess() {
        var createRequest = new CreateGameRequestBody("Joinable Game");
        var createResponse = serverFacade.createGameCall(createRequest, authToken);

        var joinRequest = new JoinGameRequestBody("WHITE", createResponse.gameID());
        var joinResponse = serverFacade.joinGameCall(joinRequest, authToken);

        assertNotNull(joinResponse);
    }

    @Test
    public void testJoinGameFailsInvalidGameID() {
        var joinRequest = new JoinGameRequestBody("BLACK", -800);

        assertThrows(RuntimeException.class, () -> serverFacade.joinGameCall(joinRequest, authToken));

    }


}

