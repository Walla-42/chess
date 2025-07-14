package dataaccess;

import model.AuthData;
import model.UserData;

public interface AuthDAO {
    void createAuth(AuthData authData) throws DataAccessException;

    AuthData getAuth(String authToken);

    boolean tokenAlreadyExists(String authToken);
}
