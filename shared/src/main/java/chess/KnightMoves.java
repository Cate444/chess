package chess;

import java.util.Collection;
import java.util.HashSet;

public class KnightMoves {
    ChessBoard board;
    ChessPosition currentPosition;

    public KnightMoves(ChessBoard board, ChessPosition position) {
        this.board = board;
        currentPosition = position;
    }

    public Collection<ChessMove> possibleMoves() {
        HashSet<ChessMove> moves = new HashSet<>();
        //up two and right
        ChessPosition newPosition = new ChessPosition(currentPosition.getRow() + 2, currentPosition.getColumn()+1);
        if (validPosition(newPosition, board)){
            moves = addMove(currentPosition, newPosition, moves);
        }
        //up and two right
        newPosition = new ChessPosition(currentPosition.getRow() + 1, currentPosition.getColumn()+2);
        if (validPosition(newPosition, board)){
            moves = addMove(currentPosition, newPosition, moves);
        }
        //up two and left
        newPosition = new ChessPosition(currentPosition.getRow() + 2, currentPosition.getColumn()-1);
        if (validPosition(newPosition, board)){
            moves = addMove(currentPosition, newPosition, moves);
        }
        //up and two left
        newPosition = new ChessPosition(currentPosition.getRow() + 1, currentPosition.getColumn()-2);
        if (validPosition(newPosition, board)){
            moves = addMove(currentPosition, newPosition, moves);
        }
        //down two and right
        newPosition = new ChessPosition(currentPosition.getRow() - 2, currentPosition.getColumn() + 1);
        if (validPosition(newPosition, board)){
            moves = addMove(currentPosition, newPosition, moves);
        }
        //down and two right
        newPosition = new ChessPosition(currentPosition.getRow() - 1, currentPosition.getColumn()+2);
        if (validPosition(newPosition, board)){
            moves = addMove(currentPosition, newPosition, moves);
        }
        //down two and left
        newPosition = new ChessPosition(currentPosition.getRow() - 2, currentPosition.getColumn()-1);
        if (validPosition(newPosition, board)){
            moves = addMove(currentPosition, newPosition, moves);
        }
        //down and two left
        newPosition = new ChessPosition(currentPosition.getRow() - 1, currentPosition.getColumn()-2);
        if (validPosition(newPosition, board)){
            moves = addMove(currentPosition, newPosition, moves);
        }
        return moves;
    }



    private Boolean validPosition(ChessPosition position, ChessBoard board) {
        ChessGame.TeamColor color = board.getPiece(currentPosition).getTeamColor();
        return board.IsAvailable(position,color);

    }

    private HashSet<ChessMove> addMove(ChessPosition oldPosition, ChessPosition newPosition, HashSet<ChessMove> moves){
        ChessMove newMove = new ChessMove(oldPosition, newPosition, null);
        moves.add(newMove);
        return moves;
    }
}
