package dataaccess;

import chess.ChessGame;
import datamodel.GameData;
import datamodel.GameName;
import datamodel.UserData;
import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

public class MemoryDataAccess implements DataAccess{
    private final HashMap<String, UserData> users = new HashMap<>();
    private final HashMap<String, String> authTokenUserMap = new HashMap<>();

    private final HashSet<GameData> gameList = new HashSet<>();
    private int gameCount = 1;

    @Override
    public void clear() {
        users.clear();
        authTokenUserMap.clear();
    }

    @Override
    public void createUser(UserData user) {
        users.put(user.username(), user);
    }

    @Override
    public UserData getUser(String username) {
        return users.get(username);
    }


    @Override
    public String createAuthToken(String username){
        String authToken = UUID.randomUUID().toString();
        authTokenUserMap.put(authToken, username);
        return authToken;
    }

    @Override
    public void logout(String authToken) throws Exception {
        if (!authTokenUserMap.containsKey(authToken)){
            throw new DataAccessException("Unauthorized");
        }
        authTokenUserMap.remove(authToken);
    }

    @Override
    public Boolean authenticate(String authToken) throws Exception{
        if (authTokenUserMap.containsKey(authToken)){
            return true;
        } else {
            throw new Exception("Unauthorized");
        }
    }

    @Override
    public int createGame(GameName gameName){
        GameData game = new GameData(gameCount, null, null, gameName, new ChessGame());
        gameList.add(game);
        gameCount += 1;
        return game.gameID();
    }

}
