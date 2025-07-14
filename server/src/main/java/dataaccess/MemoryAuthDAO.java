package dataaccess;

import model.AuthData;

import java.util.HashMap;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        MemoryAuthDAO that = (MemoryAuthDAO) o;
        return Objects.equals(authDatabase, that.authDatabase);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(authDatabase);
    }
}
