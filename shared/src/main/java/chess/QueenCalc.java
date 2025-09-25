package chess;

import java.util.Collection;
import java.util.HashSet;

public class QueenCalc extends CalcMove{

    public QueenCalc(ChessPosition position, ChessBoard board){
        super(position, board);
    }

    @Override
    public Collection<ChessMove> possibleMoves() {
        Collection<ChessMove> moves = new HashSet<>();
        moves = new RookCalc(currentPosition, board).possibleMoves();
        moves.addAll(new BishopCalc(currentPosition, board).possibleMoves());
        return moves;
    }
}
