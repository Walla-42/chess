package responses;

import model.GamesObject;

import java.util.Collection;

public record ListGamesResponseBody(Collection<GamesObject> games) {
}
