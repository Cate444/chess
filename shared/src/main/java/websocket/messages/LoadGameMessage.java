package websocket.messages;

import chess.ChessBoard;
import chess.ChessGame;
import websocket.commands.UserGameCommand;

public class LoadGameMessage extends ServerMessage{

    ChessGame chessGame;
    String teamColor;

    public LoadGameMessage(ChessGame chessGame, String color){
        super(ServerMessageType.LOAD_GAME);
        this.chessGame = chessGame;
        teamColor = color;
    }

    public ChessBoard gameBoard(){
        return chessGame.getBoard();
    }

    public String getTeamColor(){
        return teamColor;
    }
}
