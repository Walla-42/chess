package model;

import chess.ChessGame;

public record GameData(int gameID, String whiteUserName, String blackUserName, String gameName, ChessGame game) {
    public GameData UpdateGameID(int gameID){
        return new GameData(gameID, whiteUserName, blackUserName, gameName, game);
    }

    public GameData updateWhiteUsername(String whiteUserName){
        return new GameData(gameID, whiteUserName, blackUserName, gameName, game);
    }

    public GameData updateBlackUsername(String blackUserName){
        return new GameData(gameID, whiteUserName, blackUserName, gameName, game);
    }

    public GameData updateGameName(String gameName){
        return new GameData(gameID, whiteUserName, blackUserName, gameName, game);
    }

    public GameData updateGame(ChessGame game){
        return new GameData(gameID, whiteUserName, blackUserName, gameName, game);
    }
}
