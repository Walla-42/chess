package server;

import requests.*;
import responses.*;

import java.net.HttpURLConnection;

public class ServerFacade {
    private final String serverUrl;

    public ServerFacade(String url) {
        serverUrl = url;
    }

    public ClearResponse clearCall(ClearRequest request) {
        throw new RuntimeException("not yet implemented");
    }

    public RegisterResponse registerCall(RegisterRequest request) {
        throw new RuntimeException("not yet implemented");
    }

    public LoginResponse loginCall(LoginRequest request) {
        throw new RuntimeException("not yet implemented");
    }

    public JoinGameResponse joinGameCall(JoinGameRequest request) {
        throw new RuntimeException("not yet implemented");
    }

    public ListGamesResponse listGamesCall(ListGamesRequest request) {
        throw new RuntimeException("not yet implemented");
    }

    public LogoutResponse logoutCall(LogoutRequest request) {
        throw new RuntimeException("not yet implemented");
    }

    public CreateGameResponse createGameCall(CreateGameRequest request) {
        throw new RuntimeException("not yet implemented");
    }

    private <T> T makeRequest(String method, String path, Object request, Class<T> responseClass) {
        throw new RuntimeException("not yet implemented");
    }

    private static void writeBody(Object request, HttpURLConnection http) {
        throw new RuntimeException("not yet implemented");
    }

    private static <T> T readBody(HttpURLConnection http, Class<T> responseClass) {
        throw new RuntimeException("not yet implemented");
    }
}
