package dataaccess;

import dataaccess.Interfaces.AuthDAO;
import dataaccess.exceptions.BadRequestException;
import dataaccess.exceptions.DataAccessException;
import dataaccess.exceptions.DatabaseAccessException;
import dataaccess.exceptions.UnauthorizedAccessException;
import model.AuthData;

import java.sql.SQLException;

public class DatabaseAuthDAO implements AuthDAO {

    /**
     * Stores an authToken for the user in the AuthDatabase
     *
     * @param authData AuthData object containing the AuthToken and the username of the user
     */
    @Override
    public void addAuth(AuthData authData) throws BadRequestException, DatabaseAccessException {
        if (authData.authToken() == null || authData.username() == null) {
            throw new BadRequestException("Error: missing auth Token or username");
        }

        String insertString = "INSERT INTO authdatabase (auth_token, username) VALUES (?, ?)";

        try (var conn = DatabaseManager.getConnection(); var statement = conn.prepareStatement(insertString)) {
            statement.setString(1, authData.authToken());
            statement.setString(2, authData.username());

            statement.executeUpdate();

        } catch (SQLException | DataAccessException e) {
            throw new DatabaseAccessException("Error: Database access failed", e);
        }
    }

    @Override
    public void deleteAuth(String authToken) throws DatabaseAccessException, UnauthorizedAccessException, BadRequestException {
        AuthData currAuth = getAuth(authToken);
        if (currAuth == null) {
            throw new UnauthorizedAccessException("Error: invalid auth Token");
        }

        String username = currAuth.username();

        String deleteString = "DELETE FROM authdatabase WHERE username = ?";

        try (var conn = DatabaseManager.getConnection(); var statement = conn.prepareStatement(deleteString)) {
            statement.setString(1, username);
            statement.executeUpdate();

        } catch (SQLException | DataAccessException e) {
            throw new DatabaseAccessException("Error: Database access failed", e);
        }
    }

    @Override
    public AuthData getAuth(String authToken) throws DatabaseAccessException, BadRequestException {
        if (authToken == null) {
            throw new BadRequestException("Error: invalid auth token");
        }
        String getString = "SELECT * FROM authdatabase WHERE auth_token = ?";

        try (var conn = DatabaseManager.getConnection(); var statement = conn.prepareStatement(getString)) {
            statement.setString(1, authToken);
            var authQuery = statement.executeQuery();

            if (authQuery.next()) {
                return new AuthData(authToken, authQuery.getString("username"));
            }
            return null;

        } catch (SQLException | DataAccessException e) {
            e.printStackTrace();
            throw new DatabaseAccessException("Error: Database access failed", e);
        }
    }

    @Override
    public boolean tokenAlreadyExists(String authToken) throws DatabaseAccessException, BadRequestException {
        return (getAuth(authToken) != null);
    }


    @Override
    public void clearDB() throws DatabaseAccessException {
        String clear_string = "DROP TABLE IF EXISTS authdatabase";

        try (var conn = DatabaseManager.getConnection(); var statement = conn.prepareStatement(clear_string)) {
            statement.executeUpdate();

        } catch (SQLException | DataAccessException e) {
            throw new DatabaseAccessException("Error: Database access failed", e);
        }
    }

    public static void setup() {
        try (var conn = DatabaseManager.getConnection()) {
            var createTable = conn.prepareStatement("""
                    CREATE TABLE IF NOT EXISTS authdatabase (
                        auth_token VARCHAR(128) PRIMARY KEY,
                        username VARCHAR(50) NOT NULL,
                        FOREIGN KEY (username) REFERENCES userdatabase(username) ON DELETE CASCADE
                    );
                    """);
            createTable.executeUpdate();

        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException("Error: failed to create Auth Table", e);
        }
    }

}

