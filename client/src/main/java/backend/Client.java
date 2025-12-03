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
import websocket.messages.ErrorMessage;
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
            NotificationMessage notificationMessage = (NotificationMessage) message;
            System.out.println(notificationMessage.message);
        } else {
            System.out.println(message.toString());
        }
    }

    public void notifyError(ErrorMessage message){
        if (message.getServerMessageType() == ServerMessage.ServerMessageType.ERROR){
            System.out.println(message.getMessage());
        } else {
            System.out.println(message.toString());
        }
    }

    public void notifyNotification(NotificationMessage message){
        if (message.getServerMessageType() == ServerMessage.ServerMessageType.NOTIFICATION){
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
            case "resign" -> resign(tokens);
            case "highlight" -> highlight(tokens);
            default -> System.out.println("""
                    redraw - redraws board
                    leave - lets you leave the game
                    move - starts process of making move you'll be asked from where to where
                    resign - removes you from current game then leaves
                    highlight moves <POSITION> - shows possible for piece in that square
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
            for(int i =0; i < gameList.size(); i++) {
                System.out.printf(" %d) %s: %s, %s%n", i+1,gameList.get(i).gameName(), gameList.get(i).whiteUsername(), gameList.get(i).blackUsername());
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

    private void playGame(String[] tokens)  {
        if (tokens.length != 3){
            System.out.println("Invalid number of arguments. Usage: play <ID> [WHITE|BLACK]");
        } else if (!(Objects.equals(tokens[2].toUpperCase(), "WHITE") | Objects.equals(tokens[2].toUpperCase(), "BLACK"))){
            System.out.println("Invalid argument. Usage: play <ID> [WHITE|BLACK]");
        } else {
            try {
                Map<String, Object> games = server.listGames(authData.authToken());
                List<ReturnGameData> gameList = (List<ReturnGameData>) games.get("games");
                int index = Integer.parseInt(tokens[1]) - 1;
                int id = gameList.get(index).gameID();
                teamColor = tokens[2].toUpperCase();
                ChessGame.TeamColor color = ChessGame.TeamColor.valueOf(tokens[2].toUpperCase());
                server.joinGame(authData.authToken(), id, color);
                ws.joinGame(authData.authToken(), id, color);
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
            try{
                Map<String, Object> games = server.listGames(authData.authToken());
                List<ReturnGameData> gameList = (List<ReturnGameData>) games.get("games");
                int index = Integer.parseInt(tokens[1]) - 1;
                int id = gameList.get(index).gameID();
                ws.observeGame(id, authData.authToken());
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
        try {
            if (tokens.length != 1) {
                System.out.println("Invalid number of arguments. Usage: redraw ");
            } else {
                ws.redraw(gameInvolvedIn, authData.authToken());
                renderBoard.render(teamColor);
            }
        } catch (Exception ex){
            if (ex.getMessage().equals("body exception: {\"message\":\"Error: unauthorized\"}")) {
                System.out.println("you aren't authorized");
            } else {
                System.out.println("internal server error");
            }
        }
    }

    private void move(String[] tokens){
        if (tokens.length != 1){
            System.out.println("Invalid number of arguments. Usage: observe game <ID> ");
        } else{
            List<String> positions = getPositions();
            String fromPosition = positions.get(0);
            String toPosition = positions.get(1);
            ChessMove chessMove = convert(fromPosition, toPosition);
            try{
                ws.move(chessMove, gameInvolvedIn, authData.authToken());
            } catch (Exception ex) {
                if (ex.getMessage().equals("body exception: {\"message\":\"Error: unauthorized\"}")) {
                    System.out.println("you aren't authorized");
                } else {
                    System.out.println("internal server error");
                }
            }
        }
    }

    private void highlight(String[] tokens){
        if( tokens.length != 3){
            System.out.println("Invalid number of arguments. Usage: highlight moves <POSITION>");
        }
        String position = isCleanPositions(tokens[2], tokens[2]).get(0);
        while (position == null){
            Scanner scanner = new Scanner(System.in);
            System.out.println("Please input colum then row ex. e5 ");
            String rawPosition = scanner.nextLine().strip();
            position = isCleanPositions(rawPosition, rawPosition).get(0);
        }
        Integer col = letters.get(Character.toLowerCase(position.charAt(0)));
        Integer row = numbers.get(position.charAt(1));
        ChessPosition chessPosition = new ChessPosition(row, col);
        try{
            ws.highlight(chessPosition, gameInvolvedIn, authData.authToken());
            System.out.printf("You would highlight position %s", position);
        }catch (Exception ex){
            if (ex.getMessage().equals("body exception: {\"message\":\"Error: unauthorized\"}")) {
                System.out.println("you aren't authorized");
            } else {
                System.out.println("internal server error");
            }
        }
    }

    public void resign(String[] tokens){
        if( tokens.length != 1){
            System.out.println("Invalid number of arguments. Usage: resign");
        }
        try{
            System.out.println("Are you sure you want to resign: y or n");
            Scanner scanner = new Scanner(System.in);
            String response = scanner.nextLine().strip();
            if (response == "y"){
                ws.resign(gameInvolvedIn, authData.authToken());
            }
        }catch (Exception ex){
            if (ex.getMessage().equals("body exception: {\"message\":\"Error: unauthorized\"}")) {
                System.out.println("you aren't authorized");
            } else {
                System.out.println("internal server error");
            }
        }
    }

    private List<String> getPositions(){
        System.out.println("Please input colum then row ex. e5");
        Scanner scanner = new Scanner(System.in);
        System.out.print("from: ");
        String fromPosition = scanner.nextLine().strip();
        System.out.print("to : ");
        String toPosition = scanner.nextLine().strip();
        ArrayList<String> returnList = new ArrayList<>();
        returnList.add(0, fromPosition);
        returnList.add(toPosition);
        return  returnList;
    }

    private ArrayList<String> isCleanPositions(String fromPosition, String toPosition){
        ArrayList<String> positions = new ArrayList<>();
        if (fromPosition == null || toPosition == null) {
            return positions;
        }

        fromPosition = fromPosition.strip();
        toPosition   = toPosition.strip();

        String cleanFromPosition = "";
        String cleanToPosition = "";

        //checks length and takes out white space
        if (fromPosition.length() < 2 || toPosition.length() < 2) {
            return positions;
        } else if (fromPosition.length() > 2 || toPosition.length() > 2){
            for (int i= 0; i<fromPosition.length(); i++){
                if (!Character.isWhitespace(fromPosition.charAt(i)) ){
                    cleanFromPosition += fromPosition.charAt(i);
                }
            }
            for (int i= 0; i<toPosition.length(); i++){
                if (!Character.isWhitespace(toPosition.charAt(i)) ){
                    cleanToPosition += toPosition.charAt(i);
                }
            }
        } else{
            cleanFromPosition = fromPosition;
            cleanToPosition = toPosition;

        }
        // check letter then digit
        if(!Character.isLetter(cleanFromPosition.charAt(0)) || !Character.isDigit(cleanFromPosition.charAt(1))){
            if(Character.isLetter(cleanFromPosition.charAt(1)) || Character.isDigit(cleanFromPosition.charAt(0))){
                cleanFromPosition = String.valueOf(cleanFromPosition.charAt(1)) + cleanFromPosition.charAt(0);
            } else {
                System.out.println("positions must be like \"a2\"");
                return positions;
            }
        }
        if (!Character.isLetter(cleanToPosition.charAt(0)) || !Character.isDigit(cleanToPosition.charAt(1))){
            if(Character.isLetter(cleanToPosition.charAt(1)) || Character.isDigit(cleanToPosition.charAt(0))){
                cleanToPosition = String.valueOf(cleanToPosition.charAt(1)) + String.valueOf(cleanToPosition.charAt(0));
            } else {
                System.out.println("positions must be like \"a2\"");
                return positions;
            }
        }

        //check that position is within index
        String letts = "abcdegfh";
        String nums = "12345678";
        if (letts.indexOf(cleanToPosition.charAt(0)) == -1){
            System.out.println("pick column a. b. c. d. e. f. g. or h");
            return positions;
        }
        if (nums.indexOf(cleanToPosition.charAt(1)) == -1){
            System.out.println("pick row 1. 2. 3. 4. 5. 6. 7. or 8");
            return positions;
        }

        positions.add(0, cleanFromPosition);
        positions.add(1, cleanToPosition);
        return positions;
    }


    private ChessMove convert(String fromPosition, String toPosition){
        // trim & basic validation
        ArrayList<String> positions = isCleanPositions(fromPosition, toPosition);
        while (positions.size() == 0){
            List<String> rawPositions = getPositions();
            fromPosition = rawPositions.get(0);
            toPosition = rawPositions.get(1);
            positions = isCleanPositions(fromPosition, toPosition);
        }
        fromPosition = positions.get(0);
        toPosition = positions.get(1);

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
        if ((toRow == 8 && "WHITE".equals(teamColor)) || (toRow == 1 && "BLACK".equals(teamColor))){
            Scanner scanner = new Scanner(System.in);
            System.out.print("What piece would you like to promote to: ");
            pieceType = ChessPiece.PieceType.valueOf(scanner.nextLine().strip().toUpperCase());
        }

        return new ChessMove(fromObj, toObj, pieceType);
    }

}
