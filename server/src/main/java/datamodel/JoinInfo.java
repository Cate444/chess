package datamodel;

import chess.ChessGame;
import chess.ChessPiece;

public record JoinInfo(ChessGame.TeamColor playerColor, int gameID) {
}
