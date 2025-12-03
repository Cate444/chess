package server.websocket;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import com.google.gson.Gson;
import dataaccess.GameDataAccess;
import dataaccess.SQLGameDataAccess;
import dataaccess.SQLUserDataAccess;
import dataaccess.UserDataAccess;
import datamodel.GameData;
import io.javalin.websocket.*;
import websocket.commands.*;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ErrorMessage;
import websocket.messages.HighlightMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;


public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {

    private final Map<Integer, String> letters = Map.of(
            1, "a",2, "b",3, "c",4, "d",5, "e",6, "f",7, "g",8, "h");
    private final Map<Character, Integer> numbers = Map.of(
            '1', 1,'2', 2,'3', 3,'4', 4,'5', 5,'6', 7,'8', 8);

    private final ConnectionManager connections = new ConnectionManager();
    private final UserDataAccess userDataAccess;
    private final GameDataAccess gameDataAccess;

    public WebSocketHandler(UserDataAccess userDataAccess, GameDataAccess gameDataAccess) {
        this.userDataAccess = userDataAccess;
        this.gameDataAccess = gameDataAccess;
    }

    @Override
    public void handleConnect(WsConnectContext ctx) {
        System.out.println("Websocket connected");
        ctx.enableAutomaticPings();
    }

    @Override
    public void handleMessage(WsMessageContext ctx) {
        try {
            UserGameCommand userGameCommand = new Gson().fromJson(ctx.message(), UserGameCommand.class);
            switch (userGameCommand.getCommandType()) {
                case CONNECT -> connect(ctx);
                case OBSERVE -> observe(ctx);
                case MAKE_MOVE -> makeMove(ctx);
                case LEAVE -> leave(ctx);
                case RESIGN -> resign(ctx);
                case HIGHLIGHT -> highlight(ctx);
                case REDRAW -> redraw(ctx);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void handleClose(WsCloseContext ctx) {
        System.out.println("Websocket closed");
    }

    private void connect(WsMessageContext ctx) throws Exception{
        Session session = ctx.session;
        UserGameCommand userGameCommand = new Gson().fromJson(ctx.message(), UserGameCommand.class);
        int gameID = userGameCommand.getGameID();
        connections.add(session, userGameCommand.getAuthToken(), userGameCommand.getGameID());
        try {
            String username = userDataAccess.getUser(userGameCommand.getAuthToken());
            String gameName = gameDataAccess.getGameName(userGameCommand.getGameID());
            String notificationString = String.format("%s is playing game %s", username, gameName);
            var notification = new NotificationMessage(notificationString);
            connections.broadcast(session, notification, userGameCommand.getGameID());
            ChessGame chessGame = getGame(gameID);
            if (chessGame == null) {
                throw new Exception("game not found");
            }
            gameDataAccess.updateGameData(gameID, chessGame);
            String color = chessGame.getTeamTurn().toString();
            var update = new LoadGameMessage(chessGame, color);
            connections.broadcastError(ctx.session, update);
        } catch (Exception ex) {
            var error = new ErrorMessage(ex.getMessage());
            connections.broadcastError(ctx.session, error);
        }
    }

    private void observe(WsMessageContext ctx) throws IOException {
        Session session = ctx.session;
        UserGameCommand userGameCommand = new Gson().fromJson(ctx.message(), UserGameCommand.class);
        connections.add(session, userGameCommand.getAuthToken(), userGameCommand.getGameID());
        try {
            String username = userDataAccess.getUser(userGameCommand.getAuthToken());
            String gameName = gameDataAccess.getGameName(userGameCommand.getGameID().intValue());
            String notificationString = String.format("%s is observing game %s", username, gameName);
            var notification = new NotificationMessage(notificationString);
            connections.broadcast(session, notification, userGameCommand.getGameID());
            int gameID = userGameCommand.getGameID();
            ChessGame chessGame = getGame(gameID);
            if (chessGame == null) {
                throw new Exception("game not found");
            }
            var update = new LoadGameMessage(chessGame, "WHITE");
            connections.broadcastError(ctx.session, update);
        } catch (Exception ex) {
            var error = new ErrorMessage(ex.getMessage());
            connections.broadcastError(session, error);
        }

    }

    private void makeMove(WsMessageContext ctx) throws IOException {
        MakeMoveGameCommand makeMoveGameCommand = new Gson().fromJson(ctx.message(), MakeMoveGameCommand.class);
        ChessMove chessMove = makeMoveGameCommand.getMove();
        String authToken = makeMoveGameCommand.getAuthToken();
        int gameID = makeMoveGameCommand.getGameID();
        try{
            ChessGame chessGame = getGame(gameID);
            if (chessGame == null) {
                throw new Exception("game not found");
            }
            String color = chessGame.getTeamTurn().toString();
            GameData gameData = gameDataAccess.getGameInfo(gameID);
            String dataBaseUsername = userDataAccess.getUser(authToken);
            if(ChessGame.TeamColor.WHITE == chessGame.getTeamTurn()){
                if (!gameData.whiteUsername().equals(dataBaseUsername)){
                    throw new Exception("not your turn");
                }
            } else if (!ChessGame.TeamColor.BLACK.equals(chessGame.getTeamTurn())){
                if (gameData.blackUsername() != dataBaseUsername){
                    throw new Exception("not your turn");
                }
            }
            if (!chessGame.didAnyoneResign()){
                throw new Exception("game is no longer playable");
            }
            chessGame.makeMove(chessMove);
            gameDataAccess.updateGameData(gameID, chessGame);
            var update = new LoadGameMessage(chessGame, color);
            connections.broadcast(null, update ,gameID);
            String status = chessGame.checkStatus(chessGame.getTeamTurn());
            if (status != null){
                var notification = new NotificationMessage(status);
                connections.broadcast(ctx.session, notification, gameID);
            }

            String startPosition = convertPosition(chessMove.getStartPosition());
            String endPosition = convertPosition(chessMove.getEndPosition());

            if (color == "WHITE"){
                status = chessGame.checkStatus(ChessGame.TeamColor.BLACK);
                if (status == null){
                    var notification = new NotificationMessage(dataBaseUsername + " moved from " + startPosition +
                            " to " + endPosition);
                    connections.broadcast(ctx.session, notification, gameID);
                }
            } else {
                status = chessGame.checkStatus(ChessGame.TeamColor.WHITE);
                if (status == null){
                    var notification = new NotificationMessage(dataBaseUsername + " moved from " + startPosition +
                            " to " + endPosition);
                    connections.broadcast(ctx.session, notification, gameID);
                }
            }

        } catch (Exception ex){
            var error = new ErrorMessage(ex.getMessage());
            connections.broadcastError(ctx.session, error);
        }
    }

    private ChessGame getGame(int gameID) throws Exception {
        ArrayList<GameData> gameList = gameDataAccess.listGamesWithGameInfo();
        ChessGame chessGame = null;
        for (GameData game: gameList){
            if (gameID == game.gameID()){
                chessGame = game.chessGame();
            }
        }
        return chessGame;
    }

    private void leave(WsMessageContext ctx) throws Exception{
        try {
            Session session = ctx.session;
            UserGameCommand userGameCommand = new Gson().fromJson(ctx.message(), UserGameCommand.class);
            connections.remove(session, userGameCommand.getAuthToken(), userGameCommand.getGameID());
            String username = userDataAccess.getUser(userGameCommand.getAuthToken());
            String gameName = gameDataAccess.getGameName(userGameCommand.getGameID().intValue());
            GameData gameData = gameDataAccess.getGameInfo(userGameCommand.getGameID());
            if (gameData.whiteUsername() != null) {
                if(gameData.whiteUsername().equals(username)){
                    gameDataAccess.changePlayers(ChessGame.TeamColor.WHITE ,userGameCommand.getGameID());
                }
            } else if (gameData.blackUsername() != null){
                if (gameData.blackUsername().equals(username)){
                    gameDataAccess.changePlayers(ChessGame.TeamColor.BLACK ,userGameCommand.getGameID());
                }
            }
            String notificationString = String.format("%s left game %s", username, gameName);
            var notification = new NotificationMessage(notificationString);
            connections.broadcast(session, notification, userGameCommand.getGameID());
        } catch (Exception ex) {
            var error = new ErrorMessage(ex.getMessage());
            connections.broadcastError(ctx.session, error);
        }
    }

    private void resign(WsMessageContext ctx) throws IOException {
        UserGameCommand userGameCommand = new Gson().fromJson(ctx.message(), UserGameCommand.class);
        int gameID = userGameCommand.getGameID();
        try{
            GameData gameData = gameDataAccess.getGameInfo(gameID);
            String username = userDataAccess.getUser(userGameCommand.getAuthToken());
            if (!username.equals(gameData.blackUsername()) && !username.equals(gameData.whiteUsername())){
                throw new Exception("observer can't resign");
            }
            if (!gameData.chessGame().didAnyoneResign()){
                throw new Exception("This game is already ended");
            }

            ChessGame game = getGame(gameID);
            game.setStatusResign();
            gameDataAccess.updateGameData(gameID, game);
            var notification = new NotificationMessage(username + " resigned");
            connections.broadcast(null, notification, gameID);
        }catch (Exception ex){
            var error = new ErrorMessage(ex.getMessage());
            connections.broadcastError(ctx.session, error);
        }
    }

    private void highlight(WsMessageContext ctx) throws Exception{
        Session session = ctx.session;
        HighlightGameCommand highlightGameCommand = new Gson().fromJson(ctx.message(), HighlightGameCommand.class);
        chess.ChessPosition position = highlightGameCommand.getPosition();
        int gameID = highlightGameCommand.getGameID();
        try {
            ChessGame chessGame = getGame(gameID);
            if (chessGame == null) {
                throw new Exception("game not found");
            }
            Collection<ChessMove> moves = chessGame.validMoves(position);
            var notification = new HighlightMessage(moves, chessGame.getBoard());
            connections.broadcastError(session, notification);
        } catch (Exception ex) {
            var error = new ErrorMessage(ex.getMessage());
            connections.broadcastError(ctx.session, error);
        }
    }

    private void redraw(WsMessageContext ctx) throws Exception{
        RedrawGameCommand redrawGameCommand = new Gson().fromJson(ctx.message(), RedrawGameCommand.class);
        int gameID = redrawGameCommand.getGameID();
        String color = redrawGameCommand.getColor().toString();
        try {
            ChessGame chessGame = getGame(gameID);
            if (chessGame == null) {
                throw new Exception("game not found");
            }
            var update = new LoadGameMessage(chessGame, color);
            connections.broadcastError(ctx.session, update);
        }catch (Exception ex) {
            var error = new ErrorMessage(ex.getMessage());
            connections.broadcastError(ctx.session, error);
        }
    }

    private String convertPosition(ChessPosition position){
       String col = letters.get(position.getColumn());
       String row = String.valueOf(position.getRow());
       return col + row;
    }

}
