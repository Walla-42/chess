package responses;

public record ErrorResponseClass(String message, int status) {
    public ErrorResponseClass(String message) {
        this(message, 500);
    }
}
