package chess;

import java.util.Collection;
import java.util.HashSet;

public class KingMoves{

    ChessBoard board;
    ChessPosition currentPosition;

    public KingMoves(ChessBoard board, ChessPosition position){
        this.board = board;
        currentPosition = position;
    }

    public Collection<ChessMove> possibleMoves(){
        HashSet<ChessMove> moves = new HashSet<>();
        //North
        ChessPosition positionNorth = new ChessPosition(currentPosition.getRow(), currentPosition.getColumn() + 1);
        if (validPosition(positionNorth, board)) {
           moves = addMove(currentPosition, positionNorth, moves);
        }
        //NorthEast
        ChessPosition positionNorthEast = new ChessPosition(currentPosition.getRow() + 1, currentPosition.getColumn() + 1);
        if (validPosition(positionNorthEast, board)) {
            moves = addMove(currentPosition, positionNorthEast, moves);
        }
        //East
        ChessPosition positionEast = new ChessPosition(currentPosition.getRow() + 1, currentPosition.getColumn());
        if (validPosition(positionEast, board)) {
            moves = addMove(currentPosition, positionEast, moves);
        }
        else {
            System.out.println("not moving to the right");
        }
        //SouthEast
        ChessPosition positionSouthEast = new ChessPosition(currentPosition.getRow() + 1, currentPosition.getColumn() - 1);
        if (validPosition(positionSouthEast, board)) {
            moves = addMove(currentPosition, positionSouthEast, moves);
        }
        //South
        ChessPosition positionSouth = new ChessPosition(currentPosition.getRow() , currentPosition.getColumn() - 1);
        if (validPosition(positionSouth, board)) {
            moves = addMove(currentPosition, positionSouth, moves);
        }
        //SouthWest
        ChessPosition positionSouthWest = new ChessPosition(currentPosition.getRow() - 1, currentPosition.getColumn() - 1);
        if (validPosition(positionSouthWest, board)) {
            moves = addMove(currentPosition, positionSouthWest, moves);
        }
        //West
        ChessPosition positionWest = new ChessPosition(currentPosition.getRow() - 1, currentPosition.getColumn());
        if (validPosition(positionWest, board)) {
            moves = addMove(currentPosition, positionWest, moves);
        }
        // NorthWest
        ChessPosition positionNorthWest = new ChessPosition(currentPosition.getRow() - 1, currentPosition.getColumn() + 1);
        if (validPosition(positionNorthWest, board)) {
            moves = addMove(currentPosition, positionNorthWest, moves);
        }
        return moves;
    }


    private Boolean validPosition(ChessPosition position, ChessBoard board) {
        ChessGame.TeamColor color = board.getPiece(currentPosition).getTeamColor();
        if (position.getColumn() <= 8 && position.getColumn() > 0) {
            return board.IsAvailable(position, color);
        }
        return false;
    }

    private HashSet<ChessMove> addMove(ChessPosition oldPosition, ChessPosition newPosition, HashSet<ChessMove> moves){
        ChessMove newMove = new ChessMove(oldPosition, newPosition, null);
        moves.add(newMove);
        return moves;
    }

}
