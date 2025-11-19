package server.websocket;

import com.google.gson.Gson;
import dataaccess.GameDataAccess;
import dataaccess.SQLGameDataAccess;
import dataaccess.SQLUserDataAccess;
import dataaccess.UserDataAccess;
import io.javalin.websocket.*;
import websocket.commands.UserGameCommand;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.NotificationMessage;


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
                case CONNECT -> connect(ctx.session, userGameCommand);
                case MAKE_MOVE -> makeMove();
                case LEAVE -> leave();
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

    private void connect(Session session, UserGameCommand userGameCommand) throws Exception {
        connections.add(session, userGameCommand.getAuthToken(), userGameCommand.getGameID());
        String username = userDataAccess.getUser(userGameCommand.getAuthToken());
        String gameName = gameDataAccess.getGameName(userGameCommand.getGameID().intValue());
        String notificationString = String.format("%s is observing game %s", username, gameName);
        var notification = new NotificationMessage(notificationString);
        connections.broadcast(session, notification, userGameCommand.getGameID());
    }
    private void makeMove(){}
    private void leave(){}
    private void resign(){}

}
