package server.websocket;

import com.mysql.cj.Session;
import websocket.commands.UserGameCommand;

public class Connection {
    public String authToken;
    public Session session;

    public Connection(String authToken, Session session) {
        this.authToken = authToken;
        this.session = session;
    }
}
