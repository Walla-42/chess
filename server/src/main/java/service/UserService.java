package service;

import dataaccess.UserDAO;
import model.UserData;

import java.util.Objects;

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
        return inputPassword.equals(userPassword);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        UserService that = (UserService) o;
        return Objects.equals(userDAO, that.userDAO) && Objects.equals(authService, that.authService);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userDAO, authService);
    }
}
