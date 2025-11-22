package server.websocket;

import chess.ChessGame;
import chess.ChessMove;
import com.google.gson.Gson;
import dataaccess.GameDataAccess;
import dataaccess.SQLGameDataAccess;
import dataaccess.SQLUserDataAccess;
import dataaccess.UserDataAccess;
import datamodel.GameData;
import io.javalin.websocket.*;
import websocket.commands.JoinGameCommand;
import websocket.commands.MakeMoveGameCommand;
import websocket.commands.UserGameCommand;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;

import java.util.ArrayList;


public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {

    private final ConnectionManager connections = new ConnectionManager();
    private final UserDataAccess userDataAccess = new SQLUserDataAccess();
    private final GameDataAccess gameDataAccess = new SQLGameDataAccess();

    public WebSocketHandler() throws Exception {
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
                case CONNECT -> observe(ctx);
                case OBSERVE -> observe(ctx);
                case MAKE_MOVE -> makeMove(ctx);
                case LEAVE -> leave(ctx);
                case RESIGN -> resign();
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
        JoinGameCommand joinGameCommand = new Gson().fromJson(ctx.message(), JoinGameCommand.class);
        //breaks on here
        connections.add(session, joinGameCommand.getAuthToken(), joinGameCommand.getGameID());
        String username = userDataAccess.getUser(joinGameCommand.getAuthToken());
        String gameName = gameDataAccess.getGameName(joinGameCommand.getGameID().intValue());
        String color = joinGameCommand.teamColor.toString();
        String notificationString = String.format("%s is playing game %s as %s", username, gameName, color);
        var notification = new NotificationMessage(notificationString);
        connections.broadcast(session, notification, joinGameCommand.getGameID());
    }

    private void observe(WsMessageContext ctx) throws Exception {
        Session session = ctx.session;
        UserGameCommand userGameCommand = new Gson().fromJson(ctx.message(), UserGameCommand.class);
        connections.add(session, userGameCommand.getAuthToken(), userGameCommand.getGameID());
        String username = userDataAccess.getUser(userGameCommand.getAuthToken());
        String gameName = gameDataAccess.getGameName(userGameCommand.getGameID().intValue());
        String notificationString = String.format("%s is observing game %s", username, gameName);
        var notification = new NotificationMessage(notificationString);
        connections.broadcast(session, notification, userGameCommand.getGameID());
    }
    private void makeMove(WsMessageContext ctx) throws Exception{
        Session session = ctx.session;
        MakeMoveGameCommand makeMoveGameCommand = new Gson().fromJson(ctx.message(), MakeMoveGameCommand.class);
            ChessMove chessMove = makeMoveGameCommand.getChessMove();
            int gameID = makeMoveGameCommand.getGameID();
            ChessGame chessGame = getGame(gameID);
            if (chessGame == null) {
                throw new Exception("game not found");
            }
            chessGame.makeMove(chessMove);
            gameDataAccess.updateGameData(gameID, chessGame);
            var update = new LoadGameMessage(chessGame);
            connections.broadcast(null, update ,gameID);
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
        Session session = ctx.session;
        UserGameCommand userGameCommand = new Gson().fromJson(ctx.message(), UserGameCommand.class);
        connections.remove(session, userGameCommand.getAuthToken(), userGameCommand.getGameID());
        String username = userDataAccess.getUser(userGameCommand.getAuthToken());
        String gameName = gameDataAccess.getGameName(userGameCommand.getGameID().intValue());
        String notificationString = String.format("%s left game %s", username, gameName);
        var notification = new NotificationMessage(notificationString);
        connections.broadcast(session, notification, userGameCommand.getGameID());
    }

    private void resign(){}

}
