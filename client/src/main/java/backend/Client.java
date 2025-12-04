package backend;

import java.util.*;
import chess.*;
import datamodel.*;
import server.ServerFacade;
import ui.RenderBoard;
import websocket.messages.*;

public class Client implements ServerMessageObserver {
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
            'a', 1,'b', 2,'c', 3,'d', 4,'e', 5,'f', 6,'g', 7,'h', 8);
    private final Map<Character, Integer> numbers = Map.of(
            '1', 1,'2', 2,'3', 3,'4', 4,'5', 5,'6',6,'7', 7, '8', 8);

    public Client(String serverUrl) throws Exception {
        loggedIn = false;
        inGame = false;
        observing = false;
        serverURL = serverUrl;
        server = new ServerFacade(serverUrl);
        ws = new WebSocketFacade(serverUrl, this);
    }

    public void notifyError(ErrorMessage message){
        if (message.getServerMessageType() == ServerMessage.ServerMessageType.ERROR) {
            System.out.println(message.getMessage());
        } else {
            System.out.println(message);
        }
    }

    public void notifyNotification(NotificationMessage message){
        if (message.getServerMessageType() == ServerMessage.ServerMessageType.NOTIFICATION) {
            System.out.println(message.message);
        } else {
            System.out.println(message);
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
        if (tokens.length == 0) {
            return true;
        }
        String command = tokens[0];

        switch (command) {
            case "menu" -> inGame = false;
            case "quit" -> { return false; }
            case "redraw" -> redraw(tokens);
            case "leave" -> leave(tokens);
            case "move" -> move(tokens);
            case "resign" -> resign(tokens);
            case "highlight" -> highlight(tokens);
            default -> System.out.println("""
                redraw - redraws board
                leave - lets you leave the game
                move - make a move
                resign - resign the game
                highlight moves <POSITION> - show possible moves
                menu - return to login state
                quit - exit
                help - see options""");
        }
        return true;
    }

    private boolean handleObserving(Scanner scanner) {
        System.out.print("[OBSERVING] >>> ");
        String[] tokens = scanner.nextLine().split("\\s+");
        if (tokens.length == 0) {
            return true;
        }
        String command = tokens[0];

        switch (command) {
            case "menu" -> observing = false;
            case "quit" -> { return false; }
            case "leave" -> leave(tokens);
            case "highlight" -> highlight(tokens);
            default -> System.out.println("""
                highlight moves <POSITION> 
                leave - leave the game
                menu - return to login state
                quit - exit
                help - see options""");
        }
        return true;
    }

    private boolean handleLoggedIn(Scanner scanner) {
        System.out.print("[LOGGED IN] >>> ");
        String[] tokens = scanner.nextLine().split("\\s+");
        if (tokens.length == 0) {
            return true;
        }
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
                create game <NAME> - create game
                list games - list active games
                play <ID> [WHITE|BLACK] - join a game
                observe <ID> - observe a game
                quit - exit
                help - see options""");
        }
        return true;
    }

    private boolean handleLoggedOut(Scanner scanner) {
        System.out.print("[LOGGED OUT] >>> ");
        String[] tokens = scanner.nextLine().split("\\s+");
        if (tokens.length == 0) {
            return true;
        }
        String command = tokens[0];

        switch (command) {
            case "quit" -> { return false; }
            case "login" -> login(tokens);
            case "register" -> register(tokens);
            default -> System.out.println("""
                register <USERNAME> <EMAIL> <PASSWORD>
                login <USERNAME> <PASSWORD>
                quit - exit
                help - see options""");
        }
        return true;
    }

    private void register(String[] tokens){
        if (tokens.length != 4) {
            System.out.println("Usage: register <USERNAME> <PASSWORD> <EMAIL>");
            return;
        }
        try {
            authData = server.register(tokens[1], tokens[2], tokens[3]);
            loggedIn = true;
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    private void login(String[] tokens){
        if (tokens.length != 3) {
            System.out.println("Usage: login <USERNAME> <PASSWORD>");
            return;
        }
        try {
            authData = server.login(tokens[1], tokens[2]);
            loggedIn = true;
        } catch (Exception ex) {
            if (ex.getMessage().contains("unauthorized")) {
                System.out.println("Username doesn't exist");
            } else {
                System.out.println("internal server error");
            }
        }
    }

    private void logout(){
        try {
            server.logout(authData.authToken());
            loggedIn = false;
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    private void createGame(String[] tokens){
        if (tokens.length != 3) {
            System.out.println("Usage: create game <NAME>");
            return;
        }
        try {
            server.createGame(authData.authToken(), tokens[2]);
            System.out.printf("%s has been made%n", tokens[2]);
        } catch (Exception ex){
            printAuthOrInternal(ex);
        }
    }

    private void listGames(String[] tokens){
        if (tokens.length != 2) {
            System.out.println("Usage: list games");
            return;
        }
        try {
            Map<String, Object> games = server.listGames(authData.authToken());
            List<ReturnGameData> gameList = (List<ReturnGameData>) games.get("games");
            System.out.println("Game Name: WhitePlayer, BlackPlayer");
            for (int i = 0; i < gameList.size(); i++) {
                var g = gameList.get(i);
                System.out.printf(" %d) %s: %s, %s%n", i+1, g.gameName(), g.whiteUsername(), g.blackUsername());
            }
        } catch (Exception ex){
            printAuthOrInternal(ex);
        }
    }

    private void playGame(String[] tokens){
        if (tokens.length != 3) {
            System.out.println("Usage: play <ID> [WHITE|BLACK]");
            return;
        }
        if (!tokens[2].equalsIgnoreCase("WHITE") && !tokens[2].equalsIgnoreCase("BLACK")) {
            System.out.println("Invalid color. Use WHITE or BLACK.");
            return;
        }
        try {
            var list = (List<ReturnGameData>) server.listGames(authData.authToken()).get("games");
            int id = list.get(Integer.parseInt(tokens[1]) - 1).gameID();
            teamColor = tokens[2].toUpperCase();
            var color = ChessGame.TeamColor.valueOf(teamColor);
            server.joinGame(authData.authToken(), id, color);
            ws.joinGame(authData.authToken(), id, color);
            inGame = true;
            gameInvolvedIn = id;
        } catch (Exception ex){
            printAuthOrInternal(ex);
        }
    }

    private void observeGame(String[] tokens){
        if (tokens.length != 2) {
            System.out.println("Usage: observe <ID>");
            return;
        }
        try {
            var list = (List<ReturnGameData>) server.listGames(authData.authToken()).get("games");
            int id = list.get(Integer.parseInt(tokens[1]) - 1).gameID();
            ws.observeGame(id, authData.authToken());
            observing = true;
            gameInvolvedIn = id;
        } catch (Exception ex){
            printAuthOrInternal(ex);
        }
    }

    private void leave(String[] tokens){
        if (tokens.length != 1) {
            System.out.println("Usage: leave");
            return;
        }
        try {
            ws.leave(gameInvolvedIn, authData.authToken());
            observing = false;
            inGame = false;
            gameInvolvedIn = 0;
        } catch (Exception ex){
            printAuthOrInternal(ex);
        }
    }

    private void redraw(String[] tokens){
        if (tokens.length != 1) {
            System.out.println("Usage: redraw");
            return;
        }
        try {
            ws.redraw(gameInvolvedIn, authData.authToken());
            //renderBoard.render(teamColor, );
        } catch (Exception ex){
            System.out.println(ex.getMessage());
            printAuthOrInternal(ex);
        }
    }

    private void move(String[] tokens){
        if (tokens.length != 1) {
            System.out.println("Usage: move");
            return;
        }
        List<String> positions = getPositions();
        try {
            ChessMove move = convert(positions.get(0), positions.get(1));
            ws.move(move, gameInvolvedIn, authData.authToken());
        } catch (Exception ex){
            printAuthOrInternal(ex);
        }
    }

    private void highlight(String[] tokens){
        if (tokens.length != 3) {
            System.out.println("Usage: highlight moves <POSITION>");
            return;
        }
        String pos = isCleanPositions(tokens[2], tokens[2]).get(0);
        while (pos == null) {
            System.out.println("Enter column+row (e.g., e5):");
            pos = isCleanPositions(new Scanner(System.in).nextLine(), tokens[2]).get(0);
        }
        Integer col = letters.get(Character.toLowerCase(pos.charAt(0)));
        Integer row = numbers.get(pos.charAt(1));
        try {
            ws.highlight(new ChessPosition(row, col), gameInvolvedIn, authData.authToken());
            //System.out.printf("You would highlight %s%n", pos);
        } catch (Exception ex){
            printAuthOrInternal(ex);
        }
    }

    public void resign(String[] tokens){
        if (tokens.length != 1) {
            System.out.println("Usage: resign");
            return;
        }
        System.out.println("Are you sure (y/n)?");
        String res = new Scanner(System.in).nextLine().strip();
        if (res.equals("y")) {
            try {
                ws.resign(gameInvolvedIn, authData.authToken());
                inGame = false;
                gameInvolvedIn = 0;
            } catch (Exception ex){
                printAuthOrInternal(ex);
            }
        }
    }

    private List<String> getPositions(){
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter column+row (e.g., e5)");
        System.out.print("from: ");
        String from = sc.nextLine().strip();
        System.out.print("to: ");
        String to = sc.nextLine().strip();
        return Arrays.asList(from, to);
    }

    private ArrayList<String> isCleanPositions(String from, String to){
        ArrayList<String> pos = new ArrayList<>();
        if (from == null || to == null) {
            return pos;
        }
        from = from.strip();
        to = to.strip();
        String cf = "", ct = "";

        if (from.length() < 2 || to.length() < 2) {
            return pos;
        }
        if (from.length() > 2) {
            for (char c: from.toCharArray()) {
                if (!Character.isWhitespace(c)) {
                    cf += c;
                }
            }
        } else {
            cf = from;
        }
        if (to.length() > 2) {
            for (char c: to.toCharArray()) {
                if (!Character.isWhitespace(c)) {
                    ct += c;
                }
            }
        } else {
            ct = to;
        }

        if (!Character.isLetter(cf.charAt(0)) || !Character.isDigit(cf.charAt(1))) {
            if (Character.isLetter(cf.charAt(1)) && Character.isDigit(cf.charAt(0))) {
                cf = "" + cf.charAt(1) + cf.charAt(0);
            } else {
                return pos;
            }
        }
        if (!Character.isLetter(ct.charAt(0)) || !Character.isDigit(ct.charAt(1))) {
            if (Character.isLetter(ct.charAt(1)) && Character.isDigit(ct.charAt(0))) {
                ct = "" + ct.charAt(1) + ct.charAt(0);
            } else {
                return pos;
            }
        }

        if ("abcdefgh".indexOf(ct.charAt(0)) == -1) {
            return pos;
        }
        if ("12345678".indexOf(ct.charAt(1)) == -1) {
            return pos;
        }

        pos.add(cf);
        pos.add(ct);
        return pos;
    }

    private ChessMove convert(String from, String to) throws Exception{
        ArrayList<String> pos = isCleanPositions(from, to);
        while (pos.size() == 0) {
            List<String> raw = getPositions();
            pos = isCleanPositions(raw.get(0), raw.get(1));
        }
        from = pos.get(0);
        to = pos.get(1);

        char fCol = Character.toLowerCase(from.charAt(0));
        char fRow = from.charAt(1);
        char tCol = Character.toLowerCase(to.charAt(0));
        char tRow = to.charAt(1);

        Integer fc = letters.get(fCol), fr = numbers.get(fRow),
                tc = letters.get(tCol), tr = numbers.get(tRow);

        ChessPosition fp = new ChessPosition(fr, fc);
        ChessPosition tp = new ChessPosition(tr, tc);

        ChessPiece.PieceType promo = null;
        ChessBoard board =  ws.getCurrentChessBoard();
        ChessPiece piece = board.getPiece(new ChessPosition(fr,fc));
        if ((tr == 8 && "WHITE".equals(teamColor)) && piece.getPieceType().equals(ChessPiece.PieceType.PAWN)  || (tr == 1 && "BLACK".equals(teamColor) && piece.getPieceType().equals(ChessPiece.PieceType.PAWN))) {
            System.out.print("Promote to: ");
            promo = ChessPiece.PieceType.valueOf(new Scanner(System.in).nextLine().strip().toUpperCase());
        }
        return new ChessMove(fp, tp, promo);
    }

    private void printAuthOrInternal(Exception ex){
        if (ex.getMessage().contains("unauthorized")) {
            System.out.println("you aren't authorized");
        } else {
            System.out.println("internal server error");
        }
    }
}
