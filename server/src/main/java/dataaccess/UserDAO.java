package dataaccess;

import model.UserData;

public interface UserDAO {
    String getUser(String userName);

    void createUser(UserData userData);

}
