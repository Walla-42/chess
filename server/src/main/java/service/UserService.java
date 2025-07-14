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

    /**
     * communicates with the UserDAO to add userdata to the database
     *
     * @param userData UserData object containing all user information to be stored in the database
     */
    public void createUser(UserData userData) {
        userDAO.createUser(userData);
    }

    /**
     * communicates with the UserDAO to get UserData from the database
     *
     * @param username username of user
     * @return UserData object containing all information of the user
     */
    public UserData getUser(String username){
        return userDAO.getUser(username);
    }

    /**
     * A function to check if the correct password is given by the user.
     *
     * @param inputPassword Password given by the user
     * @param userPassword Password stored by the user in the database
     * @return boolean True if password match, false otherwise
     */
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
