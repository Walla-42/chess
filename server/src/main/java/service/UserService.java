package service;

import dataaccess.UserDAO;
import model.GameData;
import model.UserData;

import java.util.ArrayList;

public class UserService {
    private final UserDAO userDAO;

    UserService(UserDAO userDAO){
        this.userDAO = userDAO;
    }

    public void createUser(UserData userData){
        throw new RuntimeException("not yet implemented");
    }

    public UserData getUser(String userName){
        throw new RuntimeException("not yet implemented");
    }

    // I am not sure if I want this here in this class:
    public boolean comparePasswords(String inputPassword, String userPassword){
        throw new RuntimeException("not yet implemented");
    }
}
