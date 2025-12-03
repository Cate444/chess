package dataaccess;

import chess.ChessGame;
import datamodel.GameData;
import datamodel.GameName;
import datamodel.JoinInfo;
import datamodel.ReturnGameData;

import java.util.ArrayList;

public interface GameDataAccess {
    void clear() throws Exception;
    int createGame(GameName gameName) throws Exception;
    void join(JoinInfo joinInfo, String username) throws Exception;
    ArrayList<ReturnGameData> listGames() throws Exception;
    ArrayList<GameData> listGamesWithGameInfo() throws Exception;
    String getGameName(int gameID) throws Exception;
    void updateGameData(int gameID, ChessGame chessGame) throws Exception;
    GameData getGameInfo(int gameID) throws Exception;
}
