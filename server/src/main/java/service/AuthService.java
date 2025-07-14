package service;

import dataaccess.AuthDAO;
import model.AuthData;
import model.UserData;

public class AuthService {
    private final AuthDAO authDAO;

    public AuthService(AuthDAO authDAO){
        this.authDAO = authDAO;
    }

    public String creatAuth(UserData user){
        throw new RuntimeException("not yet implemented");
    }

    public AuthData getAuth(String authToken){
        throw new RuntimeException("not yet implemented");
    }

    public void deleteAuth(String authToken){
        throw new RuntimeException("not yet implemented");
    }
}
