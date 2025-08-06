package server.websocket;

import chess.*;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dataaccess.exceptions.DatabaseAccessException;

import dataaccess.interfaces.AuthDAO;
import dataaccess.interfaces.GameDAO;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

import server.Server;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.Map;


@WebSocket
public class WebSocketHandler {
    private static final ConnectionManager connection = new ConnectionManager();
    private static AuthDAO authDAO;
    private static GameDAO gameDAO;


    public static void initialize(AuthDAO auth, GameDAO game) {
        authDAO = auth;
        gameDAO = game;
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException {
        try {
            JsonObject jsonObject = JsonParser.parseString(message).getAsJsonObject();
            String type = jsonObject.get("commandType").getAsString();

            System.out.println(jsonObject);

            UserGameCommand command;

            switch (UserGameCommand.CommandType.valueOf(type)) {
                case MAKE_MOVE -> {
                    command = new Gson().fromJson(jsonObject, MakeMoveCommand.class);
                    ChessMove userMove = ((MakeMoveCommand) command).getChessMove();
                    System.out.println(userMove);
                    handleMakeMove((MakeMoveCommand) command, session, userMove);
                }
                case CONNECT -> {
                    command = new Gson().fromJson(jsonObject, UserGameCommand.class);
                    handleConnect(command, session);
                }
                case LEAVE -> {
                    command = new Gson().fromJson(jsonObject, UserGameCommand.class);
                    handleLeave(command, session);
                }
                case RESIGN -> {
                    command = new Gson().fromJson(jsonObject, UserGameCommand.class);
                    handleResign(command, session);
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            sendErrorMessage(session, ex.getMessage());
        }
    }


    @OnWebSocketConnect
    public void onConnect(Session session) {
        System.out.println("WebSocket connected: " + session.getRemoteAddress().getAddress());
    }


    private void connect(Session session, String username, int gameID) throws IOException, DatabaseAccessException {
        // return current game board
        try {
            GameData gameData = gameDAO.getGame(gameID);
            if (gameData == null) {
                throw new DatabaseAccessException("Error: game does not exist.");
            }

            ServerMessage updatedGameData = new LoadGameMessage(gameData);
            session.getRemote().sendString(new Gson().toJson(updatedGameData));
        } catch (DatabaseAccessException e) {
            throw new IOException(e);
        }

        String playerPosition = getPlayerPosition(gameID, username);

        // add user to websocket connection and notify game participants
        connection.add(gameID, username, session);
        var message = String.format("%s has joined the game as %s", username, playerPosition);
        ServerMessage serverMessage = new NotificationMessage(message);
        connection.broadcast(username, gameID, serverMessage);
    }

    private String getPlayerPosition(int gameID, String username) throws DatabaseAccessException {
        GameData gameData = gameDAO.getGame(gameID);
        if (gameData.whiteUsername().equals(username)) {
            return "White";
        } else if (gameData.blackUsername().equals(username)) {
            return "Black";
        } else {
            return "Observer";
        }

    }

    private void makeMove(int gameID, Session session, String username, ChessMove move) throws IOException, DatabaseAccessException {
        if (move == null) {
            sendErrorMessage(session, "Error: Invalid move received.");
            return;
        }

        try {
            GameData userGame = gameDAO.getGame(gameID);
            ChessGame game = userGame.game();
            if (game.getGameState() != ChessGame.Game_State.ONGOING) {
                sendErrorMessage(session, "Error: The game has already ended. Type 'leave' to leave the game.");
                return;
            }
            ChessGame.TeamColor userColor;
            if (userGame.blackUsername().equals(username)) {
                userColor = ChessGame.TeamColor.BLACK;
            } else if (userGame.whiteUsername().equals(username)) {
                userColor = ChessGame.TeamColor.WHITE;
            } else {
                sendErrorMessage(session, "Error: You are not playing the game.");
                return;
            }

            // check that it is their turn
            if (game.getTeamTurn() != userColor) {
                sendErrorMessage(session, "Error: it is not your turn.");
                return;
            }

            // make move and update gameboard in database
            ChessPiece piece = game.getBoard().getPiece(move.getStartPosition());
            ChessPiece.PieceType pieceType = ChessPiece.PieceType.valueOf(piece.getPieceType().toString());
            String endPosition = makeClientCoordinate(move.getEndPosition());
            String nextTurn = (userColor.equals(ChessGame.TeamColor.WHITE)) ? "Black" : "White";

            game.makeMove(move);
            gameDAO.updateGame(userGame);

            // update game participant gameboards
            ServerMessage updateGame = new LoadGameMessage(userGame);
            connection.broadcast(null, gameID, updateGame);

            // notify game participants
            var message = String.format("%s moved %s to %s. It is now %s's turn.", username, pieceType, endPosition, nextTurn);
            ServerMessage serverMessage = new NotificationMessage(message);
            connection.broadcast(username, gameID, serverMessage);

            checkIfInCheck(userGame, gameID);

        } catch (DatabaseAccessException | InvalidMoveException e) {
            sendErrorMessage(session, e.getMessage());
        }


    }

    private void checkIfInCheck(GameData gameData, int gameID) throws IOException, DatabaseAccessException {
        ChessGame game = gameData.game();
        String teamTurn = (game.getTeamTurn().equals(ChessGame.TeamColor.WHITE) ? "White" : "Black");
        String opponentTurn = (game.getTeamTurn().equals(ChessGame.TeamColor.WHITE) ? "Black" : "White");

        if (game.isInCheckmate(game.getTeamTurn())) {
            String checkmateMessage = String.format("%s is in checkmate. Game Over. %s won the game!", teamTurn, opponentTurn);
            ServerMessage checkmate = new NotificationMessage(checkmateMessage);
            connection.broadcast(null, gameID, checkmate);
            game.setGameState((opponentTurn.equals("White") ? ChessGame.Game_State.WHITE_WON : ChessGame.Game_State.BLACK_WON));
            gameDAO.updateGame(gameData);

        } else if (game.isInCheck(game.getTeamTurn())) {
            String checkMessage = String.format("%s is in check.", teamTurn);
            ServerMessage check = new NotificationMessage(checkMessage);
            connection.broadcast(null, gameID, check);
        }
    }

    private String makeClientCoordinate(ChessPosition endPosition) {
        Map<Integer, String> intToNumber = Map.of(
                1, "a",
                2, "b",
                3, "c",
                4, "d",
                5, "e",
                6, "f",
                7, "g",
                8, "h"
        );
        int row = endPosition.getRow();
        int col = endPosition.getColumn();
        String colChar = intToNumber.get(col);
        return colChar + row;
    }

    private void leaveGame(int gameID, Session session, String username) throws IOException {
        try {
            GameData currentGame = gameDAO.getGame(gameID);
            GameData updatedGame;
            if (currentGame.blackUsername() != null && currentGame.blackUsername().equals(username)) {
                String blackUsername = null;
                updatedGame = new GameData(gameID, currentGame.whiteUsername(), blackUsername, currentGame.gameName(), currentGame.game());
                updatedGame.game().setGameState(currentGame.game().getGameState());
                gameDAO.updateGame(updatedGame);
            } else if (currentGame.whiteUsername() != null && currentGame.whiteUsername().equals(username)) {
                String whiteUsername = null;
                updatedGame = new GameData(gameID, whiteUsername, currentGame.blackUsername(), currentGame.gameName(), currentGame.game());
                updatedGame.game().setGameState(currentGame.game().getGameState());
                gameDAO.updateGame(updatedGame);
            }
            connection.remove(username);
            var message = String.format("%s has left the game", username);
            ServerMessage serverMessage = new NotificationMessage(message);
            connection.broadcast(username, gameID, serverMessage);
        } catch (DatabaseAccessException e) {
            sendErrorMessage(session, "Error: Unable to update game.");
        }

        // make sure to remove userCurrentGame from client session information in REPL
    }

    private void resign(int gameID, Session session, String username) throws IOException {
        try {
            GameData currentGame = gameDAO.getGame(gameID);
            ChessGame.Game_State gameState = currentGame.game().getGameState();
            boolean gameOver = gameState != ChessGame.Game_State.ONGOING;

            if (gameOver) {
                var message = "Error: The game has ended. Player movement is now disabled. Type 'leave' to exit the game";
                sendErrorMessage(session, message);
                return;
            }

            if (currentGame.blackUsername() != null && currentGame.blackUsername().equals(username) && !gameOver) {
                currentGame.game().setGameState(ChessGame.Game_State.WHITE_WON);
            } else if (currentGame.whiteUsername() != null && currentGame.whiteUsername().equals(username) && !gameOver) {
                currentGame.game().setGameState(ChessGame.Game_State.BLACK_WON);
            } else {
                sendErrorMessage(session, "Error: The game has ended. Type 'leave' to exit the game.");
                return;
            }

            // update game state in server
            gameDAO.updateGame(currentGame);

            String winner = (currentGame.game().getGameState() == ChessGame.Game_State.WHITE_WON) ? "White" : "Black";

            // Notify everyone in the game (except the one resigning)
            var message = String.format("%s has resigned from the game. %s won the game!", username, winner);
            ServerMessage serverMessage = new NotificationMessage(message);
            connection.broadcast(username, gameID, serverMessage);

            // Notify the resigning user
            var sessionMessage = String.format("You have resigned. %s won the game. Type 'leave' to exit the game", winner);
            sendUserNotification(session, sessionMessage);

        } catch (DatabaseAccessException e) {
            sendErrorMessage(session, "Error: Unable to update game.");
        }
    }

    private void handleMakeMove(MakeMoveCommand cmd, Session session, ChessMove move) throws IOException, DatabaseAccessException {
        String username = authDAO.getAuth(cmd.getAuthToken()).username();
        makeMove(cmd.getGameID(), session, username, move);
    }

    private void handleConnect(UserGameCommand cmd, Session session) throws IOException, DatabaseAccessException {
        String username = authDAO.getAuth(cmd.getAuthToken()).username();
        connect(session, username, cmd.getGameID());
    }

    private void handleLeave(UserGameCommand cmd, Session session) throws IOException, DatabaseAccessException {
        String username = authDAO.getAuth(cmd.getAuthToken()).username();
        leaveGame(cmd.getGameID(), session, username);
    }

    private void handleResign(UserGameCommand cmd, Session session) throws IOException, DatabaseAccessException {
        String username = authDAO.getAuth(cmd.getAuthToken()).username();
        resign(cmd.getGameID(), session, username);
    }

    private void sendUserNotification(Session session, String notification) throws IOException {
        ServerMessage serverMessage = new NotificationMessage(notification);
        String json = new Gson().toJson(serverMessage);
        session.getRemote().sendString(json);
    }

    private void sendErrorMessage(Session session, String ErrorMessage) throws IOException {
        System.out.println("Sending error notification to session: " + session);

        ServerMessage serverMessage = new ErrorMessage(ErrorMessage);
        session.getRemote().sendString(new Gson().toJson(serverMessage));
    }

}
