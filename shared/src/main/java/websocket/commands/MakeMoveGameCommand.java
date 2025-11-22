package websocket.commands;

import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;

import java.util.Map;

public class MakeMoveGameCommand extends UserGameCommand{
    ChessMove chessMove;

    public MakeMoveGameCommand(String authToken, int gameID, ChessMove move) {
        super(CommandType.MAKE_MOVE, authToken, gameID);
        chessMove = move;
    }

    public ChessMove getChessMove(){
        return chessMove;
    }
}

