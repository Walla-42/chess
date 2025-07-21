package dataaccess;

import dataaccess.Interfaces.UserDAO;
import model.UserData;

public class DatabaseUserDAO implements UserDAO {

    @Override
    public UserData getUser(String username) {
        throw new RuntimeException("not yet implemented");
    }

    @Override
    public void putUser(UserData userData) {
        throw new RuntimeException("not yet implemented");
    }

    @Override
    public void clearDB() {
        throw new RuntimeException("not yet implemented");
    }

    private final String createStatement = """
            CREATE TABLE IF NOT EXISTS User_Data (
                username VARCHAR(50) PRIMARY KEY,
                email VARCHAR(100) NOT NULL UNIQUE,
                password VARCHAR(60) NOT NULL
                );
            """;
}
