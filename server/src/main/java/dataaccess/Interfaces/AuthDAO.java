package dataaccess.Interfaces;

import dataaccess.exceptions.BadRequestException;
import dataaccess.exceptions.DatabaseAccessException;
import model.AuthData;

public interface AuthDAO {
    void addAuth(AuthData authData) throws BadRequestException, DatabaseAccessException;

    AuthData getAuth(String authToken) throws DatabaseAccessException;

    boolean tokenAlreadyExists(String authToken);

    void deleteAuth(String authToken) throws DatabaseAccessException;

    void clearDB();
}
