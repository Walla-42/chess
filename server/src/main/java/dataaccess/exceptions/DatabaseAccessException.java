package dataaccess.exceptions;

public class DatabaseAccessException extends DataAccessException {
  public DatabaseAccessException(String message) {
    super(message);
  }

  public DatabaseAccessException(String message, Throwable ex) {
    super(message, ex);
  }
}
