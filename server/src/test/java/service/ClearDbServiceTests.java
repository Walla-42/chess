package service;

import dataaccess.*;
import dataaccess.Interfaces.AuthDAO;
import dataaccess.Interfaces.GameDAO;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import requests.ClearRequest;
import responses.ClearResponse;

import static org.junit.jupiter.api.Assertions.*;

public class ClearDbServiceTests {

    private ClearDbService clearDbService;
    private AuthDAO authDAO;
    private GameDAO gameDAO;
    private MemoryUserDataDAO userDAO;

    @BeforeEach
    void setUp() throws Exception {
        authDAO = new MemoryAuthDAO();
        gameDAO = new MemoryGameDataDAO();
        userDAO = new MemoryUserDataDAO();
        clearDbService = new ClearDbService(authDAO, gameDAO, userDAO);

        authDAO.createAuth(new AuthData("authToken", "user"));
        gameDAO.createGame("newGame");
        userDAO.putUser(new UserData("user", "pass", "user@email.com"));
    }

    @Test
    void clearPositive() {
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
