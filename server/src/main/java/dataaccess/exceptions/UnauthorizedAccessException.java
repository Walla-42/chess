package dataaccess.exceptions;

public class UnauthorizedAccessException extends DataAccessException {
    public UnauthorizedAccessException(String message) {
        super(message);
    }

    public UnauthorizedAccessException(String message, Throwable ex) {
        super(message, ex);
    }
}
