package websocket.messages;

import chess.ChessBoard;
import chess.ChessGame;
import websocket.commands.UserGameCommand;

public class LoadGameMessage extends ServerMessage{

    ChessGame chessGame;

    public LoadGameMessage(ChessGame chessGame){
        super(ServerMessageType.LOAD_GAME);
        this.chessGame = chessGame;
    }

    public ChessBoard gameBoard(){
        return chessGame.getBoard();
    }
}
