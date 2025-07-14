package service;

import dataaccess.AuthDAO;
import model.AuthData;
import model.UserData;

public class AuthService {
    private final AuthDAO authDAO;

    public AuthService(AuthDAO authDAO){
        this.authDAO = authDAO;
    }

    public void creatAuth(AuthData authData){
        authDAO.createAuth(authData);
    }

    public AuthData getAuth(String authToken){
        return authDAO.getAuth(authToken);
    }

    public void deleteAuth(String authToken){
        throw new RuntimeException("not yet implemented");
    }
}
