package websocket.commands;

import chess.ChessGame;

public class RedrawGameCommand extends UserGameCommand{
    ChessGame.TeamColor teamColor;

    public RedrawGameCommand(String authToken, Integer gameID, ChessGame.TeamColor color){
        super(CommandType.REDRAW, authToken, gameID);
        teamColor = color;
    }

    public ChessGame.TeamColor getColor(){
        return teamColor;
    }

    public Integer getGameID() {
        return this.getGameID();
    }
}
