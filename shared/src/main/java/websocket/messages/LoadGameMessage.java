package websocket.messages;

import chess.ChessBoard;
import chess.ChessGame;

public class LoadGameMessage extends ServerMessage{

    ChessGame game;
    String teamColor;

    public LoadGameMessage(ChessGame chessGame, String color){
        super(ServerMessageType.LOAD_GAME);
        this.game = chessGame;
        teamColor = color;
    }

    public ChessBoard gameBoard(){
        return game.getBoard();
    }

    public String getTeamColor(){
        return teamColor;
    }
}
