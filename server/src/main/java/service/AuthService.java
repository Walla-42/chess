package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import model.AuthData;
import model.UserData;

public class AuthService {
    private final AuthDAO authDAO;

    public AuthService(AuthDAO authDAO){
        this.authDAO = authDAO;
    }

    /**
     * Communicates with authDAO to add all data in AuthData to the database
     *
     * @param authData An AuthData object containing an authToken and the users username
     * @throws DataAccessException Exception thrown when the authToken is invalid or doesnt exist.
     */
    public void createAuth(AuthData authData) throws DataAccessException {
        authDAO.createAuth(authData);
    }

    /**
     * Communicates with authDAO to check the database for the given authToken
     *
     * @param authToken authToken given by the users session
     * @return AuthData Object containing the authToken and the associated username
     */
    public AuthData getAuth(String authToken){
        return authDAO.getAuth(authToken);
    }

    /**
     * Function to facilitate logout of user. Communicates with AuthDAO to remove the users authorization.
     *
     * @param authToken authToken given by the users session
     */
    public void deleteAuth(String authToken){
        authDAO.deleteAuth(authToken);
    }
}
