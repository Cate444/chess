package websocket.commands;

import chess.ChessGame;
import chess.ChessMove;

public class MakeMoveGameCommand extends UserGameCommand{
    ChessMove move;

    public MakeMoveGameCommand(String authToken, int gameID, ChessMove move) {
        super(CommandType.MAKE_MOVE, authToken, gameID);
        this.move = move;
    }

    public ChessMove getMove(){
        return move;
    }
}

