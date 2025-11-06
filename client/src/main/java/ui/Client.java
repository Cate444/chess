package ui;

import java.util.Map;
import java.util.Scanner;

public class Client {
    Boolean loggedIn;
    Boolean InGame;
    String authToken;

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

    public Client() {
       loggedIn = false;
       InGame = false;


    }

    public void run(){
        Scanner scanner = new Scanner(System.in);

        label:
        while (true){
            if (InGame){
                System.out.print("[PLAYING] >>>");
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
                        case("help"):
                            System.out.println(
                                    "register <USERNAME> <EMAIL> <PASSWORD> - create an account \n" +
                                            "login <USERNAME> <PASSWORD> - to log into an existing account \n" +
                                            "quit - to exit\n" +
                                            "help - see options" );
                    }
                }
            } else if (loggedIn){
                System.out.print("[LOGGED IN] >>>");
                String input = scanner.nextLine();
                String[] tokens = input.split("\\s+");
                if (tokens.length > 0){
                    String command = tokens[0];
                    switch (command) {
                        case ("quit"):
                            break label;
                        case ("logout"):
                            logout(tokens);
                            break;
                        case("create game"):
                            createGame(tokens);
                            break;
                        case("list games"):
                            listGames(tokens);
                            break;
                        case("play game"):
                            playGame(tokens);
                            break;
                        case("observe game"):
                            observeGame(tokens);
                            break;
                        case("help"):
                            System.out.println(
                                    "logout - logs you out \n" +
                                            "create game <GAME NAME> - creates game \n" +
                                            "list games - list active games" +
                                            "play game <ID> [WHITE|BLACK]- lets you join game and specifies color" +
                                            "observe game <ID> " +
                                            "quit - to exit\n" +
                                            "help - see options" );
                    }
                }
            } else {
                System.out.print("[LOGGED OUT] >>>");
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
                // call to server to register
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
                // call to server to login
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }
    }

    private void logout(String[] tokens){
        try{
            // call server to logout
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    private void createGame(String[] tokens){
        if (tokens.length != 2){
            System.out.println("Invalid number of arguments. Usage: create game <GAME NAME>");
        } else {
            String gameName = tokens[1];
            try{
                // call to server to create game
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }
    }

    private void listGames(String[] tokens){
        if (tokens.length != 2){
            System.out.println("Invalid number of arguments. Usage: list games <ID>");
        } else {
        String id = tokens[1];
        try{
            // call to server to list games
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
            System.out.println("Invalid number of arguments. Usage: abserve game <ID> ");
        } else{
            String id = tokens[1];
            try{
                // call to server to abserve game
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }

    }

}
