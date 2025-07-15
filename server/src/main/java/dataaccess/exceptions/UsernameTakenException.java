package dataaccess.exceptions;

public class UsernameTakenException extends DataAccessException {
    public UsernameTakenException(String message) {
        super(message);
    }

    public UsernameTakenException(String message, Throwable ex) {
        super(message, ex);
    }
}

