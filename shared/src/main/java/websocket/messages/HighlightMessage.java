package websocket.messages;

import chess.ChessBoard;
import chess.ChessMove;

import java.util.Collection;

public class HighlightMessage extends ServerMessage{
    Collection<ChessMove> moves;
    ChessBoard board;

    public HighlightMessage(Collection<ChessMove> moves, ChessBoard board) {
        super(ServerMessageType.HIGHLIGHT);
        this.moves = moves;
        this.board = board;
    }

    public ChessBoard getBoard() {
        return board;
    }

    public Collection<ChessMove> getMoves(){
        return moves;
    }
}
