package server.websocket;

import dataaccess.exceptions.UnauthorizedAccessException;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import responses.ErrorResponseClass;
import websocket.commands.UserGameCommand;

public class WebSocketHandler {
    @OnWebSocketMessage
    public void onMessage(Session session, String message){
        try {
            UserGameCommand command = Serializer.fromJson(message, UserGameCommand.class);
            String username = getUsername(command.getAuthString());

            saveSession(command.getGameID(), session);

            switch (command.getCommandType()) {
                case CONNECT -> connect(session, username, (ConnectCommand) comand);
                case MAKE_MOVE -> makeMove(session, username(MakeMoveCommand)command);
                case LEAVE -> leaveGame(session, username, (LeaveGameCommand) command);
                case RESIGN -> resign(session, username, (ResignCommand) command);
            }
        } catch (UnauthorizedAccessException ex) {
            sendMessage(session.getRemote(), new ErrorResponseClass("Error: unauthorized", 500));
        } catch (Exception ex){
            ex.printStackTrace();
            sendmessage(session.getRemote(), new ErrorResponseClass("Error: " + ex.getMessage()));
        }
    }
}
