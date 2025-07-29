package server;

import exceptions.ResponseException;
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
    private static final Gson GSON = new Gson();


    public ServerFacade(Integer port) {
        baseUrl = "http://localhost:" + port;
    }

    /**
     * Sends a request to clear the server database.
     *
     * @param request the clear request body (can be empty)
     * @return the response body indicating success or failure
     */
    public ClearResponseBody clearCall(ClearRequestBody request) {
        var path = "/db";
        return this.sendRequest("DELETE", path, null, request, ClearResponseBody.class);
    }

    /**
     * Sends a registration request to the server.
     *
     * @param request the registration request body containing username and password
     * @return the registration response body containing authentication data
     */
    public RegisterResponseBody registerCall(RegisterRequestBody request) {
        var path = "/user";
        return this.sendRequest("POST", path, null, request, RegisterResponseBody.class);
    }

    /**
     * Sends a login request to the server.
     *
     * @param request the login request body containing credentials
     * @return the login response body containing authentication data
     */
    public LoginResponseBody loginCall(LoginRequestBody request) {
        var path = "/session";
        return this.sendRequest("POST", path, null, request, LoginResponseBody.class);
    }

    /**
     * Sends a request to join a game.
     *
     * @param request   the join game request body including game ID and player color
     * @param authToken the authorization token
     * @return the response body indicating success or failure
     */
    public JoinGameResponseBody joinGameCall(JoinGameRequestBody request, String authToken) {
        var path = "/game";
        return this.sendRequest("PUT", path, authToken, request, JoinGameResponseBody.class);
    }

    /**
     * Sends a request to retrieve a list of available games.
     *
     * @param authToken the authorization token
     * @return the list games response body containing game metadata
     */
    public ListGamesResponseBody listGamesCall(String authToken) {
        var path = "/game";
        return this.sendRequest("GET", path, authToken, null, ListGamesResponseBody.class);
    }

    /**
     * Sends a logout request to invalidate the session.
     *
     * @param request   the logout request body (typically includes username)
     * @param authToken the authorization token
     * @return the logout response body indicating success or failure
     */
    public LogoutResponseBody logoutCall(LogoutRequestBody request, String authToken) {
        var path = "/session";
        return this.sendRequest("DELETE", path, authToken, request, LogoutResponseBody.class);
    }

    /**
     * Sends a request to create a new game.
     *
     * @param request   the create game request body including the game name
     * @param authToken the authorization token
     * @return the create game response body with game ID and other metadata
     */
    public CreateGameResponseBody createGameCall(CreateGameRequestBody request, String authToken) {
        var path = "/game";
        return this.sendRequest("POST", path, authToken, request, CreateGameResponseBody.class);
    }

    /**
     * Internal method to send an HTTP request and parse the response.
     *
     * @param method       the HTTP method to use (GET, POST, PUT, DELETE)
     * @param route        the URL path (e.g., "/game")
     * @param token        the optional authorization token (null if not needed)
     * @param reqBody      the request body to serialize into JSON
     * @param responseType the expected response type class
     * @return the deserialized response object
     * @throws ResponseException if an error occurs during the request
     */
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

    /**
     * Serializes the given object as JSON and writes it to the HTTP request body.
     *
     * @param conn the HTTP connection
     * @param data the object to serialize
     * @throws IOException if an I/O error occurs
     */
    private void writeJson(HttpURLConnection conn, Object data) throws IOException {
        conn.setRequestProperty("Content-Type", "application/json");
        try (OutputStream output = conn.getOutputStream()) {
            String json = GSON.toJson(data);
            output.write(json.getBytes());
        }
    }

    /**
     * Parses the HTTP response body as JSON into the given type.
     *
     * @param conn the HTTP connection
     * @param type the expected response type class
     * @param <T>  the type of the response body
     * @return the parsed response object or {@code null} if empty
     * @throws IOException if an I/O error occurs
     */
    private <T> T parseResponse(HttpURLConnection conn, Class<T> type) throws IOException {
        if (conn.getContentLength() == 0 || type == null) {
            return null;
        }

        try (InputStream body = conn.getInputStream()) {
            InputStreamReader reader = new InputStreamReader(body);
            return GSON.fromJson(reader, type);
        }
    }

    /**
     * Validates the HTTP status code and throws a {@link ResponseException} on error.
     *
     * @param conn the HTTP connection
     * @throws IOException if an I/O error occurs or the status code is not 2xx
     */
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