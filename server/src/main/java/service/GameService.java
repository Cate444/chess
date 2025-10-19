package service;

import dataaccess.DataAccess;
import datamodel.GameData;
import datamodel.GameName;
import datamodel.JoinInfo;
import datamodel.ReturnGameData;

import java.util.ArrayList;
import java.util.HashSet;

public class GameService {
    private final DataAccess dataAccess;

    public GameService(DataAccess dataAccess){
        this.dataAccess = dataAccess;
    }

    public int createGame(String authToken, GameName gameName) throws Exception {
        dataAccess.authenticate(authToken);
        if (gameName.gameName() == null){
            throw new Exception("bad request");
        }
        int gameID = dataAccess.createGame(gameName);
        return gameID;
    }

    public void joinGame(String authToken, JoinInfo joinInfo) throws Exception{
        String username = dataAccess.authenticate(authToken);
        dataAccess.join(joinInfo, username);
    }

    public ArrayList<ReturnGameData> listGames(String authToken) throws Exception{
        dataAccess.authenticate(authToken);
        ArrayList<ReturnGameData> games = dataAccess.listGames();
        return games;
    }

    public void clear(){
        dataAccess.clear();
    }

}
