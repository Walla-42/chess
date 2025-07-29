package exceptions;

import com.google.gson.Gson;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

public class ResponseException extends RuntimeException {
    final private int statusCode;
    final private static Gson GSON = new Gson();

    public ResponseException(int statusCode, String message) {
        super(message);
        this.statusCode = statusCode;

    }

    public static ResponseException fromJson(InputStream stream) {
        var map = GSON.fromJson(new InputStreamReader(stream), HashMap.class);
        var status = ((Double) map.get("status")).intValue();
        var message = map.get("message").toString();
        return new ResponseException(status, message);
    }

}
