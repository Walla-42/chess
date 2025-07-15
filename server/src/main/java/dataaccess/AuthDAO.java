package dataaccess;

import dataaccess.exceptions.DataAccessException;
import model.AuthData;

public interface AuthDAO {
    void createAuth(AuthData authData);

    AuthData getAuth(String authToken);

    boolean tokenAlreadyExists(String authToken);

    void deleteAuth(String authToken);

    void clearDB();
}
