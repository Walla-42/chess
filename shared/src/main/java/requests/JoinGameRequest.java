package requests;

public record JoinGameRequest(String authToken, String playerColor, Integer gameID) {
}
