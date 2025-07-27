package requests;

public record JoinGameRequestBody(String authToken, String playerColor, Integer gameID) {
}
