package backend;

import java.util.*;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;
import datamodel.AuthData;
import datamodel.GameData;
import datamodel.ReturnGameData;
import server.ServerFacade;
import ui.RenderBoard;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

public class Client implements ServerMessageObserver{
    private ServerFacade server;
    private WebSocketFacade ws;
    private ServerMessageObserver serverMessageObserver;

    private Boolean loggedIn;
    private Boolean inGame;
    private Boolean observing;
    private AuthData authData;
    private RenderBoard renderBoard = new RenderBoard();
    private String teamColor;
    private String serverURL;
    int gameInvolvedIn;

    private final Map<Character, Integer> letters = Map.of(
            'a' , 1,
            'b', 2,
            'c', 3,
            'd', 4,
            'e', 5,
            'f', 6,
            'g', 7,
            'h', 8
    );

    private final Map<Character, Integer> numbers = Map.of(
            '1' , 1,
            '2', 2,
            '3', 3,
            '4', 4,
            '5', 5,
            '6', 6,
            '7', 7,
            '8', 8
    );

    public Client(String serverUrl) throws Exception {
       loggedIn = false;
       inGame = false;
       observing = false;
       serverURL = serverUrl;
       server = new ServerFacade(serverUrl);
       ws = new WebSocketFacade(serverUrl, this);
    }
    @Override
    public void notify(ServerMessage message){
        if (message.getServerMessageType() == ServerMessage.ServerMessageType.NOTIFICATION){
            //it breaks on this line
            NotificationMessage notificationMessage = (NotificationMessage) message;
            String aMessage = notificationMessage.message;
            System.out.println(notificationMessage.message);
        } else {
            System.out.println(message.toString());
        }
    }

    public void notifyNotification(NotificationMessage message){
        if (message.getServerMessageType() == ServerMessage.ServerMessageType.NOTIFICATION){
            //String aMessage = message.message;
            System.out.println(message.message);
        } else {
            System.out.println(message.toString());
        }
    }

    public void run() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            if (inGame) {
                if (!handleInGame(scanner)) {
                    break;
                }
            } else if (observing) {
                if (!handleObserving(scanner)) {
                    break;
                }
            } else if (loggedIn) {
                if (!handleLoggedIn(scanner)) {
                    break;
                }
            } else {
                if (!handleLoggedOut(scanner)) {
                    break;
                }
            }
        }
    }

    private boolean handleInGame(Scanner scanner) {
        System.out.print("[PLAYING] >>> ");
        String[] tokens = scanner.nextLine().split("\\s+");
        if (tokens.length == 0){ return true;}
        String command = tokens[0];

        switch (command) {
            case "menu" -> inGame = false;
            case "quit" -> {return false;}
            case "redraw" -> redraw(tokens);
            case "leave" -> leave(tokens);
            case "move" -> move(tokens);
            case "resign" -> {}
            case "highlight" -> {}
            default -> System.out.println("""
                    redraw - redraws board
                    leave - lets you leave the game
                    move - starts process of making move you'll be asked from where to where
                    resign - removes you from current game then leaves
                    highlight moves - shows possible moves
                    menu - return to login state
                    quit - to exit
                    help - see options""");
        }
        return true;
    }

    private boolean handleObserving(Scanner scanner) {
        System.out.print("[OBSERVING] >>> ");
        String[] tokens = scanner.nextLine().split("\\s+");
        if (tokens.length == 0){ return true;}
        String command = tokens[0];
        switch (command) {
            case "menu" -> observing = false;
            case "quit" -> { return false; }
            case "leave" -> leave(tokens);
            default -> System.out.println("""
                leave - lets you leave the game
                menu - return to login state
                quit - to exit
                help - see options""");
        }
        return true;
    }

    private boolean handleLoggedIn(Scanner scanner) {
        System.out.print("[LOGGED IN] >>> ");
        String[] tokens = scanner.nextLine().split("\\s+");
        if (tokens.length == 0) {return true;}
        String command = tokens[0];
        switch (command) {
            case "quit" -> { return false; }
            case "logout" -> logout();
            case "create" -> createGame(tokens);
            case "list" -> listGames(tokens);
            case "play" -> playGame(tokens);
            case "observe" -> observeGame(tokens);
            default -> System.out.println("""
                logout - logs you out
                create game <GAME NAME> - creates game
                list games - list active games
                play <ID> [WHITE|BLACK] - lets you join game and specifies color
                observe <ID>
                quit - to exit
                help - see options""");
        }
        return true;
    }

    private boolean handleLoggedOut(Scanner scanner) {
        System.out.print("[LOGGED OUT] >>> ");
        String[] tokens = scanner.nextLine().split("\\s+");
        if (tokens.length == 0) {return true;}
        String command = tokens[0];
        switch (command) {
            case "quit" -> { return false; }
            case "login" -> login(tokens);
            case "register" -> register(tokens);
            default -> System.out.println("""
                    register <USERNAME> <EMAIL> <PASSWORD> - create an account
                    login <USERNAME> <PASSWORD> - to log into an existing account
                    quit - to exit
                    help - see options""");
        }
        return true;
    }

    private void register(String[] tokens){
        if (tokens.length != 4){
            System.out.println("Invalid number of arguments. Usage: register <USERNAME> <PASSWORD> <EMAIL>");
        } else{
            String username = tokens[1];
            String email = tokens[2];
            String password = tokens[3];
            try{
                authData = server.register(username, email, password);
                loggedIn = true;
            } catch (Exception ex){
                System.out.println(ex.getMessage());
            }
        }
    }

    private void login(String[] tokens){
        if (tokens.length != 3){
            System.out.println("Invalid number of arguments. Usage: login <USERNAME> <PASSWORD>");
        } else{
            String username = tokens[1];
            String password = tokens[2];
            try {
                authData = server.login(username, password);
                loggedIn = true;
            } catch (Exception ex) {
                if (ex.getMessage().equals("body exception: {\"message\":\"Error: unauthorized\"}")) {
                    System.out.println("Username doesn't exist");
                } else {
                    System.out.println("internal server error");
                }
            }
        }
    }

    private void logout(){
        try{
            server.logout(authData.authToken());
            loggedIn = false;
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    private void createGame(String[] tokens){
        if (tokens.length != 3){
            System.out.println("Invalid number of arguments. Usage: create game <GAME NAME>");
        } else {
            String gameName = tokens[2];
            try{
                Map<String, Integer> gameId = server.createGame(authData.authToken(), gameName);
                System.out.printf("%s id is %s %n", gameName, gameId.get("gameID"));
            } catch (Exception ex) {
                if (ex.getMessage().equals("body exception: {\"message\":\"Error: unauthorized\"}")) {
                    System.out.println("you aren't authorized");
                } else {
                    System.out.println("internal server error");
                }
            }
        }
    }

    private void listGames(String[] tokens){
        if (tokens.length != 2){
            System.out.println("Invalid number of arguments. Usage: list games");
        } else {
        try{
            Map<String, Object> games = server.listGames(authData.authToken());

            List<ReturnGameData> gameList = (List<ReturnGameData>) games.get("games");
            System.out.println("Game Name: WhitePlayer, BlackPlayer");
            for (ReturnGameData game : gameList) {
                System.out.printf("  %s: %s, %s%n", game.gameName(), game.whiteUsername(), game.blackUsername());
            }

        } catch (Exception ex) {
            if (ex.getMessage().equals("body exception: {\"message\":\"Error: unauthorized\"}")) {
                System.out.println("you aren't authorized");
            } else {
                System.out.println("internal server error");
            }
        }
        }
    }

    private void playGame(String[] tokens){
        if (tokens.length != 3){
            System.out.println("Invalid number of arguments. Usage: play <ID> [WHITE|BLACK]");
        } else if (!(Objects.equals(tokens[2].toUpperCase(), "WHITE") | Objects.equals(tokens[2].toUpperCase(), "BLACK"))){
            System.out.println("Invalid argument. Usage: play <ID> [WHITE|BLACK]");
        } else {
            int id = Integer.parseInt(tokens[1]);
            teamColor = tokens[2].toUpperCase();
            ChessGame.TeamColor color = ChessGame.TeamColor.valueOf(tokens[2].toUpperCase());
            try {
                server.joinGame(authData.authToken(), id, color);
                ws.joinGame(authData.authToken(), id, color);
                renderBoard.render(tokens[2]);
                inGame = true;
                gameInvolvedIn = id;
            } catch (Exception ex) {
                switch (ex.getMessage()){
                    case "body exception: {\"message\":\"Error: unauthorized\"}" -> System.out.println("you aren't authorized");
                    case "body exception: {\"message\":\"Error: team already has player\"}" -> System.out.println("team already has player");
                    default -> System.out.println("internal server error");
                }
            }
        }
    }

    private void leave(String[] tokens){
        if (tokens.length != 1){
            System.out.println("Invalid number of arguments. Usage: leave ");
        } else{
            try{
                ws.leave(gameInvolvedIn, authData.authToken());
                observing = false;
                inGame = false;
                gameInvolvedIn = 0;
            } catch (Exception ex) {
                if (ex.getMessage().equals("body exception: {\"message\":\"Error: unauthorized\"}")) {
                    System.out.println("you aren't authorized");
                } else {
                    System.out.println("internal server error");
                }
            }
        }
    }

    private void observeGame(String[] tokens){
        if (tokens.length != 2){
            System.out.println("Invalid number of arguments. Usage: observe game <ID> ");
        } else{
            int id = Integer.parseInt(tokens[1]);
            try{
                ws.observeGame(id, authData.authToken());
                renderBoard.render("WHITE");
                observing = true;
                gameInvolvedIn = id;
            } catch (Exception ex) {
                if (ex.getMessage().equals("body exception: {\"message\":\"Error: unauthorized\"}")) {
                    System.out.println("you aren't authorized");
                } else {
                    System.out.println("internal server error");
                }
            }
        }
    }
    private void redraw(String[] tokens){
        if (tokens.length != 1){
            System.out.println("Invalid number of arguments. Usage: redraw ");
        } else {
            renderBoard.render(teamColor);
        }
    }
    private void move(String[] tokens){
        if (tokens.length != 1){
            System.out.println("Invalid number of arguments. Usage: observe game <ID> ");
        } else{
            System.out.println("Please input colum then row ex. e5");
            Scanner scanner = new Scanner(System.in);
            System.out.print("from: ");
            String fromPosition = scanner.nextLine().strip();
            System.out.print("to : ");
            String toPosition = scanner.nextLine().strip();
            ChessMove chessMove = convert(fromPosition, toPosition);
            try{
                ws.move(chessMove, gameInvolvedIn, authData.authToken());

                //renderBoard.render("WHITE");
                //observing = true;
            } catch (Exception ex) {
                if (ex.getMessage().equals("body exception: {\"message\":\"Error: unauthorized\"}")) {
                    System.out.println("you aren't authorized");
                } else {
                    System.out.println("internal server error");
                }
            }
        }
    }

    private ChessMove convert(String fromPosition, String toPosition){
        // trim & basic validation
        if (fromPosition == null || toPosition == null) {
            throw new IllegalArgumentException("positions cannot be null");
        }
        fromPosition = fromPosition.strip();
        toPosition   = toPosition.strip();

        if (fromPosition.length() < 2 || toPosition.length() < 2) {
            throw new IllegalArgumentException("positions must be like \"a2\"");
        }

        // letter is column (a-h), number is row (1-8)
        char fromColChar = Character.toLowerCase(fromPosition.charAt(0)); // 'a'
        char fromRowChar = fromPosition.charAt(1);                        // '2'
        char toColChar   = Character.toLowerCase(toPosition.charAt(0));   // 'a'
        char toRowChar   = toPosition.charAt(1);                          // '3'

        Integer fromCol = letters.get(fromColChar); // letters: a->1
        Integer fromRow = numbers.get(fromRowChar); // numbers: '2'->2
        Integer toCol   = letters.get(toColChar);
        Integer toRow   = numbers.get(toRowChar);

        if (fromCol == null || fromRow == null || toCol == null || toRow == null) {
            throw new IllegalArgumentException(
                    "Invalid chess coordinates: '" + fromPosition + "' -> '" + toPosition + "'."
            );
        }

        ChessPosition fromObj = new ChessPosition(fromRow, fromCol);
        ChessPosition toObj   = new ChessPosition(toRow, toCol);

        ChessPiece.PieceType pieceType = null;
        // use equals to compare strings
        if ((toRow == 8 && "WHITE".equals(teamColor)) || (toRow == 1 && "BLACK".equals(teamColor))){
            Scanner scanner = new Scanner(System.in);
            System.out.print("What piece would you like to promote to: ");
            pieceType = ChessPiece.PieceType.valueOf(scanner.nextLine().strip().toUpperCase());
        }

        return new ChessMove(fromObj, toObj, pieceType);
    }

}
