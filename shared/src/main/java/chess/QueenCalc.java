package chess;

import java.util.Collection;

public class QueenCalc extends CalcMove{

    public QueenCalc(ChessPosition position, ChessBoard board){
        super(position, board);
    }

    @Override
    public Collection<ChessMove> possibleMoves() {
        Collection<ChessMove> moves;
        moves = new RookCalc(currentPosition, board).possibleMoves();
        moves.addAll(new BishopCalc(currentPosition, board).possibleMoves());
        return moves;
    }
}
