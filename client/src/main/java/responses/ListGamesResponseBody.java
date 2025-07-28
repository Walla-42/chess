package responses;

import model.GameData;

import java.util.Collection;

public record ListGamesResponseBody(Collection<GameData> games) {
}
