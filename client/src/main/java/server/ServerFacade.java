package server;

import Exceptions.ResponseException;
import com.google.gson.Gson;
import requests.*;
import responses.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

public class ServerFacade {
    private final String baseUrl;
    private static final Gson gson = new Gson();


    public ServerFacade(String url) {
        baseUrl = url;
    }

    public ClearResponseBody clearCall(ClearRequestBody request) {
        var path = "/db";
        return this.sendRequest("DELETE", path, null, request, ClearResponseBody.class);
    }

    public RegisterResponseBody registerCall(RegisterRequestBody request) {
        var path = "/user";
        return this.sendRequest("POST", path, null, request, RegisterResponseBody.class);
    }

    public LoginResponseBody loginCall(LoginRequestBody request) {
        var path = "/session";
        return this.sendRequest("POST", path, null, request, LoginResponseBody.class);
    }

    public JoinGameResponseBody joinGameCall(JoinGameRequestBody request, String authToken) {
        var path = "/game";
        return this.sendRequest("PUT", path, authToken, request, JoinGameResponseBody.class);

    }

    public ListGamesResponseBody listGamesCall(String authToken) {
        var path = "/game";
        return this.sendRequest("GET", path, authToken, null, ListGamesResponseBody.class);

    }

    public LogoutResponseBody logoutCall(LogoutRequestBody request, String authToken) {
        var path = "/session";
        return this.sendRequest("DELETE", path, authToken, request, LogoutResponseBody.class);

    }

    public CreateGameResponseBody createGameCall(CreateGameRequestBody request, String authToken) {
        var path = "/game";
        return this.sendRequest("POST", path, authToken, request, CreateGameResponseBody.class);

    }

    private <T> T sendRequest(String method, String route, String token, Object reqBody, Class<T> responseType) {
        HttpURLConnection connection = null;

        try {
            URI uri = new URI(baseUrl + route);
            URL url = uri.toURL();
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(method);
            connection.setDoOutput(true);

            if (token != null) {
                connection.setRequestProperty("Authorization", token);
            }

            if (reqBody != null) {
                writeJson(connection, reqBody);
            }

            connection.connect();

            validateStatus(connection);
            return parseResponse(connection, responseType);

        } catch (ResponseException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseException(500, "Unexpected error: " + e.getMessage());
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private void writeJson(HttpURLConnection conn, Object data) throws IOException {
        conn.setRequestProperty("Content-Type", "application/json");
        try (OutputStream output = conn.getOutputStream()) {
            String json = gson.toJson(data);
            output.write(json.getBytes());
        }
    }

    private <T> T parseResponse(HttpURLConnection conn, Class<T> type) throws IOException {
        if (conn.getContentLength() == 0 || type == null) {
            return null;
        }

        try (InputStream body = conn.getInputStream()) {
            InputStreamReader reader = new InputStreamReader(body);
            return gson.fromJson(reader, type);
        }
    }

    private void validateStatus(HttpURLConnection conn) throws IOException {
        int code = conn.getResponseCode();
        if (code >= 200 && code < 300) {
            return;
        }

        try (InputStream errorStream = conn.getErrorStream()) {
            if (errorStream != null) {
                throw ResponseException.fromJson(errorStream);
            }
        }

        throw new ResponseException(code, "HTTP error code: " + code);
    }
}