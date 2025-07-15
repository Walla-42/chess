package dataaccess.exceptions;

public class GameTakenException extends DataAccessException {
    public GameTakenException(String message) {
        super(message);
    }

    public GameTakenException(String message, Throwable ex) {
        super(message, ex);
    }
}
