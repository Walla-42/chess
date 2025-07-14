package dataaccess;

import model.UserData;

import java.util.HashMap;
import java.util.Objects;


public class MemoryUserDataDAO implements UserDAO{
    private HashMap<String, UserData> userDatabase = new HashMap<>();

    public UserData getUser(String username){
        return userDatabase.get(username);
    }

    public void createUser(UserData userData) {
        userDatabase.put(userData.getUsername(), userData);
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
