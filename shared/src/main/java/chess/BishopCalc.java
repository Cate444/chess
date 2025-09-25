package chess;

import java.util.Collection;
import java.util.HashSet;

public class BishopCalc extends CalcMove{

    public BishopCalc(ChessPosition position, ChessBoard board){
        super(position, board);
    }

    @Override
    public Collection<ChessMove> possibleMoves(){
        Collection<ChessMove> moves = new HashSet<>();
        ChessPosition newPosition = new ChessPosition(currentPosition.getRow() + 1, currentPosition.getColumn()+1);
        // up and right
        while(newPosition.getRow() <= 8 && newPosition.getRow() > 0 && newPosition.getColumn() <= 8 && newPosition.getColumn() > 0){
            if(board.isAvailableAndWillKill(newPosition, teamColor)[0]){
                addMove(newPosition, moves);
            }
            if (board.isAvailableAndWillKill(newPosition, teamColor)[1] || (!board.isAvailableAndWillKill(newPosition, teamColor)[0] && !board.isAvailableAndWillKill(newPosition, teamColor)[1])){
                break;
            }
            newPosition = new ChessPosition(newPosition.getRow() + 1, newPosition.getColumn()+1);
        }

        //down and right
        newPosition = new ChessPosition(currentPosition.getRow() - 1, currentPosition.getColumn()+1);
        while(newPosition.getRow() <= 8 && newPosition.getRow() > 0 && newPosition.getColumn() <= 8 && newPosition.getColumn() > 0){
            if(board.isAvailableAndWillKill(newPosition, teamColor)[0]){
                addMove(newPosition, moves);
            }
            if (board.isAvailableAndWillKill(newPosition, teamColor)[1] || (!board.isAvailableAndWillKill(newPosition, teamColor)[0] && !board.isAvailableAndWillKill(newPosition, teamColor)[1])){
                break;
            }
            newPosition = new ChessPosition(newPosition.getRow() - 1, newPosition.getColumn()+1);
        }

        //down and left
        newPosition = new ChessPosition(currentPosition.getRow() - 1, currentPosition.getColumn() - 1);
        while(newPosition.getColumn() <= 8 && newPosition.getColumn() > 0 && newPosition.getRow() <= 8 && newPosition.getRow() > 0){
            if(board.isAvailableAndWillKill(newPosition, teamColor)[0]){
                addMove(newPosition, moves);
            }
            if (board.isAvailableAndWillKill(newPosition, teamColor)[1] || (!board.isAvailableAndWillKill(newPosition, teamColor)[0] && !board.isAvailableAndWillKill(newPosition, teamColor)[1])){
                break;
            }
            newPosition = new ChessPosition(newPosition.getRow() -1, newPosition.getColumn()-1);
        }

        //up and left
        newPosition = new ChessPosition(currentPosition.getRow() +1, currentPosition.getColumn() - 1);
        while(newPosition.getRow() <= 8 && newPosition.getRow() > 0 && newPosition.getColumn() <= 8 && newPosition.getColumn() > 0){
            if(board.isAvailableAndWillKill(newPosition, teamColor)[0]){
                addMove(newPosition, moves);
            }
            if (board.isAvailableAndWillKill(newPosition, teamColor)[1] || (!board.isAvailableAndWillKill(newPosition, teamColor)[0] && !board.isAvailableAndWillKill(newPosition, teamColor)[1])){
                break;
            }
            newPosition = new ChessPosition(newPosition.getRow() +1, newPosition.getColumn()-1);
        }
        return moves;
    }
}
