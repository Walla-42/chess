package dataaccess;

import model.AuthData;

public class MemoryAuthDAO implements AuthDAO{
    public void createAuth(AuthData authData){
        throw new RuntimeException("not yet implemented");
    }

    public AuthData getAuth(String authToken){
        throw new RuntimeException("not yet implemented");
    }
}
