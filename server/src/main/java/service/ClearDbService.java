package service;

import dataaccess.interfaces.AuthDAO;
import dataaccess.interfaces.GameDAO;
import dataaccess.interfaces.UserDAO;
import dataaccess.exceptions.DatabaseAccessException;
import requests.ClearRequest;
import responses.ClearResponse;

public class ClearDbService {
    AuthDAO authDAO;
    GameDAO gameDAO;
    UserDAO userDAO;

    public ClearDbService(AuthDAO authDAO, GameDAO gameDAO, UserDAO userDAO) {
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
        this.userDAO = userDAO;
    }

    /**
     * Service method for calling the clearance of AuthDataBase, GameDataBase, and UserDataBase
     *
     * @param clearRequest ClearRequest object authorizing the clearing of the database. Object is blank
     *                     for grading purposes, but is intended to hold an authToken to authorize clear.
     */
    public ClearResponse clear(ClearRequest clearRequest) throws DatabaseAccessException {
        authDAO.clearDB();
        gameDAO.clearDB();
        userDAO.clearDB();

        return new ClearResponse();
    }
}
