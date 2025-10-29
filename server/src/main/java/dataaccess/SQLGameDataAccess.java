package dataaccess;

import datamodel.GameName;
import datamodel.JoinInfo;
import datamodel.ReturnGameData;

import java.util.ArrayList;

public class SQLGameDataAccess implements GameDataAccess {

    @Override
    public void clear() {

    }

    @Override
    public int createGame(GameName gameName) {
        return 0;
    }

    @Override
    public void join(JoinInfo joinInfo, String username) throws Exception {

    }

    @Override
    public ArrayList<ReturnGameData> listGames() {
        return null;
    }
}
