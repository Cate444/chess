package backend;

import websocket.messages.ErrorMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

public interface ServerMessageObserver {
    void notify(ServerMessage message);
    void notifyNotification(NotificationMessage message);
    void notifyError(ErrorMessage message);
}
