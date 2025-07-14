package dataaccess;

import model.AuthData;

import java.util.HashMap;

public class MemoryAuthDAO implements AuthDAO{
    private HashMap<String, AuthData> authDatabase = new HashMap<>();

    public void createAuth(AuthData authData){
        authDatabase.put(authData.getAuthToken(), authData);
    }

    public AuthData getAuth(String authToken){
        return authDatabase.get(authToken);
    }

    public boolean tokenAlreadyExists(String authToken){
        return (authDatabase.containsKey(authToken));
    }
}
