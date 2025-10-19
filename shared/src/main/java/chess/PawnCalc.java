package chess;

import java.util.Collection;
import java.util.HashSet;

public class PawnCalc extends CalcMove{

    public PawnCalc(ChessPosition position, ChessBoard board){
        super(position, board);
    }

    @Override
    public Collection<ChessMove> possibleMoves() {
        Collection<ChessMove> moves = new HashSet<>();
        if(teamColor == ChessGame.TeamColor.WHITE){
            if(currentPosition.getRow() == 2){
                ChessPosition newPosition = new ChessPosition(4, currentPosition.getColumn());
                ChessPosition betweenPosition = new ChessPosition(3, currentPosition.getColumn());
                if(board.isAvailableAndWillKill(newPosition, teamColor)[0] &&
                        !board.isAvailableAndWillKill(newPosition, teamColor)[1] &&
                        board.isAvailableAndWillKill(betweenPosition, teamColor)[0] &&
                        !board.isAvailableAndWillKill(betweenPosition, teamColor)[1]){
                    addMove(newPosition,moves);
                }
            }
            ChessPosition newPosition = new ChessPosition(currentPosition.getRow()+1, currentPosition.getColumn());
            if(board.isAvailableAndWillKill(newPosition, teamColor)[0] &&
                    !board.isAvailableAndWillKill(newPosition, teamColor)[1]){
                addMove(newPosition, moves);
            }
            newPosition = new ChessPosition(currentPosition.getRow()+1, currentPosition.getColumn()+1);
            if(board.isAvailableAndWillKill(newPosition, teamColor)[0] &&
                    board.isAvailableAndWillKill(newPosition, teamColor)[1]){
                addMove(newPosition, moves);
            }
            newPosition = new ChessPosition(currentPosition.getRow()+1, currentPosition.getColumn()-1);
            if(board.isAvailableAndWillKill(newPosition, teamColor)[0] &&
                    board.isAvailableAndWillKill(newPosition, teamColor)[1]){
                addMove(newPosition, moves);
            }

        } else if(teamColor == ChessGame.TeamColor.BLACK){
            if(currentPosition.getRow() == 7){
                ChessPosition newPosition = new ChessPosition(5, currentPosition.getColumn());
                ChessPosition betweenPosition = new ChessPosition(6, currentPosition.getColumn());
                if(board.isAvailableAndWillKill(newPosition, teamColor)[0] &&
                        !board.isAvailableAndWillKill(newPosition, teamColor)[1] &&
                        board.isAvailableAndWillKill(betweenPosition, teamColor)[0] &&
                        !board.isAvailableAndWillKill(betweenPosition, teamColor)[1]){
                    addMove(newPosition,moves);
                }
            }
            ChessPosition newPosition = new ChessPosition(currentPosition.getRow()-1, currentPosition.getColumn());
            if(board.isAvailableAndWillKill(newPosition, teamColor)[0] &&
                    !board.isAvailableAndWillKill(newPosition, teamColor)[1]){
                addMove(newPosition, moves);
            }
            newPosition = new ChessPosition(currentPosition.getRow()-1, currentPosition.getColumn()+1);
            if(board.isAvailableAndWillKill(newPosition, teamColor)[0] &&
                    board.isAvailableAndWillKill(newPosition, teamColor)[1]){
                addMove(newPosition, moves);
            }
            newPosition = new ChessPosition(currentPosition.getRow()-1, currentPosition.getColumn()-1);
            if(board.isAvailableAndWillKill(newPosition, teamColor)[0] &&
                    board.isAvailableAndWillKill(newPosition, teamColor)[1]){
                addMove(newPosition, moves);
            }
        }

        return moves;
    }

    @Override
    void addMove(ChessPosition newPosition, Collection<ChessMove> moves){
        if ((teamColor == ChessGame.TeamColor.WHITE && newPosition.getRow() == 8) ||
                (teamColor == ChessGame.TeamColor.BLACK && newPosition.getRow() == 1) ){
            moves.add(new ChessMove(currentPosition, newPosition, ChessPiece.PieceType.QUEEN));
            moves.add(new ChessMove(currentPosition, newPosition, ChessPiece.PieceType.KNIGHT));
            moves.add(new ChessMove(currentPosition, newPosition, ChessPiece.PieceType.ROOK));
            moves.add(new ChessMove(currentPosition, newPosition, ChessPiece.PieceType.BISHOP));

        } else {
            moves.add(new ChessMove(currentPosition, newPosition, null));
        }
    }

}

