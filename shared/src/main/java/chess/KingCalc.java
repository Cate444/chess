package chess;

import java.util.Collection;
import java.util.HashSet;

public class KingCalc extends CalcMove{

    public KingCalc(ChessPosition position, ChessBoard board){
        super(position, board);
    }

    @Override
    public Collection<ChessMove> possibleMoves() {
        Collection<ChessMove> moves = new HashSet<>();
        //up
        ChessPosition newPosition = new ChessPosition(currentPosition.getRow() + 1, currentPosition.getColumn());
        if(board.isAvailableAndWillKill(newPosition, teamColor)[0]){
            addMove(newPosition, moves);
        }
        //up and right
        newPosition = new ChessPosition(currentPosition.getRow() + 1, currentPosition.getColumn() + 1);
        if(board.isAvailableAndWillKill(newPosition, teamColor)[0]){
            addMove(newPosition, moves);
        }
        // up and left
        newPosition = new ChessPosition(currentPosition.getRow() + 1, currentPosition.getColumn() - 1);
        if(board.isAvailableAndWillKill(newPosition, teamColor)[0]){
            addMove(newPosition, moves);
        }
        //right
        newPosition = new ChessPosition(currentPosition.getRow(), currentPosition.getColumn() + 1);
        if(board.isAvailableAndWillKill(newPosition, teamColor)[0]){
            addMove(newPosition, moves);
        }
        //left
        newPosition = new ChessPosition(currentPosition.getRow(), currentPosition.getColumn() - 1);
        if(board.isAvailableAndWillKill(newPosition, teamColor)[0]){
            addMove(newPosition, moves);
        }
        // down and right
        newPosition = new ChessPosition(currentPosition.getRow() - 1, currentPosition.getColumn() + 1);
        if(board.isAvailableAndWillKill(newPosition, teamColor)[0]){
            addMove(newPosition, moves);
        }
        //down
        newPosition = new ChessPosition(currentPosition.getRow() - 1, currentPosition.getColumn());
        if(board.isAvailableAndWillKill(newPosition, teamColor)[0]){
            addMove(newPosition, moves);
        }
        //down and left
        newPosition = new ChessPosition(currentPosition.getRow() - 1, currentPosition.getColumn() - 1);
        if(board.isAvailableAndWillKill(newPosition, teamColor)[0]){
            addMove(newPosition, moves);
        }

        return moves;
    }


}

