package websocket.commands;

import chess.ChessGame;

public class JoinGameCommand extends UserGameCommand{
    private String message;
    public ChessGame.TeamColor teamColor;

    public JoinGameCommand(String authToken, int gameID, ChessGame.TeamColor teamColor) {
        super(CommandType.CONNECT ,authToken, gameID);
        this.teamColor = teamColor;
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
