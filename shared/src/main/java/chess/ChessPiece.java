package chess;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {
    ChessGame.TeamColor teamColor;
    ChessPiece.PieceType type;


    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.teamColor = pieceColor;
        this.type = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return teamColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */

    // calls everything
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new HashSet<>();
        if (board.getPiece(myPosition).getPieceType() == PieceType.ROOK){
            moves = new RookCalc(myPosition, board).possibleMoves();
        } else if (board.getPiece(myPosition).getPieceType() == PieceType.BISHOP){
            moves = new BishopCalc(myPosition, board).possibleMoves();
        } else if (board.getPiece(myPosition).getPieceType() == PieceType.KING){
            moves = new KingCalc(myPosition, board).possibleMoves();
        } else if (board.getPiece(myPosition).getPieceType() == PieceType.KNIGHT){
            moves = new KnightCalc(myPosition, board).possibleMoves();
        } else if (board.getPiece(myPosition).getPieceType() == PieceType.QUEEN){
            moves = new QueenCalc(myPosition, board).possibleMoves();
        } else if (board.getPiece(myPosition).getPieceType() == PieceType.PAWN){
            moves = new PawnCalc(myPosition, board).possibleMoves();
        }
        return moves;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece piece = (ChessPiece) o;
        return teamColor == piece.teamColor && type == piece.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(teamColor, type);
    }

    @Override
    public String toString() {
        return "ChessPiece{" +
                "teamColor=" + teamColor +
                ", type=" + type +
                '}';
    }
}
