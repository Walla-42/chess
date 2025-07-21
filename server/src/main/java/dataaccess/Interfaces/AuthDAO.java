package dataaccess.Interfaces;

import dataaccess.exceptions.BadRequestException;
import dataaccess.exceptions.DatabaseAccessException;
import model.AuthData;

public interface AuthDAO {
    void createAuth(AuthData authData) throws BadRequestException, DatabaseAccessException;

    AuthData getAuth(String authToken);

    boolean tokenAlreadyExists(String authToken);

    void deleteAuth(String authToken);

    void clearDB();
}
