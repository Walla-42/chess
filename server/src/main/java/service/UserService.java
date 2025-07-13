package service;

import dataaccess.UserDAO;
import model.GameData;

import java.util.ArrayList;

public class UserService {
    private UserDAO userDAO;

    UserService(UserDAO userDAO){
        this.userDAO = userDAO;
    }

}
