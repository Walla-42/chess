package dataaccess;

import model.UserData;

public interface UserDAO {
    UserData getUser(String userName);

    void createUser(UserData userData);

}
