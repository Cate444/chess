package websocket.commands;

public class ObserveGameCommand extends UserGameCommand{
    private String message;
    public ObserveGameCommand(String authToken, int gameID) {
        super(CommandType.OBSERVE ,authToken, gameID);
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage(String message){
        return message;
    }

    public Integer getGameID() {
        return this.getGameID();
    }
}
