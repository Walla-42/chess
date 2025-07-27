package Exceptions;

import com.google.gson.Gson;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class ResponseException extends RuntimeException {
    final private int statusCode;
    final private static Gson gson = new Gson();

    public ResponseException(int statusCode, String message) {
        super(message);
        this.statusCode = statusCode;

    }

    public String toJson() {
        return gson.toJson(Map.of("message", getMessage(), "status", statusCode));
    }

    public static ResponseException fromJson(InputStream stream) {
        var map = gson.fromJson(new InputStreamReader(stream), HashMap.class);
        var status = ((Double) map.get("status")).intValue();
        var message = map.get("message").toString();
        return new ResponseException(status, message);
    }

    public int statusCode() {
        return statusCode;
    }
}
