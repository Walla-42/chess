package requests;

public record CreateGameRequestBody(String authToken, String gameName) {
}
