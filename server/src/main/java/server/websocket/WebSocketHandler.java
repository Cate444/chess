package server.websocket;

import com.google.gson.Gson;
import io.javalin.websocket.*;
import websocket.commands.UserGameCommand;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ServerMessage;

import javax.management.Notification;
import java.io.IOException;

public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {

    private final ConnectionManager connections = new ConnectionManager();

    @Override
    public void handleConnect(WsConnectContext ctx) {
        System.out.println("Websocket connected");
        ctx.enableAutomaticPings();
    }

    @Override
    public void handleMessage(WsMessageContext ctx) {
       //this never calls handleConnect which allow pings so you need figure out why and where that should be called
        try {
            UserGameCommand userGameCommand = new Gson().fromJson(ctx.message(), UserGameCommand.class);
            switch (userGameCommand.getCommandType()) {
                case CONNECT -> connect(ctx.session, userGameCommand);
               //case CONNECT -> handleConnect(ctx);
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

    private void connect(Session session, UserGameCommand userGameCommand) throws IOException {
        connections.add(session);
        System.out.println("YOU USED THE WEBSOCKET");
        userGameCommand.getGameID();
        var notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
        connections.broadcast(session, notification);
    }
    private void makeMove(){}
    private void leave(){}
    private void resign(){}

}
