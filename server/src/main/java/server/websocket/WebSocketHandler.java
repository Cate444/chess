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
import websocket.commands.*;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ErrorMessage;
import websocket.messages.HighlightMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;


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
                case CONNECT -> connect(ctx);
                case OBSERVE -> observe(ctx);
                case MAKE_MOVE -> makeMove(ctx);
                case LEAVE -> leave(ctx);
                case RESIGN -> resign();
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
        JoinGameCommand joinGameCommand = new Gson().fromJson(ctx.message(), JoinGameCommand.class);
        UserGameCommand userGameCommand = new Gson().fromJson(ctx.message(), UserGameCommand.class);
        connections.add(session, userGameCommand.getAuthToken(), userGameCommand.getGameID());
        try {
            String username = userDataAccess.getUser(userGameCommand.getAuthToken());
            String gameName = gameDataAccess.getGameName(userGameCommand.getGameID());
            String notificationString = String.format("%s is playing game %s", username, gameName);
            var notification = new NotificationMessage(notificationString);
            connections.broadcast(session, notification, userGameCommand.getGameID());
            int gameID = userGameCommand.getGameID();
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
            chessGame.makeMove(chessMove);
            gameDataAccess.updateGameData(gameID, chessGame);
            var update = new LoadGameMessage(chessGame, color);
            connections.broadcast(null, update ,gameID);
            String status = chessGame.checkStatus(chessGame.getTeamTurn());
            if (status != null){
                var notification = new NotificationMessage(status);
                connections.broadcast(ctx.session, notification, gameID);
            }
            if (color == "WHITE"){
                status = chessGame.checkStatus(ChessGame.TeamColor.BLACK);
                if (status == null){
                    var notification = new NotificationMessage("status is still good");
                    connections.broadcast(ctx.session, notification, gameID);
                }
            } else {
                status = chessGame.checkStatus(ChessGame.TeamColor.WHITE);
                if (status == null){
                    var notification = new NotificationMessage("status is still good");
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
            String notificationString = String.format("%s left game %s", username, gameName);
            var notification = new NotificationMessage(notificationString);
            connections.broadcast(session, notification, userGameCommand.getGameID());
        } catch (Exception ex) {
            var error = new ErrorMessage(ex.getMessage());
            connections.broadcastError(ctx.session, error);
        }
    }

    private void resign(){}

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

}
