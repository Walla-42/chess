package dataaccess;

import model.AuthData;
import model.UserData;

public interface AuthDAO {
    void createAuth(AuthData authData);

    AuthData getAuth(String authToken);

    boolean tokenAlreadyExists(String authToken);
}
