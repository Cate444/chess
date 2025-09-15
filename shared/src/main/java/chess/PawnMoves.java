package chess;

import java.util.Collection;
import java.util.HashSet;

public class PawnMoves {
    ChessBoard board;
    ChessPosition currentPosition;
    ChessGame.TeamColor color;

    public PawnMoves(ChessBoard board, ChessPosition position) {
        this.board = board;
        currentPosition = position;
        color = board.getPiece(currentPosition).getTeamColor();
    }

    public Collection<ChessMove> possibleMoves() {
        HashSet<ChessMove> moves = new HashSet<>();
        Boolean firstMove = true;

        if(color == ChessGame.TeamColor.WHITE) {
            if (firstMove && currentPosition.getRow() == 2) {
                ChessPosition newPosition = new ChessPosition(4, currentPosition.getColumn());
                ChessPosition positionInBetween = new ChessPosition(6, currentPosition.getColumn() );
                if (validPosition(newPosition, board)[0] && !validPosition(newPosition, board)[1] && board.IsAvailable(positionInBetween,color)) {
                addMove(currentPosition, newPosition, moves);
            }
                firstMove = false;
            }
            ChessPosition newPosition = new ChessPosition(currentPosition.getRow() + 1, currentPosition.getColumn());
            if (validPosition(newPosition, board)[0] && !validPosition(newPosition, board)[1]) {
                addMove(currentPosition, newPosition, moves);
            }
            newPosition = new ChessPosition(currentPosition.getRow() + 1, currentPosition.getColumn() + 1);
            if (validPosition(newPosition, board)[0] && validPosition(newPosition, board)[1]) {
                addMove(currentPosition, newPosition, moves);
            }
            newPosition = new ChessPosition(currentPosition.getRow() + 1, currentPosition.getColumn() - 1);
            if (validPosition(newPosition, board)[0] && validPosition(newPosition, board)[1]) {
                addMove(currentPosition, newPosition, moves);
            }
        } else{
            if (firstMove && currentPosition.getRow() == 7) {
                ChessPosition newPosition = new ChessPosition(5, currentPosition.getColumn());
                ChessPosition positionInBetween = new ChessPosition(6, currentPosition.getColumn());
                if (validPosition(newPosition, board)[0] && !validPosition(newPosition, board)[1] && board.IsAvailable(positionInBetween,color)) {
                    addMove(currentPosition, newPosition, moves);
                }
                firstMove = false;
            }
            ChessPosition newPosition = new ChessPosition(currentPosition.getRow() - 1, currentPosition.getColumn());
            if (validPosition(newPosition, board)[0] && !validPosition(newPosition, board)[1]) {
                addMove(currentPosition, newPosition, moves);
            }
            newPosition = new ChessPosition(currentPosition.getRow() - 1, currentPosition.getColumn() + 1);
            if (validPosition(newPosition, board)[0] && validPosition(newPosition, board)[1]) {
                addMove(currentPosition, newPosition, moves);
            }
            newPosition = new ChessPosition(currentPosition.getRow() - 1, currentPosition.getColumn() - 1);
            if (validPosition(newPosition, board)[0] && validPosition(newPosition, board)[1]) {
                addMove(currentPosition, newPosition, moves);
            }
        }
        return moves;
    }



    private Boolean[] validPosition(ChessPosition position, ChessBoard board) {
        ChessGame.TeamColor color = board.getPiece(currentPosition).getTeamColor();
        return board.IsAvailableWithKill(position,color);

    }

    private HashSet<ChessMove> addMove(ChessPosition oldPosition, ChessPosition newPosition, HashSet<ChessMove> moves){
        if (newPosition.getRow() == 8 && color == ChessGame.TeamColor.WHITE ||newPosition.getRow() == 1 && color == ChessGame.TeamColor.BLACK ) {
            ChessMove newMove = new ChessMove(oldPosition, newPosition, ChessPiece.PieceType.QUEEN);
            moves.add(newMove);
            newMove = new ChessMove(oldPosition, newPosition, ChessPiece.PieceType.KNIGHT);
            moves.add(newMove);
            newMove = new ChessMove(oldPosition, newPosition, ChessPiece.PieceType.ROOK);
            moves.add(newMove);
            newMove = new ChessMove(oldPosition, newPosition, ChessPiece.PieceType.BISHOP);
            moves.add(newMove);
        }
        else{
            ChessMove newMove = new ChessMove(oldPosition, newPosition, null);
            moves.add(newMove);
        }
        return moves;
    }
}
