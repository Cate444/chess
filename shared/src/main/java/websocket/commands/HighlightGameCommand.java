package websocket.commands;

import chess.ChessGame;
import chess.ChessPosition;

public class HighlightGameCommand extends UserGameCommand{
    ChessPosition position;

    public  HighlightGameCommand(String authToken, int gameID, ChessPosition position){
        super(CommandType.HIGHLIGHT, authToken, gameID);
        this.position = position;
    }

    public ChessPosition getPosition(){
        return position;
    }


}
