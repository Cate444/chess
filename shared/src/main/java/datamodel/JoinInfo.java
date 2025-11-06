package datamodel;

import chess.ChessGame;

public record JoinInfo(ChessGame.TeamColor playerColor, int gameID) {
}
