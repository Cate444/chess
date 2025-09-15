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
    private final PieceType type;
    private final ChessGame.TeamColor color;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.type = type;
        this.color = pieceColor;
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
        return color;
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
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
       // this is where you will call your other classes
        HashSet<ChessMove> emptySet = new HashSet<>();
        if (board.getPiece(myPosition).getPieceType() == PieceType.KING){
            KingMoves kingObject  = new KingMoves(board, myPosition);
            return kingObject.possibleMoves();
        } else if (board.getPiece(myPosition).getPieceType() == PieceType.BISHOP){
            BishopMoves bishopObject  = new BishopMoves(board, myPosition);
            return bishopObject.possibleMoves();
        } else if (board.getPiece(myPosition).getPieceType() == PieceType.ROOK) {
            RookMoves rookObject = new RookMoves(board, myPosition);
            return rookObject.possibleMoves();
        } else if (board.getPiece(myPosition).getPieceType() == PieceType.QUEEN) {
            QueenMoves queenObject = new QueenMoves(board, myPosition);
            return queenObject.possibleMoves();
        }else if (board.getPiece(myPosition).getPieceType() == PieceType.KNIGHT) {
            KnightMoves knightObject = new KnightMoves(board, myPosition);
            return knightObject.possibleMoves();
        }else if (board.getPiece(myPosition).getPieceType() == PieceType.PAWN) {
            PawnMoves pawnObject = new PawnMoves(board, myPosition);
            return pawnObject.possibleMoves();
        }
        return emptySet;
    }

    @Override
    public String toString() {
        return "ChessPiece{" +
                "type=" + type +
                ", color=" + color +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        return type == that.type && color == that.color;
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, color);
    }
}
