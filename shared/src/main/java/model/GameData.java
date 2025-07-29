package model;

import chess.ChessGame;

public record GameData(int gameID, String whiteUsername, String blackUsername, String gameName, ChessGame game) {
    public GameData updateWhiteUsername(String whiteUserName) {
        return new GameData(gameID, whiteUserName, blackUsername, gameName, game);
    }

    public GameData updateBlackUsername(String blackUserName) {
        return new GameData(gameID, whiteUsername, blackUserName, gameName, game);
    }
}
