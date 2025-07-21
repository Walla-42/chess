package service;

import dataaccess.Interfaces.AuthDAO;
import dataaccess.exceptions.BadRequestException;
import dataaccess.exceptions.DatabaseAccessException;
import model.AuthData;

import java.util.UUID;

public class AuthService {
    private final AuthDAO authDAO;

    public AuthService(AuthDAO authDAO) {
        this.authDAO = authDAO;
    }

    /**
     * Service method for adding data in AuthData to the AuthDatabase. Encapsulates interactions with authDAO to prevent
     * access to AuthDatabase through user or Game Service classes.
     *
     * @param authData An AuthData object containing an authToken and the users username
     */
    public void createAuth(AuthData authData) throws BadRequestException, DatabaseAccessException {
        authDAO.addAuth(authData);
    }

    /**
     * Service method for returning an authData object from AuthDatabase. Encapsulates interaction with AuthDAO to prevent
     * access to authDataBase through User or Game Service Classes.
     *
     * @param authToken authToken given by the users session
     * @return AuthData Object containing the authToken and the associated username
     */
    public AuthData getAuth(String authToken) throws DatabaseAccessException {
        return authDAO.getAuth(authToken);
    }

    /**
     * Service method to facilitate logout of user by removal of authTokens from AuthDatabase. Encapsulates interactions
     * with authDAO to prevent access to AuthDatabase through user or Game Service classes.
     *
     * @param authToken authToken given by the users session
     */
    public void deleteAuth(String authToken) throws DatabaseAccessException {
        authDAO.deleteAuth(authToken);
    }

    /**
     * Service method for generating a random authToken. Checks that the generated token doesn't already exists.
     *
     * @return random generated authToken
     */
    public String generateAuth() {
        String authToken;
        do {
            authToken = UUID.randomUUID().toString();
        } while (authDAO.tokenAlreadyExists(authToken));
        return authToken;
    }
}
