package dataaccess;

import dataaccess.Interfaces.AuthDAO;
import dataaccess.exceptions.BadRequestException;
import dataaccess.exceptions.DataAccessException;
import dataaccess.exceptions.DatabaseAccessException;
import model.AuthData;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DatabaseAuthDAO implements AuthDAO {

    public DatabaseAuthDAO() throws DataAccessException {
        configureDatabase();
    }

    /**
     * Stores an authToken for the user in the AuthDatabase
     *
     * @param authData AuthData object containing the AuthToken and the username of the user
     */
    @Override
    public void createAuth(AuthData authData) throws BadRequestException, DatabaseAccessException {
        if (authData.authToken() == null || authData.username() == null) {
            throw new BadRequestException("Error: missing authToken or username");
        }

        String insert_string = "INSERT INTO AuthData (authToken, username) VALUES (?, ?)";

        try (var conn = DatabaseManager.getConnection(); var statement = conn.prepareStatement(insert_string)) {
            statement.setString(1, authData.authToken());
            statement.setString(2, authData.username());

            statement.executeUpdate();

        } catch (SQLException | DataAccessException e) {
            throw new DatabaseAccessException(e.getMessage());
        }
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

    private final String createStatement = """
            CREATE TABLE IF NOT EXISTS AuthData (
                auth_token VARCHAR(128) PRIMARY KEY,
                username VARCHAR(50) NOT NULL,
                FOREIGN KEY (username) REFERENCES UserData(username) ON DELETE CASCADE
            );
            """;


    private void configureDatabase() throws DataAccessException {
        DatabaseManager.createDatabase();
        try (var conn = DatabaseManager.getConnection()) {
            PreparedStatement preparedStatement = conn.prepareStatement(createStatement);
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            throw new DataAccessException(String.format("Unable to configure database: %s", ex.getMessage()));
        }
    }
}
