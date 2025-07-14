package dataaccess;

import model.UserData;

import java.util.HashMap;


public class MemoryUserDataDAO implements UserDAO{
    private HashMap<String, UserData> userDatabase = new HashMap<>();

    public UserData getUser(String userName){
        return userDatabase.get(userName);
    }

    public void createUser(UserData userData) {
        userDatabase.put(userData.getUserName(), userData);
    }
}
