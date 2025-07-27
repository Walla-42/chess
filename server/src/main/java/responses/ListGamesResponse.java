package responses;

import model.GamesObject;

import java.util.Collection;

public record ListGamesResponse(Collection<GamesObject> games) {
}
