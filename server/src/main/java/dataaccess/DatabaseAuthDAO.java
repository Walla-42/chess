package dataaccess;

import dataaccess.Interfaces.AuthDAO;
import dataaccess.exceptions.BadRequestException;
import dataaccess.exceptions.DataAccessException;
import dataaccess.exceptions.DatabaseAccessException;
import model.AuthData;

import java.sql.SQLException;

public class DatabaseAuthDAO implements AuthDAO {

    public DatabaseAuthDAO() {
        try (var conn = DatabaseManager.getConnection()) {
            var createTable = conn.prepareStatement(createStatement);
            createTable.executeUpdate();

        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Stores an authToken for the user in the AuthDatabase
     *
     * @param authData AuthData object containing the AuthToken and the username of the user
     */
    @Override
    public void addAuth(AuthData authData) throws BadRequestException, DatabaseAccessException {
        if (authData.authToken() == null || authData.username() == null) {
            throw new BadRequestException("Error: missing authToken or username");
        }

        String insertString = "INSERT INTO AuthData (authToken, username) VALUES (?, ?)";

        try (var conn = DatabaseManager.getConnection(); var statement = conn.prepareStatement(insertString)) {
            statement.setString(1, authData.authToken());
            statement.setString(2, authData.username());

            statement.executeUpdate();

        } catch (SQLException | DataAccessException e) {
            throw new DatabaseAccessException(e.getMessage());
        }
    }

    @Override
    public void deleteAuth(String authToken) throws DatabaseAccessException {
        String deleteString = "DELETE FROM AuthData WHERE auth_token EQUALS ?";

        try (var conn = DatabaseManager.getConnection(); var statement = conn.prepareStatement(deleteString)) {
            statement.setString(1, authToken);
            statement.executeUpdate();

        } catch (SQLException | DataAccessException e) {
            throw new DatabaseAccessException(e.getMessage());
        }
    }

    @Override
    public AuthData getAuth(String authToken) throws DatabaseAccessException {
        String getString = "SELECT FROM authData WHERE auth_token EQUALS ?";

        try (var conn = DatabaseManager.getConnection(); var statement = conn.prepareStatement(getString)) {
            statement.setString(1, authToken);
            var authQuery = statement.executeQuery();
            if (authQuery.next()) {
                return new AuthData(authToken, authQuery.getString("username"));
            }
            return null;

        } catch (SQLException | DataAccessException e) {
            throw new DatabaseAccessException(e.getMessage());
        }
    }

    @Override
    public boolean tokenAlreadyExists(String authToken) {
        throw new RuntimeException("not yet implemented");
    }


    @Override
    public void clearDB() {
        throw new RuntimeException("not yet implemented");
    }

    private final String createStatement = """
            CREATE TABLE IF NOT EXISTS AuthData (
                auth_token VARCHAR(128) PRIMARY KEY,
                username VARCHAR(50) NOT NULL,
                FOREIGN KEY (username) REFERENCES UserData(username) ON DELETE CASCADE
            );
            """;
}

