package server;

import com.google.gson.Gson;
import requests.*;
import responses.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public class ServerFacade {
    private final String serverUrl;

    public ServerFacade(String url) {
        serverUrl = url;
    }

    public ClearResponse clearCall(ClearRequest request) {
        var path = "/db";
        return this.makeRequest("DELETE", path, null, request, ClearResponse.class);
    }

    public RegisterResponse registerCall(RegisterRequest request) {
        var path = "/user";
        return this.makeRequest("POST", path, null, request, RegisterResponse.class);
    }

    public LoginResponse loginCall(LoginRequest request) {
        var path = "/session";
        return this.makeRequest("POST", path, null, request, LoginResponse.class);
    }

    public JoinGameResponse joinGameCall(JoinGameRequest request, String authToken) {
        var path = "/game";
        return this.makeRequest("PUT", path, authToken, request, JoinGameResponse.class);

    }

    public ListGamesResponse listGamesCall(String authToken) {
        var path = "/game";
        return this.makeRequest("GET", path, authToken, null, ListGamesResponse.class);

    }

    public LogoutResponse logoutCall(LogoutRequest request, String authToken) {
        var path = "/session";
        return this.makeRequest("DELETE", path, authToken, request, LogoutResponse.class);

    }

    public CreateGameResponse createGameCall(CreateGameRequest request, String authToken) {
        var path = "/game";
        return this.makeRequest("POST", path, authToken, request, CreateGameResponse.class);

    }

    private <T> T makeRequest(String method, String path, String authToken, Object request, Class<T> responseClass) {
        try {
            URL url = (new URI(serverUrl + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(true);

            if (authToken != null) {
                http.addRequestProperty("Authorization", authToken);
            }

            writeBody(request, http);
            http.connect();
            return readBody(http, responseClass);

        } catch (URISyntaxException e) {
            throw new RuntimeException("working on it");
        } catch (Exception e) {
            throw new RuntimeException("its complicated...");
        }
    }

    private static void writeBody(Object request, HttpURLConnection http) throws IOException {
        if (request != null) {
            http.addRequestProperty("Content-Type", "application/json");
            String reqData = new Gson().toJson(request);
            try (OutputStream reqBody = http.getOutputStream()) {
                reqBody.write(reqData.getBytes());
            }
        }
    }

    private static <T> T readBody(HttpURLConnection http, Class<T> responseClass) throws IOException {
        T response = null;
        if (http.getContentLength() > 0) {
            try (InputStream respBody = http.getInputStream()) {
                InputStreamReader reader = new InputStreamReader(respBody);
                if (responseClass != null) {
                    response = new Gson().fromJson(reader, responseClass);
                }
            }
        }
        return response;
    }

    // need to add error handling from the error codes sent by the server here
}
