package websocket.messages;

import chess.ChessBoard;
import chess.ChessGame;
import websocket.commands.UserGameCommand;

public class LoadGameMessage extends ServerMessage{

    ChessGame chessGame;
    ChessGame.TeamColor teamColor;

    public LoadGameMessage(ChessGame chessGame, ChessGame.TeamColor color){
        super(ServerMessageType.LOAD_GAME);
        this.chessGame = chessGame;
        teamColor = color;
    }

    public ChessBoard gameBoard(){
        return chessGame.getBoard();
    }

    public ChessGame.TeamColor getTeamColor(){
        return teamColor;
    }
}
