package model;

import chess.ChessGame;

public record GameData(int gameID, String whiteUserName, String blackUserName, String gameName, ChessGame game) {
    public GameData updateWhiteUsername(String whiteUserName){
        return new GameData(gameID, whiteUserName, blackUserName, gameName, game);
    }

    public GameData updateBlackUsername(String blackUserName){
        return new GameData(gameID, whiteUserName, blackUserName, gameName, game);
    }
}
