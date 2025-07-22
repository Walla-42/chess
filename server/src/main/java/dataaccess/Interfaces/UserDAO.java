package dataaccess.Interfaces;

import dataaccess.exceptions.DatabaseAccessException;
import model.UserData;

public interface UserDAO {
    UserData getUser(String username) throws DatabaseAccessException;

    void putUser(UserData userData);

    void clearDB();
}
