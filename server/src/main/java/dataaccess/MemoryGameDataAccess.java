package dataaccess;

import chess.ChessGame;
import datamodel.GameData;
import datamodel.GameName;
import datamodel.JoinInfo;
import datamodel.ReturnGameData;

import java.util.ArrayList;

public class MemoryGameDataAccess implements GameDataAccess {
    public final ArrayList<GameData> gameList = new ArrayList<>();
    private int gameCount = 1;

    public void clear() {
        gameList.clear();
        gameCount = 1;
    }

    @Override
    public int createGame(GameName gameName){
        GameData game = new GameData(gameCount, null, null, gameName.gameName(), new ChessGame());
        gameList.add(game);
        gameCount += 1;
        return game.gameID();
    }

    @Override
    public void join(JoinInfo joinInfo, String username) throws Exception{
        for (GameData game : gameList) {
            if (game.gameID() == joinInfo.gameID()) {
                if(joinInfo.playerColor() == null){
                    throw new Exception("bad request");
                } else if (joinInfo.playerColor() == ChessGame.TeamColor.WHITE) {
                    if (game.whiteUsername() != null) {
                        throw new Exception("already taken");
                    }
                    GameData newGame = new GameData(joinInfo.gameID(), username, game.blackUsername(), game.gameName(), game.chessGame());
                    gameList.remove(game);
                    gameList.add(newGame);
                    return;
                } else if (joinInfo.playerColor() == ChessGame.TeamColor.BLACK) {
                    if (game.blackUsername() != null) {
                        throw new Exception("already taken");
                    }
                    GameData newGame = new GameData(joinInfo.gameID(), game.whiteUsername(), username, game.gameName(), game.chessGame());
                    gameList.remove(game);
                    gameList.add(newGame);
                    return;
                } else {
                    return;
                }
            }
        }
        throw new Exception("bad request");
    }

    @Override
    public ArrayList<ReturnGameData> listGames() {
        ArrayList<ReturnGameData>  newGameList= new ArrayList<>();
        for (GameData game: gameList){
            newGameList.add(new ReturnGameData(game.gameID(), game.whiteUsername(), game.blackUsername(), game.gameName()));
        }
        return newGameList;
    }
}
