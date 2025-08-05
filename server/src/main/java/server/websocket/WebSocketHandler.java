package server.websocket;

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
    private final ConnectionManager connection = new ConnectionManager();
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
                case LEAVE -> leaveGame(session, username);
                case RESIGN -> resign(session, username);
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

    private void leaveGame(Session session, String username) {
        throw new RuntimeException("Not yet implemented");
    }

    private void resign(Session session, String username) {
        throw new RuntimeException("Not yet implemented");
    }

    private void sendMessage(RemoteEndpoint remote, ErrorResponseClass error) {
        throw new RuntimeException("Not yet implemented");
    }

    private void sendErrorMessage(Session session, String ErrorMessage) throws IOException {
        ServerMessage serverMessage = new ErrorMessage(ErrorMessage);
        session.getRemote().sendString(new Gson().toJson(serverMessage));
    }

}
