package dataaccess;

import datamodel.GameName;
import datamodel.JoinInfo;
import datamodel.ReturnGameData;

import java.util.ArrayList;

public interface GameDataAccess {
    void clear();
    int createGame(GameName gameName);
    void join(JoinInfo joinInfo, String username) throws Exception;
    ArrayList<ReturnGameData> listGames();
}
