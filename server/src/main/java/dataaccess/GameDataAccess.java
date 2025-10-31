package dataaccess;

import datamodel.GameName;
import datamodel.JoinInfo;
import datamodel.ReturnGameData;
import org.junit.jupiter.api.function.Executable;

import java.util.ArrayList;

public interface GameDataAccess {
    void clear() throws Exception;
    int createGame(GameName gameName) throws Exception;
    void join(JoinInfo joinInfo, String username) throws Exception;
    ArrayList<ReturnGameData> listGames() throws Exception;
}
