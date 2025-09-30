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
    //private boolean gameOver;

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
        if (currPiece == null) {
            return null;
        }
        Collection<ChessMove> possibleMoves = board.getPiece(startPosition).pieceMoves(board, startPosition);
        for(ChessMove move: possibleMoves){
            ChessPiece tempPiece = board.getPiece(move.getEndPosition());
            board.addPiece(move.getEndPosition(), tempPiece);
            board.addPiece(move.getStartPosition(), null);
            if (isInCheck(currPiece.getTeamColor())) {
               possibleMoves.remove(move);
            }
            board.addPiece(move.getEndPosition(), tempPiece);
            board.addPiece(startPosition, currPiece);
        }
        return possibleMoves;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPiece piece = board.getPiece(move.getStartPosition());
        if (validMoves(move.getStartPosition()).contains(move) && piece.getTeamColor() == teamTurn && !isInCheck(teamTurn)) {
            if (move.getPromotionPiece() == null) {
                board.addPiece(move.getEndPosition(), piece);
                board.addPiece(move.getStartPosition(), null);
            } else {
                ChessPiece promotionPiece = new ChessPiece(piece.getTeamColor(), move.getPromotionPiece());
                board.addPiece(move.getEndPosition(), promotionPiece);
                board.addPiece(move.getStartPosition(), null);
            }
        }else {
                   throw new InvalidMoveException(String.format("Valid move:"));
                }
        if (teamTurn == TeamColor.WHITE){
            teamTurn = TeamColor.BLACK;
        } else if (teamTurn == TeamColor.BLACK) {
            teamTurn = TeamColor.WHITE;
        }
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */

    public boolean isInCheck(TeamColor teamColor) {
       ChessPosition kingPosition = getSpecificPiece(ChessPiece.PieceType.KING, teamColor);
        for(int i = 1; i <= 8; i++){
            for(int j = 1; j <= 8; j++){
                ChessPiece currentPiece = board.getPiece(new ChessPosition(i, j));
                if (currentPiece == null || currentPiece.getTeamColor() == teamColor) {
                    continue;
                }else{
                    Collection<ChessMove> moves = currentPiece.pieceMoves(board, new ChessPosition(i, j));
                    for (ChessMove move : moves) {
                        if (move.getEndPosition().equals(kingPosition)) {
                            return true;
                    }

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
        return true;
//        ChessBoard tempBoard = board.clone();
//        if(isInCheck(teamColor)){
//            for(int i = 1; i <= 8; i++){
//                for(int j = 1; j <= 8; j++){
//                    ChessPosition aPosition = new ChessPosition(i, j);
//                    if(board.getPiece(aPosition).getPieceType() != null && (board.getPiece(aPosition).getTeamColor() != teamColor)){
//                        Collection<ChessMove> moves = board.getPiece(aPosition).pieceMoves(board, aPosition);
//                        for(ChessMove move: moves){
//                            tempBoard.addPiece(move.getEndPosition(), tempBoard.getPiece(aPosition));
//                            tempBoard.addPiece(move.getEndPosition(), null);
//                            if(!isInCheck(teamColor)){
//                                return false;
//                            }
//                        }
//                    } else{continue;}
//                }
//            } return true;
//
//        } else{ return false;}
   }

    private ChessPosition getSpecificPiece(ChessPiece.PieceType type, TeamColor teamColor){
        for(int i = 1; i <= 8; i++){
            for(int j = 1; j <= 8; j++){
                ChessPosition aPosition = new ChessPosition(i, j);
                if(board.getPiece(aPosition) != null && (board.getPiece(aPosition).getPieceType() == type && (board.getPiece(aPosition).getTeamColor() == teamColor))){
                    return aPosition;
                }
            }
        }
        return null;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        return true;
//        ChessBoard tempBoard = board.clone();
//        if(!isInCheck(teamColor)){
//            for(int i = 1; i <= 8; i++){
//                for(int j = 1; j <= 8; j++){
//                    ChessPosition aPosition = new ChessPosition(i, j);
//                    if(board.getPiece(aPosition).getPieceType() != null && (board.getPiece(aPosition).getTeamColor() != teamColor)){
//                        Collection<ChessMove> moves = board.getPiece(aPosition).pieceMoves(board, aPosition);
//                        for(ChessMove move: moves){
//                            tempBoard.addPiece(move.getEndPosition(), tempBoard.getPiece(aPosition));
//                            tempBoard.addPiece(move.getEndPosition(), null);
//                            if(!isInCheck(teamColor)){
//                                return false;
//                            }
//                        }
//                    } else{continue;}
//                }
//            } return true;
//
//        } else{ return false;}
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
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
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessGame chessGame = (ChessGame) o;
        return teamTurn == chessGame.teamTurn && Objects.equals(board, chessGame.board);
    }

    @Override
    public int hashCode() {
        return Objects.hash(teamTurn, board);
    }
}
