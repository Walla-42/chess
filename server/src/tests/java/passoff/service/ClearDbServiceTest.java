package passoff.service;

import dataaccess.*;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import requests.ClearRequest;
import responses.ClearResponse;
import service.ClearDbService;

import static org.junit.jupiter.api.Assertions.*;

public class ClearDbServiceTest {

    private ClearDbService clearDbService;
    private AuthDAO authDAO;
    private GameDAO gameDAO;
    private MemoryUserDataDAO userDAO;

    @BeforeEach
    void setUp() {
        authDAO = new MemoryAuthDAO();
        gameDAO = new MemoryGameDataDAO();
        userDAO = new MemoryUserDataDAO();
        clearDbService = new ClearDbService(authDAO, gameDAO, userDAO);

        authDAO.createAuth(new AuthData("authToken", "user"));
        gameDAO.createGame("newGame");
        userDAO.createUser(new UserData("user", "pass", "user@email.com"));
    }

    @Test
    void clear_Positive() {
        assertNotNull(authDAO.getAuth("authToken"));
        assertFalse(gameDAO.listGames().isEmpty());
        assertNotNull(userDAO.getUser("user"));

        ClearResponse response = clearDbService.clear(new ClearRequest());

        assertNotNull(response);

        assertNull(authDAO.getAuth("authToken"));
        assertTrue(gameDAO.listGames().isEmpty());
        assertNull(userDAO.getUser("user"));
    }
}
