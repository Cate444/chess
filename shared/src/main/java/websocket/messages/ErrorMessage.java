package websocket.messages;

public class ErrorMessage extends ServerMessage{
    public final String message;

    //find why it is making one of these instead of load game command on the test
    public ErrorMessage(String message) {
        super(ServerMessageType.ERROR);
        this.message = message;
    }
}
