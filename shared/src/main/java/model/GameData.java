package model;

import chess.ChessGame;
import chess.ChessMove;

public class GameData {
    private final int gameID;
    private final String whiteUserName;
    private final String blackUserName;
    private final String gameName;
    private final ChessGame game;

    public GameData(int gameID, String whiteUserName, String blackUserName, String gameName, ChessGame game){
        this.gameID = gameID;
        this.whiteUserName = whiteUserName;
        this.blackUserName = blackUserName;
        this.gameName = gameName;
        this.game = game;
    }

    public int getGameID() {
        return gameID;
    }

    public String getWhiteUserName(){
        return whiteUserName;
    }

    public String getBlackUserName() {
        return blackUserName;
    }

    public String getGameName(){
        return gameName;
    }

    // is getGame overriding another method?
    public ChessGame getGame(){
        return game;
    }
}
