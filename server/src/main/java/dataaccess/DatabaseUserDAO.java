package dataaccess;

import dataaccess.Interfaces.UserDAO;
import dataaccess.exceptions.DataAccessException;
import dataaccess.exceptions.DatabaseAccessException;
import model.UserData;

import java.sql.SQLException;

public class DatabaseUserDAO implements UserDAO {

    @Override
    public UserData getUser(String username) throws DatabaseAccessException {
        String getString = "SELECT * FROM userdatabase WHERE username = ?";

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
            throw new DatabaseAccessException("Error: Database access failed", e);
        }
    }

    @Override
    public void putUser(UserData userData) throws DatabaseAccessException {
        String insertString = "INSERT INTO userdatabase (username, password, email) VALUES (?, ?, ?)";

        try (var conn = DatabaseManager.getConnection(); var statement = conn.prepareStatement(insertString)) {
            statement.setString(1, userData.username());
            statement.setString(2, userData.password());
            statement.setString(3, userData.email());

            statement.executeUpdate();

        } catch (SQLException | DataAccessException e) {
            throw new DatabaseAccessException("Error: Database access failed", e);
        }
    }

    @Override
    public void clearDB() throws DatabaseAccessException {
        String clear_string = "TRUNCATE TABLE userdatabase";

        try (var conn = DatabaseManager.getConnection(); var statement = conn.prepareStatement(clear_string)) {
            statement.executeUpdate();

        } catch (SQLException | DataAccessException e) {
            throw new DatabaseAccessException("Error: Database access failed", e);
        }
    }

    public static void setup() {
        try (var conn = DatabaseManager.getConnection()) {
            var createTable = conn.prepareStatement("""
                    CREATE TABLE IF NOT EXISTS userdatabase (
                        username VARCHAR(50) PRIMARY KEY,
                        email VARCHAR(100) NOT NULL UNIQUE,
                        password VARCHAR(60) NOT NULL
                        );
                    """);
            createTable.executeUpdate();

        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException("Error: failed to create User Table", e);
        }
    }
}
