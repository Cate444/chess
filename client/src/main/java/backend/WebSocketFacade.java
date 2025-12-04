package backend;

import chess.*;
import chess.ChessPiece.PieceType;
import com.google.gson.Gson;


import jakarta.websocket.*;
import ui.RenderBoard;
import websocket.commands.HighlightGameCommand;
import websocket.commands.JoinGameCommand;
import websocket.commands.MakeMoveGameCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.*;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

//need to extend Endpoint for websocket to work properly
public class WebSocketFacade extends Endpoint {

    Session session;
    ServerMessageObserver serverMessageObserver;
    ChessGame.TeamColor teamColor;
    ChessBoard currentChessBoard;

    public WebSocketFacade(String url, ServerMessageObserver serverMessageObserver) throws Exception {
        try {
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/ws");
            this.serverMessageObserver = serverMessageObserver;

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            //set message handler
            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    ServerMessage notification = new Gson().fromJson(message, ServerMessage.class);
                    switch (notification.getServerMessageType()) {
                        case NOTIFICATION -> notificationMessage(message);
                        case ERROR -> errorMessage(message);
                        case LOAD_GAME -> loadGameMessage(message);
                        case HIGHLIGHT -> highlightMessage(message);
                        default -> throw new IllegalStateException("Unexpected value: " + notification.getServerMessageType());
                    }
                    //serverMessageObserver.notify(notification);
                }
            });
        } catch (Exception ex) {
            throw new Exception(ex);
        }
    }

    //Endpoint requires this method, but you don't have to do anything
    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }

    private void highlightMessage(String message){
        HighlightMessage highlightMessage = new Gson().fromJson(message, HighlightMessage.class);
        ChessBoard chessBoard = highlightMessage.getBoard();
        String[][] board = transform(chessBoard);
        Collection<ChessMove> moves = highlightMessage.getMoves();
        List<ChessPosition> positions = new ArrayList<>();
        for (ChessMove move : moves) {
            positions.add(move.getEndPosition());
            //System.out.println(move.getEndPosition().toString());
        }
        RenderBoard renderBoard = new RenderBoard();
        if (teamColor == null){
            renderBoard.render(ChessGame.TeamColor.WHITE.toString(), board, positions);
        }else {
            renderBoard.render(teamColor.toString(),board, positions);
        }
    }

    private void notificationMessage(String message){
        NotificationMessage notificationMessage = new Gson().fromJson(message, NotificationMessage.class);
        serverMessageObserver.notifyNotification(notificationMessage);
    }

    private void errorMessage(String message){
        ErrorMessage errorMessage = new Gson().fromJson(message, ErrorMessage.class);
        serverMessageObserver.notifyError(errorMessage);
    }

    private void loadGameMessage(String message){
        LoadGameMessage loadGameMessage = new Gson().fromJson(message, LoadGameMessage.class);
        ChessBoard chessBoard = loadGameMessage.gameBoard();
        currentChessBoard = chessBoard;
        String[][] board = transform(chessBoard);
        RenderBoard boardRender = new RenderBoard();
        if (teamColor == null){
            boardRender.render("WHITE", board);
        }else {
            boardRender.render(teamColor.toString(), board);
        }

    }

    public void observeGame(int gameID, String authToken) throws IOException {
        try {
            var userGameCommand = new UserGameCommand(UserGameCommand.CommandType.OBSERVE, authToken, gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(userGameCommand));
        } catch (Exception ex) {
            throw ex;
        }
    }

    public void highlight(ChessPosition position, int gameID, String authToken) throws IOException{
        try {
            if(currentChessBoard.getPiece(position) == null){
                throw new IOException("no piece there");
            }
            var highlightCommand = new HighlightGameCommand(authToken, gameID, position);
            this.session.getBasicRemote().sendText(new Gson().toJson(highlightCommand));
        } catch (Exception ex) {
            throw ex;
        }
    }

    private String[][] transform(ChessBoard chessBoard){
        String[][] board = new String[8][8];
        for(int i= 0; i <8; i++){
            String[] row = new String[8];
            for (int j=0; j <8; j++){
               ChessPiece piece = chessBoard.getPiece(new ChessPosition(i+1, j+1));
               if (piece == null){
                  row[j] = " ";
               } else if(piece.getTeamColor() == ChessGame.TeamColor.WHITE){
                   switch (piece.getPieceType()){
                       case ROOK -> row[j] = String.valueOf("♖");
                       case KNIGHT -> row[j] = String.valueOf("♘");
                       case BISHOP -> row[j] = String.valueOf("♗");
                       case QUEEN -> row[j] = String.valueOf("♔");
                       case KING -> row[j] = String.valueOf("♕");
                       case PAWN -> row[j] = String.valueOf("♙");
                   }
               } else if (piece.getTeamColor() == ChessGame.TeamColor.BLACK) {
                   switch (piece.getPieceType()){
                       case ROOK -> row[j] = String.valueOf("♜");
                       case KNIGHT -> row[j] = String.valueOf("♞");
                       case BISHOP -> row[j] = String.valueOf("♝");
                       case QUEEN -> row[j] = String.valueOf("♚");
                       case KING -> row[j] = String.valueOf("♛");
                       case PAWN -> row[j] = String.valueOf("♟");
                   }
               }
            }
            board[i] = row;
        }
        return board;
    }

    public void joinGame(String authToken, int gameID, ChessGame.TeamColor color) throws Exception{
        try {
            var joinGameCommand = new JoinGameCommand(authToken, gameID, color);
            this.session.getBasicRemote().sendText(new Gson().toJson(joinGameCommand));
            this.teamColor = color;
        } catch (Exception ex) {
            throw ex;
        }
    }

    public void leave(int gameID, String authToken) throws IOException{
        try {
            var userGameCommand = new UserGameCommand(UserGameCommand.CommandType.LEAVE, authToken, gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(userGameCommand));
            teamColor = null;
        } catch (Exception ex) {
            throw ex;
        }
    }

    public void move(ChessMove chessMove, int gameID, String authToken) throws Exception{
        try {
            var userGameCommand = new MakeMoveGameCommand(authToken, gameID, chessMove);
            this.session.getBasicRemote().sendText(new Gson().toJson(userGameCommand));
            //trying to move the 3 time the websocket is closed

        } catch (Exception ex) {
            throw ex;
        }
    }

    public void redraw(int gameID, String authToken) throws Exception{
        try{
            String[][] board = transform(currentChessBoard);
            RenderBoard boardRender = new RenderBoard();
            if (teamColor == null){
                boardRender.render("WHITE", board);
            }else {
                boardRender.render(teamColor.toString(), board);
            }
        } catch (Exception ex){
            throw ex;
        }
    }

    public ChessBoard getCurrentChessBoard(){
        return currentChessBoard;
    }

    public void resign(int gameID, String authToken) throws Exception{
        try{
            UserGameCommand resignCommand = new UserGameCommand(UserGameCommand.CommandType.RESIGN, authToken, gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(resignCommand));
        } catch (Exception ex) {
            throw ex;
        }
    }
}