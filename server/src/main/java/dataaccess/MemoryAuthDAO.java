package dataaccess;

import model.AuthData;

import javax.xml.crypto.Data;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;

public class MemoryAuthDAO implements AuthDAO{
    private HashMap<String, AuthData> tokenAuthDatabase = new HashMap<>();
    private HashMap<String, AuthData> userAuthDatabase = new HashMap<>();

    public void createAuth(AuthData authData) throws DataAccessException {
        if (userLoggedIn(authData.getUsername())) throw new DataAccessException("User already logged in");

        tokenAuthDatabase.put(authData.getAuthToken(), authData);
        userAuthDatabase.put(authData.getUsername(), authData);
    }

    public AuthData getAuth(String authToken){
        return tokenAuthDatabase.get(authToken);
    }

    public void deleteAuth(String authToken){
        String username = tokenAuthDatabase.get(authToken).getUsername();
        tokenAuthDatabase.remove(authToken);
        userAuthDatabase.remove(username);
    }

    public boolean userLoggedIn(String username){
        return userAuthDatabase.containsKey(username);
    }

    public boolean tokenAlreadyExists(String authToken){
        return tokenAuthDatabase.containsKey(authToken);
    }


    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        MemoryAuthDAO that = (MemoryAuthDAO) o;
        return Objects.equals(tokenAuthDatabase, that.tokenAuthDatabase) && Objects.equals(userAuthDatabase, that.userAuthDatabase);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tokenAuthDatabase, userAuthDatabase);
    }
}
