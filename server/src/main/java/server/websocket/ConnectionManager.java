package server.websocket;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    public final ConcurrentHashMap<String, Connection> connections = new ConcurrentHashMap<>();

    public void add(int gameID, String username, Session session) {
        var connection = new Connection(gameID, username, session);
        connections.put(username, connection);
    }

    public void remove(String username) {
        connections.remove(username);
    }

    /**
     * Fetches usernames of all users connected to a specific game
     *
     * @param gameID ID of game
     * @return ArrayList of usernames connected to the gameID specified
     */
    public ArrayList<String> getConnections(int gameID) {
        ArrayList<String> gameConnections = new ArrayList<>();
        for (Connection conn : connections.values()) {
            if (conn.gameID == gameID) {
                gameConnections.add(conn.username);
            }
        }
        return gameConnections;
    }

    /**
     * broadcasts messages to all users connected to the given gameID's game
     *
     * @param excludeVisitorName username of root user
     * @param gameID             game ID of current game
     * @param message            message to be sent to all users
     * @throws IOException
     */
    public void broadcast(String excludeVisitorName, int gameID, ServerMessage message) throws IOException {
        ArrayList<String> gameConnections = getConnections(gameID);
        var removeList = new ArrayList<Connection>();
        for (String user : gameConnections) {
            Connection conn = connections.get(user);
            if (conn.session.isOpen()) {
                if (!conn.username.equals(excludeVisitorName)) {
                    conn.send(new Gson().toJson(message));
                }
            } else {
                removeList.add(conn);
            }
        }

        // Clean up any connections that were left open.
        for (var conn : removeList) {
            connections.remove(conn.username);
        }
    }
}
