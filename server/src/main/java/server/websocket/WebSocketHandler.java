package server.websocket;

import com.google.gson.Gson;
import dataaccess.exceptions.UnauthorizedAccessException;
import org.eclipse.jetty.websocket.api.RemoteEndpoint;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import responses.ErrorResponseClass;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@WebSocket
public class WebSocketHandler {
    private final ConnectionManager connection = new ConnectionManager();
    private final Map<Session, String> sessionToUsername = new ConcurrentHashMap<>();
    private final Map<Session, Integer> sessionToGame = new ConcurrentHashMap<>();


    @OnWebSocketMessage
    public void onMessage(Session session, String message) {
        try {
            UserGameCommand command = new Gson().fromJson(message, UserGameCommand.class);
            String username =

                    saveSession(command.getGameID(), session);

            switch (command.getCommandType()) {
                case CONNECT -> connect(session, username);
                case MAKE_MOVE -> makeMove(session, username);
                case LEAVE -> leaveGame(session, username);
                case RESIGN -> resign(session, username);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            sendMessage(session.getRemote(), new ErrorResponseClass("Error: " + ex.getMessage()));
        }
    }

    private String saveSession(Integer gameID, Session session) {
        throw new RuntimeException("not yet implemented");
    }

    private void connect(Session session, String username) {
        throw new RuntimeException("not yet implementd");
    }

    private void makeMove(Session session, String username) {
        throw new RuntimeException("Not yet implemented");
    }

    private void leaveGame(Session session, String username) {
        throw new RuntimeException("Not yet implemented");
    }

    private void resign(Session session, String username) {
        throw new RuntimeException("Not yet implemented");
    }

    private String getUsername(String authToken) {
        throw new RuntimeException("not yet implemented");
    }

    private void sendMessage(RemoteEndpoint remote, ErrorResponseClass error) {
        throw new RuntimeException("Not yet implemented");
    }
}
