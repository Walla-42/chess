package dataaccess;

import dataaccess.Interfaces.UserDAO;
import model.UserData;

public class DatabaseUserDAO implements UserDAO {

    @Override
    public UserData getUser(String username) {
        throw new RuntimeException("not yet implemented");
    }

    @Override
    public void putUser(UserData userData) {
        throw new RuntimeException("not yet implemented");
    }

    @Override
    public void clearDB() {
        throw new RuntimeException("not yet implemented");
    }
}
