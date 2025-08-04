package server.websocket;


import chess.ChessMove;
import com.google.gson.Gson;
import exceptions.ResponseException;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class WebSocketFacade extends Endpoint {
    Session session;
    NotificationHandler notificationHandler;


    public WebSocketFacade(String url, NotificationHandler notificationHandler) throws ResponseException {
        try {
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/ws");
            this.notificationHandler = notificationHandler;

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            //set message handler
            this.session.addMessageHandler((MessageHandler.Whole<String>) message -> {
                ServerMessage serverMessage = null;
                if (message.contains("\"MessageType\":\"NOTIFICATION\"")) {
                    serverMessage = new Gson().fromJson(message, NotificationMessage.class);
                } else if (message.contains("\"MessageType\":\"ERROR\"")) {
                    serverMessage = new Gson().fromJson(message, ErrorMessage.class);
                } else if (message.contains("\"MessageType\":\"LOAD_GAME\"")) {
                    serverMessage = new Gson().fromJson(message, LoadGameMessage.class);
                }
                notificationHandler.notify(serverMessage);
            });
        } catch (DeploymentException | IOException | URISyntaxException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }

    private void enterGame(String authToken, int gameID) throws ResponseException {
        try {
            UserGameCommand enterGameCommand = new UserGameCommand(UserGameCommand.CommandType.CONNECT, authToken, gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(enterGameCommand));
        } catch (IOException e) {
            throw new ResponseException(500, e.getMessage());
        }
    }

    private void onMove(String authToken, int gameID, ChessMove move) {
        try {
            UserGameCommand makeMoveCommand = new MakeMoveCommand(UserGameCommand.CommandType.MAKE_MOVE, authToken, gameID, move);
            this.session.getBasicRemote().sendText(new Gson().toJson(makeMoveCommand));
        } catch (IOException e) {
            throw new ResponseException(500, e.getMessage());
        }
    }

    private void onResign(String authToken, int gameID) {
        try {
            UserGameCommand resignCommand = new UserGameCommand(UserGameCommand.CommandType.RESIGN, authToken, gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(resignCommand));
        } catch (IOException e) {
            throw new ResponseException(500, e.getMessage());
        }
    }

    private void onLeave(String authToken, int gameID) {
        try {
            UserGameCommand leaveCommand = new UserGameCommand(UserGameCommand.CommandType.LEAVE, authToken, gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(leaveCommand));

        } catch (IOException e) {
            throw new ResponseException(500, e.getMessage());
        }
    }
}
