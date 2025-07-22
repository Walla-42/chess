package dataaccess.interfaces;

import dataaccess.exceptions.BadRequestException;
import dataaccess.exceptions.DatabaseAccessException;
import dataaccess.exceptions.UnauthorizedAccessException;
import model.AuthData;

public interface AuthDAO {
    void addAuth(AuthData authData) throws BadRequestException, DatabaseAccessException;

    AuthData getAuth(String authToken) throws DatabaseAccessException;

    boolean tokenAlreadyExists(String authToken) throws DatabaseAccessException, BadRequestException;

    void deleteAuth(String authToken) throws DatabaseAccessException, UnauthorizedAccessException;

    void clearDB() throws DatabaseAccessException;
}
