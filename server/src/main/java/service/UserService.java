package service;

import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import model.GameData;
import model.UserData;

import java.util.ArrayList;

public class UserService {
    private final UserDAO userDAO;
    private final AuthService authService;

    public UserService(UserDAO userDAO, AuthService authService){
        this.userDAO = userDAO;
        this.authService = authService;
    }


    public void createUser(UserData userData) {
        userDAO.createUser(userData);
    }

    public UserData getUser(String userName){
        return userDAO.getUser(userName);
    }

    // I am not sure if I want this here in this class:
    public boolean comparePasswords(String inputPassword, String userPassword){
        throw new RuntimeException("not yet implemented");
    }
}
