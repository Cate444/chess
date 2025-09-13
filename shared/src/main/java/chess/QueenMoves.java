package chess;

import java.util.Collection;
import java.util.HashSet;

public class QueenMoves {

    ChessBoard board;
    ChessPosition currentPosition;

    public QueenMoves(ChessBoard board, ChessPosition position) {
        this.board = board;
        currentPosition = position;
    }

    public Collection<ChessMove> possibleMoves() {
        Collection<ChessMove> moves = new HashSet<>();
        RookMoves rookObject = new RookMoves(board, currentPosition);
        moves = rookObject.possibleMoves();
        BishopMoves bishopObject  = new BishopMoves(board, currentPosition);
        moves.addAll(bishopObject.possibleMoves());
        return moves;
    }
}
