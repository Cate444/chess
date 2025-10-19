package service;

import dataaccess.DataAccess;
import datamodel.GameName;

public class GameService {
    private final DataAccess dataAccess;

    public GameService(DataAccess dataAccess){
        this.dataAccess = dataAccess;
    }

    public int createGame(String authToken, GameName gameName) throws Exception {
        dataAccess.authenticate(authToken);
        int gameID = dataAccess.createGame(gameName);
        return gameID;
    }

    public void clear(){
        dataAccess.clear();
    }

}
