package dataaccess.exceptions;

public class FailureResponseException extends DataAccessException {
  public FailureResponseException(String message) {
    super(message);
  }

  public FailureResponseException(String message, Throwable ex) {
    super(message, ex);
  }
}
