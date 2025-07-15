package service;

import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import requests.ClearRequest;
import responses.ClearResponse;

public class ClearDbService {
    AuthDAO authDAO;
    GameDAO gameDAO;
    UserDAO userDAO;

    public ClearDbService(AuthDAO authDAO, GameDAO gameDAO, UserDAO userDAO){
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
        this.userDAO = userDAO;
    }

    /**
     * Method for calling the clearance of AuthDataBase, GameDataBase, and UserDataBase
     *
     * @param clearRequest ClearRequest object authorizing the clearing of the database.
     */
    public ClearResponse clear(ClearRequest clearRequest){
        authDAO.clearDB();
        gameDAO.clearDB();
        userDAO.clearDB();

        return new ClearResponse();
    }
}
