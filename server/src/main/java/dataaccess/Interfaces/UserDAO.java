package dataaccess.Interfaces;

import model.UserData;

public interface UserDAO {
    UserData getUser(String username);

    void putUser(UserData userData);

    void clearDB();
}
