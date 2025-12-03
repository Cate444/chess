package backend;

import websocket.messages.ErrorMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

public interface ServerMessageObserver {
    void notifyNotification(NotificationMessage message);
    void notifyError(ErrorMessage message);
}
