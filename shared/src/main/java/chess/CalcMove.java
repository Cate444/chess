package chess;

import java.util.Collection;

public abstract class CalcMove {
    ChessBoard board;
    ChessPosition currentPosition;
    ChessGame.TeamColor teamColor;

    public CalcMove(ChessPosition position, ChessBoard board) {
        this.currentPosition = position;
        this.board = board;
        teamColor = board.getPiece(currentPosition).getTeamColor();
    }

    public abstract Collection<ChessMove>  possibleMoves();

    //override for pawn
    void addMove(ChessPosition newPosition, Collection<ChessMove> moves){
        moves.add(new ChessMove(currentPosition, newPosition, null));
    }
}