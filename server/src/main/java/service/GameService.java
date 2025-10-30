package service;

import dataaccess.GameDataAccess;
import dataaccess.UserDataAccess;
import datamodel.GameName;
import datamodel.JoinInfo;
import datamodel.ReturnGameData;

import java.util.ArrayList;

public class GameService {
    private final GameDataAccess gameDataAccess;
    private final UserDataAccess userDataAccess;

    public GameService(GameDataAccess gameDataAccess, UserDataAccess userDataAccess){
        this.gameDataAccess = gameDataAccess;
        this.userDataAccess = userDataAccess;
    }

    public int createGame(String authToken, GameName gameName) throws Exception {
        userDataAccess.authenticate(authToken);
        if (gameName.gameName() == null){
            throw new Exception("bad request");
        }
        int gameID = gameDataAccess.createGame(gameName);
        return gameID;
    }

    public void joinGame(String authToken, JoinInfo joinInfo) throws Exception{
        String theAuthToken = userDataAccess.authenticate(authToken);
        gameDataAccess.join(joinInfo, theAuthToken);
    }

    public ArrayList<ReturnGameData> listGames(String authToken) throws Exception{
        try{
        userDataAccess.authenticate(authToken);
        ArrayList<ReturnGameData> games = gameDataAccess.listGames();
        return games;
        } catch (Exception ex) {
            throw ex;
        }
    }

    public void clear() throws Exception {
        try {
            gameDataAccess.clear();
        } catch (Exception ex) {
            throw ex;
        }
    }

}
