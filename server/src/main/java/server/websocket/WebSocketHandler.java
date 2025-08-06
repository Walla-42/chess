package server.websocket;

import chess.ChessGame;
import com.google.gson.Gson;
import dataaccess.exceptions.DatabaseAccessException;
import dataaccess.exceptions.UnauthorizedAccessException;

import dataaccess.interfaces.AuthDAO;
import dataaccess.interfaces.GameDAO;
import model.AuthData;
import model.GameData;
import org.eclipse.jetty.websocket.api.RemoteEndpoint;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import responses.ErrorResponseClass;

import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;


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
        UserGameCommand command = new Gson().fromJson(message, UserGameCommand.class);
        try {
            AuthData authData = authDAO.getAuth(command.getAuthToken());
            if (authData == null) {
                throw new UnauthorizedAccessException("Error: Invalid AuthToken");
            }

            String username = authData.username();
            int gameID = command.getGameID();

            switch (command.getCommandType()) {
                case CONNECT -> connect(session, username, gameID);
                case MAKE_MOVE -> makeMove(session, username);
                case LEAVE -> leaveGame(gameID, session, username);
                case RESIGN -> resign(gameID, session, username);
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


    private void connect(Session session, String username, int gameID) throws IOException {
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

        // add user to websocket connection and notify game participants
        connection.add(gameID, username, session);
        var message = String.format("%s has joined the game", username);
        ServerMessage serverMessage = new NotificationMessage(message);
        connection.broadcast(username, gameID, serverMessage);
    }

    private void makeMove(Session session, String username) {
        //
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

            // Send updated game state (includes resigned state) to everyone
            ServerMessage updateGame = new LoadGameMessage(currentGame);
            connection.broadcast(null, gameID, updateGame);

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
