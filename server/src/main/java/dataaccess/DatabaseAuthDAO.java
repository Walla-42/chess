package dataaccess;

import dataaccess.Interfaces.AuthDAO;
import dataaccess.exceptions.BadRequestException;
import model.AuthData;

public class DatabaseAuthDAO implements AuthDAO {
    @Override
    public void createAuth(AuthData authData) throws BadRequestException {
        throw new RuntimeException("not yet implemented");
    }

    @Override
    public AuthData getAuth(String authToken) {
        throw new RuntimeException("not yet implemented");
    }

    @Override
    public boolean tokenAlreadyExists(String authToken) {
        throw new RuntimeException("not yet implemented");
    }

    @Override
    public void deleteAuth(String authToken) {
        throw new RuntimeException("not yet implemented");
    }

    @Override
    public void clearDB() {
        throw new RuntimeException("not yet implemented");
    }
}
