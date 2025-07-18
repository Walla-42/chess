package dataaccess;

import dataaccess.Interfaces.UserDAO;
import model.UserData;

import java.util.HashMap;


public class MemoryUserDataDAO implements UserDAO {
    private final HashMap<String, UserData> userDatabase = new HashMap<>();

    /**
     * gets user information from the database using the users username
     *
     * @param username users username
     * @return UserData object obtained from the database
     */
    public UserData getUser(String username) {
        return userDatabase.get(username);
    }

    /**
     * adds all the users data to the database
     *
     * @param userData Collection of all user data to be added to the database
     */
    public void putUser(UserData userData) {
        userDatabase.put(userData.username(), userData);
    }

    /**
     * Method for clearing the UserDatabase
     */
    public void clearDB() {
        userDatabase.clear();
    }
}
