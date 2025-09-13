package chess;

import java.util.Collection;
import java.util.HashSet;

public class BishopMoves {
    ChessBoard board;
    ChessPosition currentPosition;


    BishopMoves(ChessBoard board, ChessPosition position){
        currentPosition = position;
        this.board = board;
    }

    public Collection<ChessMove> possibleMoves(){
        HashSet<ChessMove> moves = new HashSet<>();
        //up and right
        ChessPosition newPosition = new ChessPosition(currentPosition.getRow()+1, currentPosition.getColumn()+1);
        while(validPosition(newPosition, board)[0]){
            moves = addMove(currentPosition, newPosition, moves);
            if (validPosition(newPosition, board)[1]) break;
            newPosition = new ChessPosition(newPosition.getRow()+1, newPosition.getColumn()+1);
        }
        //down and right
        newPosition = new ChessPosition(currentPosition.getRow()-1, currentPosition.getColumn()+1);
        while(validPosition(newPosition, board)[0]){
            moves = addMove(currentPosition, newPosition, moves);
            if (validPosition(newPosition, board)[1]) break;
            newPosition = new ChessPosition(newPosition.getRow()-1, newPosition.getColumn()+1);
        }
        //down and left
        newPosition = new ChessPosition(currentPosition.getRow()-1, currentPosition.getColumn()-1);
        while(validPosition(newPosition, board)[0]){
            moves = addMove(currentPosition, newPosition, moves);
            if (validPosition(newPosition, board)[1]) break;
            newPosition = new ChessPosition(newPosition.getRow()-1, newPosition.getColumn()-1);
        }
        //up and left
        newPosition = new ChessPosition(currentPosition.getRow()+1, currentPosition.getColumn()-1);
        while(validPosition(newPosition, board)[0]){
            moves = addMove(currentPosition, newPosition, moves);
            if (validPosition(newPosition, board)[1]) break;
            newPosition = new ChessPosition(newPosition.getRow()+1, newPosition.getColumn()-1);
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
        //System.out.println(newMove);
        return moves;
    }

}
