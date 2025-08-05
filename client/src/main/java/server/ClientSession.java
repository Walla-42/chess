package server;

import chess.ChessGame;
import model.GameData;

import java.util.HashMap;
import java.util.Map;

public class ClientSession {
    private String username;
    private String authToken;
    private Map<Integer, Integer> gameLookupMap = new HashMap<>();
    private Map<Integer, GameData> gameDataMap = new HashMap<>();
    private Integer userCurrentGame = null;
    private ChessGame currGameBoard = null;
    private User_Role userRole = User_Role.NONE;

    public enum User_Role {
        PLAYER,
        OBSERVER,
        NONE
    }

    public User_Role getUserRole() {
        return userRole;
    }

    public void setUserRole(User_Role role) {
        userRole = role;
    }

    public ChessGame getGameBoard() {
        return currGameBoard;
    }

    public void updateGameBoard(ChessGame gameBoard) {
        currGameBoard = gameBoard;
    }

    public void addLookupMapValue(Integer userFacingID, Integer gameID) {
        gameLookupMap.put(userFacingID, gameID);

    }

    public void setCurrentGame(int gameID) {
        userCurrentGame = gameID;
    }

    public int getUserCurrentGame() {
        return userCurrentGame;
    }

    public void removeCurrentGame() {
        userCurrentGame = null;
        currGameBoard = null;
        userRole = User_Role.NONE;
    }

    public void addGameDataMapValue(Integer userFacingID, GameData gameData) {
        gameDataMap.put(userFacingID, gameData);
    }

    public void clearMap() {
        gameLookupMap.clear();
        gameDataMap.clear();

    }

    public Integer getGameID(Integer userFacingID) {
        if (gameLookupMap.containsKey(userFacingID)) {
            return gameLookupMap.get(userFacingID);
        } else {
            return null;
        }
    }

    public GameData getChessGame(Integer userFacingID) {
        if (gameDataMap.containsKey(userFacingID)) {
            return gameDataMap.get(userFacingID);
        } else {
            return null;
        }
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public String getUsername() {
        return this.username;
    }

    public String getAuthToken() {
        return this.authToken;
    }

    public void clearSession() {
        this.username = null;
        this.authToken = null;
    }
}
