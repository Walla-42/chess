package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import dataaccess.interfaces.GameDAO;
import dataaccess.exceptions.DataAccessException;
import dataaccess.exceptions.DatabaseAccessException;
import model.GameData;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.HashSet;

public class DatabaseGameDAO implements GameDAO {

    @Override
    public Collection<GameData> listGames() throws DatabaseAccessException {
        Gson gson = new Gson();
        String getString = "SELECT gameID, white_username, black_username, game_name, chess_game FROM gamedatabase WHERE status = 'active'";

        try (var conn = DatabaseManager.getConnection(); var statement = conn.prepareStatement(getString)) {
            DatabaseManager.printTableContents("gamedatabase");
            try (var gameQuery = statement.executeQuery()) {
                HashSet<GameData> activeGames = new HashSet<>();
                while (gameQuery.next()) {
                    var gameID = gameQuery.getInt("gameID");
                    var whiteUsername = gameQuery.getString("white_username");
                    var blackUsername = gameQuery.getString("black_username");
                    var gameName = gameQuery.getString("game_name");
                    var game = gson.fromJson(gameQuery.getString("chess_game"), ChessGame.class);

                    activeGames.add(new GameData(gameID, whiteUsername, blackUsername, gameName, game));
                }
                return activeGames;
            }
        } catch (SQLException | DataAccessException e) {
            System.out.println("Error logged in listGames method of DatabaseGameDAO: " + e.getMessage());
            throw new DatabaseAccessException("Error: Database access failed", e);
        }
    }

    @Override
    public GameData createGame(String gameName) throws DatabaseAccessException {
        Gson gson = new Gson();
        ChessGame newChessGame = new ChessGame();
        String chessGameString = gson.toJson(newChessGame);
        String status = (newChessGame.getGameState() == ChessGame.Game_State.ONGOING) ? "active" : "inactive";

        String insertString = "INSERT INTO gamedatabase (white_username, black_username, game_name, chess_game, status) VALUES (?, ?, ?, ?, ?)";

        try (var conn = DatabaseManager.getConnection(); var statement = conn.prepareStatement(insertString, Statement.RETURN_GENERATED_KEYS)) {
            statement.setNull(1, java.sql.Types.VARCHAR);
            statement.setNull(2, java.sql.Types.VARCHAR);
            statement.setString(3, gameName);
            statement.setString(4, chessGameString);
            statement.setString(5, status);

            statement.executeUpdate();

            var gameID = statement.getGeneratedKeys();

            int generatedGameID = 1;
            if (gameID.next()) {
                generatedGameID = gameID.getInt(1);
            }

            return new GameData(generatedGameID, null, null, gameName, newChessGame);

        } catch (SQLException | DataAccessException e) {
            System.out.println("Error logged in createGame method of DatabaseGameDAO: " + e.getMessage());
            throw new DatabaseAccessException("Error: Database Access Failed", e);
        }
    }

    @Override
    public GameData getGame(Integer gameID) throws DatabaseAccessException {
        Gson gson = new Gson();
        String getString = "SELECT * FROM gamedatabase WHERE gameID = ?";

        try (var conn = DatabaseManager.getConnection(); var statement = conn.prepareStatement(getString)) {
            statement.setInt(1, gameID);
            var getGameQuery = statement.executeQuery();

            if (getGameQuery.next()) {
                String whiteUsername = getGameQuery.getString("white_username");
                String blackUsername = getGameQuery.getString("black_username");
                String gameName = getGameQuery.getString("game_name");
                ChessGame chessGame = gson.fromJson(getGameQuery.getString("chess_game"), ChessGame.class);


                return new GameData(gameID, whiteUsername, blackUsername, gameName, chessGame);
            }
            return null;
        } catch (SQLException | DataAccessException e) {
            System.out.println("Error logged in getGame method of DatabaseGameDAO: " + e.getMessage());
            throw new DatabaseAccessException("Error: Database Access Failed", e);
        }
    }

    @Override
    public void updateGame(GameData updatedGameData) throws DatabaseAccessException {
        Gson gson = new Gson();

        int gameId = updatedGameData.gameID();
        String whiteUsername = updatedGameData.whiteUsername();
        String blackUsername = updatedGameData.blackUsername();
        String gameName = updatedGameData.gameName();
        String chessGame = gson.toJson(updatedGameData.game());
        String status = (updatedGameData.game().getGameState() == ChessGame.Game_State.ONGOING) ? "active" : "inactive";

        String updateString = "UPDATE gamedatabase SET white_username = ?, black_username = ?, game_name = ?, chess_game = ?, status = ? WHERE gameID = ?";

        try (var conn = DatabaseManager.getConnection(); var statement = conn.prepareStatement(updateString)) {
            statement.setString(1, whiteUsername);
            statement.setString(2, blackUsername);
            statement.setString(3, gameName);
            statement.setString(4, chessGame);
            statement.setString(5, status);
            statement.setInt(6, gameId);

            int rowsAffected = statement.executeUpdate();
            if (rowsAffected == 0) {
                throw new DatabaseAccessException("Error: No game found with gameID " + gameId);
            }


        } catch (SQLException | DataAccessException e) {
            System.out.println("Error logged in updateGame method of DatabaseGameDAO: " + e.getMessage());
            throw new DatabaseAccessException("Error: Database Access Failed", e);
        }
    }


    @Override
    public void clearDB() throws DatabaseAccessException {
        String clearString = "DELETE FROM gamedatabase";

        try (var conn = DatabaseManager.getConnection(); var statement = conn.prepareStatement(clearString)) {
            statement.executeUpdate();

        } catch (SQLException | DataAccessException e) {
            System.out.println("Error logged in clearDB method of DatabaseGameDAO: " + e.getMessage());
            throw new DatabaseAccessException("Error: Database access failed", e);
        }
    }

    public static void setup() {
        try (var conn = DatabaseManager.getConnection()) {
            var createTable = conn.prepareStatement("""
                    CREATE TABLE IF NOT EXISTS gamedatabase (
                        gameID INTEGER PRIMARY KEY AUTO_INCREMENT,
                        white_username VARCHAR(50),
                        black_username VARCHAR(50),
                        game_name VARCHAR(100),
                        chess_game TEXT NOT NULL,
                        status TEXT NOT NULL,
                        FOREIGN KEY (white_username) REFERENCES userdatabase(username) ON DELETE SET NULL,
                        FOREIGN KEY (black_username) REFERENCES userdatabase(username) ON DELETE SET NULL
                        );
                    """);
            createTable.executeUpdate();

        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException("Error: failed to create gameData table", e);
        }
    }
}
