package backend;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessMove;
import com.google.gson.Gson;


import jakarta.websocket.*;
import websocket.commands.JoinGameCommand;
import websocket.commands.MakeMoveGameCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.net.URI;

//need to extend Endpoint for websocket to work properly
public class WebSocketFacade extends Endpoint {

    Session session;
    ServerMessageObserver serverMessageObserver;

    public WebSocketFacade(String url, ServerMessageObserver serverMessageObserver) throws Exception {
        try {
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/ws");
            this.serverMessageObserver = serverMessageObserver;

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            //set message handler
            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    ServerMessage notification = new Gson().fromJson(message, ServerMessage.class);
                    switch (notification.getServerMessageType()) {
                        case NOTIFICATION :
                            NotificationMessage notificationMessage = new Gson().fromJson(message, NotificationMessage.class);
//                            System.out.print("\n");
//                            System.out.println(notificationMessage.message);
                            serverMessageObserver.notifyNotification(notificationMessage);
//                        case ERROR -> ;
                        case LOAD_GAME :
                            LoadGameMessage loadGameMessage = new Gson().fromJson(message, LoadGameMessage.class);
                            ChessBoard chessBoard = loadGameMessage.gameBoard();
                            String board = transform(chessBoard);
                            System.out.println(chessBoard.toString());
                    }
                    serverMessageObserver.notify(notification);
                }
            });
        } catch (Exception ex) {
            throw new Exception(ex);
        }
    }

    //Endpoint requires this method, but you don't have to do anything
    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }

    public void observeGame(int gameID, String authToken) throws IOException {
        try {
            var userGameCommand = new UserGameCommand(UserGameCommand.CommandType.OBSERVE, authToken, gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(userGameCommand));
        } catch (Exception ex) {
            throw ex;
        }
    }

    private String transform(ChessBoard chessBoard){

    }

    public void joinGame(String authToken, int gameID, ChessGame.TeamColor color) throws Exception{
        try {
            var joinGameCommand = new JoinGameCommand(authToken, gameID, color);
            //var userGameCommand = new UserGameCommand(UserGameCommand.CommandType.CONNECT, authToken, gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(joinGameCommand));
        } catch (Exception ex) {
            throw ex;
        }
    }

    public void leave(int gameID, String authToken) throws IOException{
        try {
            var userGameCommand = new UserGameCommand(UserGameCommand.CommandType.LEAVE, authToken, gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(userGameCommand));
        } catch (Exception ex) {
            throw ex;
        }
    }

    public void move(ChessMove chessMove, int gameID, String authToken) throws Exception{
        try {
            var userGameCommand = new MakeMoveGameCommand(authToken, gameID, chessMove);
            this.session.getBasicRemote().sendText(new Gson().toJson(userGameCommand));
        } catch (Exception ex) {
            throw ex;
        }
    }
}