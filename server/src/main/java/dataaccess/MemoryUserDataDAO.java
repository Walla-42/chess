package dataaccess;

import model.UserData;

import java.util.HashMap;
import java.util.Objects;


public class MemoryUserDataDAO implements UserDAO{
    private final HashMap<String, UserData> userDatabase = new HashMap<>();

    /**
     * gets user information from the database using the users username
     *
     * @param username users username
     * @return UserData object obtained from the database
     */
    public UserData getUser(String username){
        return userDatabase.get(username);
    }

    /**
     * adds all the users data to the database
     *
     * @param userData Collection of all user data to be added to the database
     */
    public void createUser(UserData userData) {
        userDatabase.put(userData.getUsername(), userData);
    }

    /**
     * Function for clearing the UserDatabase
     */
    public void clearDB(){
        userDatabase.clear();
    }


    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        MemoryUserDataDAO that = (MemoryUserDataDAO) o;
        return Objects.equals(userDatabase, that.userDatabase);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(userDatabase);
    }
}
