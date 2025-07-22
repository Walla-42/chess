package dataaccess;

import dataaccess.Interfaces.UserDAO;
import dataaccess.exceptions.DataAccessException;
import dataaccess.exceptions.DatabaseAccessException;
import model.UserData;

import java.sql.SQLException;

public class DatabaseUserDAO implements UserDAO {

    public DatabaseUserDAO() {
        setup();
    }

    @Override
    public UserData getUser(String username) throws DatabaseAccessException {
        String getString = "SELECT * FROM User_Data WHERE username = ?";

        try (var conn = DatabaseManager.getConnection(); var statement = conn.prepareStatement(getString)) {
            statement.setString(1, username);
            var userQuery = statement.executeQuery();
            if (userQuery.next()) {
                String password = userQuery.getString("password");
                String email = userQuery.getString("email");
                return new UserData(username, password, email);
            }
            return null;
        } catch (SQLException | DataAccessException e) {
            throw new DatabaseAccessException("Database access failed", e);
        }
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

    private void setup() {
        try (var conn = DatabaseManager.getConnection()) {
            var createTable = conn.prepareStatement(createStatement);
            createTable.executeUpdate();

        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
