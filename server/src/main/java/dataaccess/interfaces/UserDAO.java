package dataaccess.interfaces;

import dataaccess.exceptions.DatabaseAccessException;
import model.UserData;

public interface UserDAO {
    UserData getUser(String username) throws DatabaseAccessException;

    void putUser(UserData userData) throws DatabaseAccessException;

    void clearDB() throws DatabaseAccessException;
}
