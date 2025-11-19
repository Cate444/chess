package server.websocket;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.sql.Connection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
   public final ConcurrentHashMap<String, Session> connections = new ConcurrentHashMap<>();
   public final ConcurrentHashMap<Integer, Set<String>> gameIDtoAuthTokens = new ConcurrentHashMap<>();

    public void add(Session session, String authToken, Integer gameID) {
         connections.put(authToken, session);
         if (null != gameIDtoAuthTokens.get(gameID)){
             HashSet<String> authTokenSet = (HashSet<String>) gameIDtoAuthTokens.get(gameID);
             authTokenSet.add(authToken);
             gameIDtoAuthTokens.put(gameID, authTokenSet);
         } else{
             HashSet<String> authTokenSet = new HashSet<>();
             authTokenSet.add(authToken);
             gameIDtoAuthTokens.put(gameID, authTokenSet);
         }
    }

    public void remove(Session session) {
        connections.remove(session);
    }

    public int size() {
        return connections.size();
    }

    public void broadcast(Session excludeSession, ServerMessage notification, int gameID) throws IOException {
        HashSet<String> authTokenSet = (HashSet<String>) gameIDtoAuthTokens.get(gameID);
        Gson gson = new Gson();
        String msg = gson.toJson(notification);
        for (String goodAuthToken: authTokenSet){
            Session c = connections.get(goodAuthToken);
            if (c.isOpen()) {
               // if (!c.equals(excludeSession)) {
                    c.getRemote().sendString(msg);
                //}
            }
        }
        //why is this only getting one thing in each map
        //HELP!!

    }
}

