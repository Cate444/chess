package chess;

import java.util.Collection;
import java.util.Objects;
import java.util.HashSet;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    private TeamColor teamTurn;
    private ChessBoard board;
    private boolean noOneResigned = true;

    public ChessGame() {
        board = new ChessBoard();
        board.resetBoard();
        teamTurn = TeamColor.WHITE;
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return teamTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        teamTurn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece currPiece = board.getPiece(startPosition);
        Collection<ChessMove> possibleMoves = board.getPiece(startPosition).pieceMoves(board, startPosition);
        Collection<ChessMove> validMoves = new HashSet<>();
        if (currPiece == null) {
            return validMoves;
        }
        for(ChessMove move: possibleMoves){
            ChessPiece tempPiece = board.getPiece(move.getEndPosition());
            board.addPiece(move.getEndPosition(), currPiece);
            board.addPiece(move.getStartPosition(), null);
            if (!isInCheck(currPiece.getTeamColor())) {
               validMoves.add(move);
            }
            board.addPiece(move.getEndPosition(), tempPiece);
            board.addPiece(startPosition, currPiece);
        }
        return validMoves;
    }


    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPiece piece = board.getPiece(move.getStartPosition());
        if (piece == null) {
            throw new InvalidMoveException("No piece at start position.");
        }
        if (piece.getTeamColor() != teamTurn){
            throw new InvalidMoveException("It's not your turn");
        }

        if (!validMoves(move.getStartPosition()).contains(move) || piece.getTeamColor() != teamTurn) {
            throw new InvalidMoveException("Invalid move: from- " + move.startPosition + " to- " + move.endPosition);
        }

        // Handle promotions
        if (move.getPromotionPiece() == null) {
            board.addPiece(move.getEndPosition(), piece);
        } else {
            ChessPiece promotion = new ChessPiece(piece.getTeamColor(), move.getPromotionPiece());
            board.addPiece(move.getEndPosition(), promotion);
        }
        board.addPiece(move.getStartPosition(), null);
        // Switch turn
        teamTurn = (teamTurn == TeamColor.WHITE) ? TeamColor.BLACK : TeamColor.WHITE;
    }

    public String checkStatus(TeamColor teamColor){
        if (isInCheckmate(teamTurn)){
            return (teamTurn.toString() + " is in check mate");
        } else if (isInCheck(teamTurn)){
            return (teamTurn.toString() + " is in check");
        } else if (isInStalemate(teamTurn)){
            return (teamTurn.toString() + " is in stale mate");
        } else if (!noOneResigned){
            return ("game over player resigned");
        }
        return null;
    }

    public void setStatusResign(){
        noOneResigned = false;
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */

    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition kingPos = getKingPos(teamColor);

        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                ChessPosition pos = new ChessPosition(i, j);
                ChessPiece piece = board.getPiece(pos);

                if (piece == null || piece.getTeamColor() == teamColor) {continue;}

                for (ChessMove move : piece.pieceMoves(board, pos)) {
                    if (move.getEndPosition().equals(kingPos)) {
                        return true;
                        // have to throw that you're in check
                    }
                }
            }
        }
        return false;
    }


    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        if (!isInCheck(teamColor)) {return false;}
        return !canMyTeamMove(teamColor);
    }
    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        return !isInCheck(teamColor) && !canMyTeamMove(teamColor);
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }


    private ChessPosition getKingPos(TeamColor teamColor) {
        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                ChessPosition pos = new ChessPosition(i, j);
                ChessPiece currentPiece = board.getPiece(pos);

                if (currentPiece != null && currentPiece.getPieceType() == ChessPiece.PieceType.KING && currentPiece.getTeamColor() == teamColor) {
                    return pos;
                }
            }
        }
        return null;
    }

    private Boolean canMyTeamMove(TeamColor teamColor){
        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                ChessPosition pos = new ChessPosition(i, j);
                ChessPiece currentPiece = board.getPiece(pos);

                if (currentPiece != null && currentPiece.getTeamColor() == teamColor) {
                    if (!validMoves(pos).isEmpty()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }


    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }

    public boolean didAnyoneResign(){
        return noOneResigned;
    }

    @Override
    public String toString() {
        return "ChessGame{" +
                "teamTurn=" + teamTurn +
                ", board=" + board +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {return true;}
        if (o == null || getClass() != o.getClass()) {return false;}
        ChessGame chessGame = (ChessGame) o;
        return teamTurn == chessGame.teamTurn && Objects.equals(board, chessGame.board);
    }

    @Override
    public int hashCode() {
        return Objects.hash(teamTurn, board);
    }
}
