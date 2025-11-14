package ui;

import java.util.*;

import chess.ChessGame;
import datamodel.AuthData;
import datamodel.ReturnGameData;
import server.ServerFacade;

public class Client {
    ServerFacade server;

    Boolean loggedIn;
    Boolean inGame;
    Boolean observing;
    AuthData authData;
    RenderBoard renderBoard = new RenderBoard();
    String teamColor;

    private final Map<String, Integer> letters = Map.of(
            "a" , 1,
            "b", 2,
            "c", 3,
            "d", 4,
            "e", 5,
            "f", 6,
            "g", 7,
            "h", 8
    );

    public Client(String serverUrl) {
       loggedIn = false;
       inGame = false;
       observing = false;
       server = new ServerFacade(serverUrl);
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
            case "leave" -> inGame = false;
            case "make" -> move(tokens);
            case "resign" -> {}
            case "highlight" -> {}
            default -> System.out.println("""
                    redraw - redraws board
                    leave - lets you leave the game
                    make move - makes a move
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
            default -> System.out.println("""
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
            System.out.println("Invalid number of arguments. Usage: play game <ID> [WHITE|BLACK]");
        } else if (!(Objects.equals(tokens[2].toUpperCase(), "WHITE") | Objects.equals(tokens[2].toUpperCase(), "BLACK"))){
            System.out.println("Invalid argument. Usage: play game <ID> [WHITE|BLACK]");
        } else {
            int id = Integer.parseInt(tokens[1]);
            teamColor = tokens[2].toUpperCase();
            ChessGame.TeamColor color = ChessGame.TeamColor.valueOf(tokens[2].toUpperCase());
            try {
                server.joinGame(authData.authToken(), id, color);
                renderBoard.render(tokens[2]);
                inGame = true;
            } catch (Exception ex) {
                switch (ex.getMessage()){
                    case "body exception: {\"message\":\"Error: unauthorized\"}" -> System.out.println("you aren't authorized");
                    case "body exception: {\"message\":\"Error: team already has player\"}" -> System.out.println("team already has player");
                    default -> System.out.println("internal server error");
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
                renderBoard.render("WHITE");
                observing = true;
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
        System.out.println("play");
    }

}
