package chess;

import java.util.Collection;
import java.util.HashSet;

public class KnightCalc extends CalcMove{

    public KnightCalc(ChessPosition position, ChessBoard board){
        super(position, board);
    }

    @Override
    public Collection<ChessMove> possibleMoves() {
        Collection<ChessMove> moves = new HashSet<>();
        //up 2right
        ChessPosition newPosition = new ChessPosition(currentPosition.getRow() + 1, currentPosition.getColumn() + 2);
        if(board.isAvailableAndWillKill(newPosition, teamColor)[0]){
            addMove(newPosition, moves);
        }
        //2up and right
        newPosition = new ChessPosition(currentPosition.getRow() + 2, currentPosition.getColumn() + 1);
        if(board.isAvailableAndWillKill(newPosition, teamColor)[0]){
            addMove(newPosition, moves);
        }
        // up and 2left
        newPosition = new ChessPosition(currentPosition.getRow() + 1, currentPosition.getColumn() - 2);
        if(board.isAvailableAndWillKill(newPosition, teamColor)[0]){
            addMove(newPosition, moves);
        }
        //2up and left
        newPosition = new ChessPosition(currentPosition.getRow() + 2, currentPosition.getColumn() - 1);
        if(board.isAvailableAndWillKill(newPosition, teamColor)[0]){
            addMove(newPosition, moves);
        }
        // 2down and right
        newPosition = new ChessPosition(currentPosition.getRow() - 2, currentPosition.getColumn() + 1);
        if(board.isAvailableAndWillKill(newPosition, teamColor)[0]){
            addMove(newPosition, moves);
        }
        //down and 2right
        newPosition = new ChessPosition(currentPosition.getRow() - 1, currentPosition.getColumn() + 2);
        if(board.isAvailableAndWillKill(newPosition, teamColor)[0]){
            addMove(newPosition, moves);
        }
        //2down and left
        newPosition = new ChessPosition(currentPosition.getRow() - 2, currentPosition.getColumn() - 1);
        if(board.isAvailableAndWillKill(newPosition, teamColor)[0]){
            addMove(newPosition, moves);
        }
        //down and 2left
        newPosition = new ChessPosition(currentPosition.getRow() - 1, currentPosition.getColumn() - 2);
        if(board.isAvailableAndWillKill(newPosition, teamColor)[0]){
            addMove(newPosition, moves);
        }

        return moves;
    }
}

