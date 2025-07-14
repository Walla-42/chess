package service;

import dataaccess.UserDAO;
import model.UserData;

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

    public UserData getUser(String username){
        return userDAO.getUser(username);
    }

    // I am not sure if I want this here in this class:
    public boolean comparePasswords(String inputPassword, String userPassword){
        throw new RuntimeException("not yet implemented");
    }
}
