package ui;

import java.util.*;

import datamodel.AuthData;
import datamodel.ReturnGameData;
import server.ServerFacade;

public class Client {
    ServerFacade server;

    Boolean loggedIn;
    Boolean inGame;
    AuthData authData;

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
       server = new ServerFacade(serverUrl);
    }

    public void run(){
        Scanner scanner = new Scanner(System.in);
        label:
        while (true){
            if (inGame){
                System.out.print("[PLAYING] >>> ");
            } else if (loggedIn){
                System.out.print("[LOGGED IN] >>> ");
                String input = scanner.nextLine();
                String[] tokens = input.split("\\s+");
                if (tokens.length > 0){
                    String command = tokens[0];
                    switch (command) {
                        case ("quit"):
                            break label;
                        case ("logout"):
                            logout();
                            break;
                        case("create"):
                            createGame(tokens);
                            break;
                        case("list"):
                            listGames(tokens);
                            break;
                        case("play game"):
                            playGame(tokens);
                            break;
                        case("observe game"):
                            observeGame(tokens);
                            break;
                        default:
                            System.out.println(
                                    """
                                            logout - logs you out\s
                                            create game <GAME NAME> - creates game\s
                                            list games - list active games
                                            play game <ID> [WHITE|BLACK]- lets you join game and specifies color
                                            observe game <ID>
                                            quit - to exit
                                            help - see options""");
                    }
                }
            } else {
                System.out.print("[LOGGED OUT] >>> ");
                String input = scanner.nextLine();
                String[] tokens = input.split("\\s+");
                if (tokens.length > 0){
                    String command = tokens[0];
                    switch (command) {
                        case ("quit"):
                            break label;
                        case ("login"):
                            login(tokens);
                            break;
                        case("register"):
                            register(tokens);
                            break;
                        default:
                            System.out.println(
                                    """
                                            register <USERNAME> <EMAIL> <PASSWORD> - create an account\s
                                            login <USERNAME> <PASSWORD> - to log into an existing account\s
                                            quit - to exit
                                            help - see options""");
                    }
                }
            }
        }
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
                System.out.println(ex.getMessage());
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
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
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
            System.out.println("Game ID | Game Name");
            for (ReturnGameData game : gameList) {
                System.out.printf("  %d:    %s%n", game.gameID(), game.gameName());
            }

        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        }
    }

    private void playGame(String[] tokens){
        if (tokens.length != 3){
            System.out.println("Invalid number of arguments. Usage: play game <ID> [WHITE|BLACK]");
        } else {
            String id = tokens[1];
            String color = tokens[2];
            try {
                // call to server to play game
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }
    }

    private void observeGame(String[] tokens){
        if (tokens.length != 2){
            System.out.println("Invalid number of arguments. Usage: observe game <ID> ");
        } else{
            String id = tokens[1];
            try{
                // call to server to observe game
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }

    }

}
