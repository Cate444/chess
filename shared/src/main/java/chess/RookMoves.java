package chess;

import java.util.Collection;
import java.util.HashSet;

public class RookMoves {
    ChessBoard board;
    ChessPosition currentPosition;

    public RookMoves(ChessBoard board, ChessPosition position) {
        this.board = board;
        currentPosition = position;
    }

    public Collection<ChessMove> possibleMoves(){
        HashSet<ChessMove> moves = new HashSet<>();
        //up
        ChessPosition newPosition = new ChessPosition(currentPosition.getRow()+1, currentPosition.getColumn());
        while(validPosition(newPosition, board)[0]){
            moves = addMove(currentPosition, newPosition, moves);
            if (validPosition(newPosition, board)[1]) break;
            newPosition = new ChessPosition(newPosition.getRow()+1, newPosition.getColumn());
        }
        //right
        newPosition = new ChessPosition(currentPosition.getRow(), currentPosition.getColumn()+1);
        while(validPosition(newPosition, board)[0]){
            moves = addMove(currentPosition, newPosition, moves);
            if (validPosition(newPosition, board)[1]) break;
            newPosition = new ChessPosition(newPosition.getRow(), newPosition.getColumn()+1);
        }
        //down
        newPosition = new ChessPosition(currentPosition.getRow()-1, currentPosition.getColumn());
        System.out.println(newPosition);
        while(validPosition(newPosition, board)[0]){
            moves = addMove(currentPosition, newPosition, moves);
            if (validPosition(newPosition, board)[1]) break;
            newPosition = new ChessPosition(newPosition.getRow()-1, newPosition.getColumn());
        }
        //left
        newPosition = new ChessPosition(currentPosition.getRow(), currentPosition.getColumn()-1);
        while(validPosition(newPosition, board)[0]){
            moves = addMove(currentPosition, newPosition, moves);
            if (validPosition(newPosition, board)[1]) break;
            newPosition = new ChessPosition(newPosition.getRow(), newPosition.getColumn()-1);
        }
        return moves;
    }

    private Boolean[] validPosition(ChessPosition position, ChessBoard board) {
        ChessGame.TeamColor color = board.getPiece(currentPosition).getTeamColor();
        return board.IsAvailableWithKill(position,color);

    }

    private HashSet<ChessMove> addMove(ChessPosition oldPosition, ChessPosition newPosition, HashSet<ChessMove> moves){
        ChessMove newMove = new ChessMove(oldPosition, newPosition, null);
        moves.add(newMove);
        return moves;
    }
}
