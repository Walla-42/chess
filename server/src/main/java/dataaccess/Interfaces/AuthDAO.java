package dataaccess.Interfaces;

import dataaccess.exceptions.BadRequestException;
import dataaccess.exceptions.DatabaseAccessException;
import dataaccess.exceptions.UnauthorizedAccessException;
import model.AuthData;

public interface AuthDAO {
    void addAuth(AuthData authData) throws BadRequestException, DatabaseAccessException;

    AuthData getAuth(String authToken) throws DatabaseAccessException, BadRequestException;

    boolean tokenAlreadyExists(String authToken) throws DatabaseAccessException, BadRequestException;

    void deleteAuth(String authToken) throws DatabaseAccessException, UnauthorizedAccessException, BadRequestException;

    void clearDB() throws DatabaseAccessException;
}
