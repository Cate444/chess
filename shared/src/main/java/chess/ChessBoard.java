package chess;

import java.util.Arrays;
import java.util.Objects;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {

    private ChessPiece[][] board;

    public ChessBoard() {
        board = new ChessPiece[8][8];
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        board[position.getRow()-1][position.getColumn()-1] = piece;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return board[position.getRow()-1][position.getColumn()-1];
    }

    public boolean IsAvailable(ChessPosition position, ChessGame.TeamColor color){
        if (position.getColumn() <= 8 && position.getColumn() > 0 && position.getRow() <= 8 && position.getRow() > 0) {
            if (board[position.getRow() - 1][position.getColumn() - 1] == null || getPiece(position).getTeamColor() != color) {
                return true;
            }
        }
        return false;
    }

    public Boolean[] IsAvailableWithKill(ChessPosition position, ChessGame.TeamColor color) {
        //[avaliable, would it result in a kill]
        Boolean[] availableAndKilled = {false, false};
        if (position.getColumn() <= 8 && position.getColumn() > 0 && position.getRow() <= 8 && position.getRow() > 0) {
            if (board[position.getRow() - 1][position.getColumn() - 1] == null) {
                availableAndKilled[0] = true;
            } else if (getPiece(position).getTeamColor() != color) {
                availableAndKilled[0] = true;
                availableAndKilled[1] = true;
            }
        }
        return availableAndKilled;
    }


    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        ChessPosition newPosition = new ChessPosition(1,1);
        ChessPiece Rook1 = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK);
        addPiece(newPosition, Rook1);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessBoard that = (ChessBoard) o;
        return Objects.deepEquals(board, that.board);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(board);
    }

    @Override
    public String toString() {
        return "ChessBoard{" +
                "board=" + Arrays.toString(board) +
                '}';
    }


}
