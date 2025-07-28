package server;

import java.util.HashMap;
import java.util.Map;

public class ClientSession {
    private String username;
    private String authToken;
    private Map<Integer, Integer> gameLookupMap = new HashMap<>();

    public void addMapValue(Integer userFacingID, Integer gameID) {
        gameLookupMap.put(userFacingID, gameID);

    }

    public void clearMap() {
        gameLookupMap.clear();
    }

    public Integer getGameID(Integer userFacingID) {
        if (gameLookupMap.containsKey(userFacingID)) {
            return gameLookupMap.get(userFacingID);
        } else {
            return null;
        }
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public String getUsername() {
        return this.username;
    }

    public String getAuthToken() {
        return this.authToken;
    }

    public void clearSession() {
        this.username = null;
        this.authToken = null;
    }
}
