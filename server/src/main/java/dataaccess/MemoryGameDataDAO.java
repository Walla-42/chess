package dataaccess;

import model.GameData;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

public class MemoryGameDataDAO implements GameDAO{
    HashMap<Integer, GameData> gameDatabase = new HashMap<>();
    HashSet<Integer> gameIDs = new HashSet<>();

    public Collection<GameData> listGames(){
        HashSet<GameData> activeGames = new HashSet<>();
        for (int gameID : gameIDs){
            activeGames.add(gameDatabase.get(gameID));
        }
        return activeGames;
    }
}
