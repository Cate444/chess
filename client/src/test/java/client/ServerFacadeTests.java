package client;

import chess.ChessGame;
import datamodel.AuthData;
import datamodel.ReturnGameData;
import org.junit.jupiter.api.*;
import server.Server;
import server.ServerFacade;

import java.util.ArrayList;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade serverFacade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        serverFacade = new ServerFacade("http://localhost:" + port);
        System.out.println("Started test HTTP server on " + port);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @Test
    public void sampleTest() {
        Assertions.assertTrue(true);
    }

    @Test
    public void clear() {
        try{
            serverFacade.clear();
            Assertions.assertTrue(true);
        } catch (Exception ex) {
            System.out.println(ex);
            Assertions.assertFalse(true);
        }
    }

    @Test
    public void clearBad(){
        try {
        AuthData userData = serverFacade.register("u", "e", "p");
        serverFacade.logout(userData.authToken());
        serverFacade.login("u", "p");
        assertDoesNotThrow(serverFacade::clear);
        } catch (Exception ex){
            System.out.println(ex);
        }
    }

    @Test
    public void register(){
        try {
        serverFacade.clear();
        AuthData userData = serverFacade.register("u", "e", "p");
        assertEquals( "u", userData.username());
        } catch (Exception ex){}
    }

    @Test
    public void registerAgain() {
        try {
        serverFacade.clear();
        serverFacade.register("Tyler", "the", "best");
        assertThrows(java.lang.Exception.class, ()-> serverFacade.register("Tyler", "the", "best"));
        } catch (Exception ex){}
    }

    @Test
    public void logout() {
        try {
        serverFacade.clear();
        AuthData userData = serverFacade.register("Tyler", "the", "best");
        assertDoesNotThrow(()-> serverFacade.logout(userData.authToken()));
        } catch (Exception ex){}
    }

    @Test
    public void logoutWrongAuthToken() {
        try {
        serverFacade.clear();
        AuthData userData = serverFacade.register("Tyler", "the", "best");
        assertThrows(java.lang.Exception.class, ()-> serverFacade.logout(userData.username()));
        } catch (Exception ex){}
    }

    @Test
    public void login() {
        try {
        serverFacade.clear();
        AuthData userData = serverFacade.register("Tyler", "the", "best");
        serverFacade.logout(userData.authToken());
        assertDoesNotThrow(()-> serverFacade.login("Tyler", "best"));
        } catch (Exception ex){}
    }

    @Test
    public void loginNonexistentUser(){
       try {
           serverFacade.clear();
           assertThrows(java.lang.Exception.class, () -> serverFacade.login("Tyler", "best"));
       } catch (Exception ex){}
    }

    @Test
    public void createGame() {
        try {
            serverFacade.clear();
            AuthData userData = serverFacade.register("Tyler", "the", "best");
            assertDoesNotThrow(()-> serverFacade.createGame(userData.authToken(), "Agame"));
        } catch (Exception e) {
        }

    }

    @Test
    public void createBadGame() {
        try {
            serverFacade.clear();
            assertThrows(Exception.class, () -> serverFacade.createGame("athToken", "Agame"));
        } catch (Exception e) {
        }
    }

    @Test
    public void listGame() {
        try {
            serverFacade.clear();
            AuthData userData = serverFacade.register("Tyler", "the", "best");
            Map<String, Object> gameList = serverFacade.listGames(userData.authToken());
            assertEquals(1, gameList.size());
        } catch (Exception ex){}

    }

    @Test
    public void listGamesUnauthorized() {
        try {
        serverFacade.clear();
        assertThrows(Exception.class ,()-> serverFacade.listGames("token"));
    } catch (Exception ex){}
    }

    @Test
    public void joinGame() {
        try {
            serverFacade.clear();
            AuthData userData = serverFacade.register("Tyler", "the", "best");
            serverFacade.createGame(userData.authToken(), "Agame");
            Map<String, Object> gameList = serverFacade.listGames(userData.authToken());
            ArrayList<ReturnGameData> gameDatas = (ArrayList<ReturnGameData>) gameList.get("games");
            ReturnGameData gameData = gameDatas.get(0);
            assertDoesNotThrow(() -> serverFacade.joinGame(userData.authToken(), gameData.gameID(), ChessGame.TeamColor.WHITE));
        } catch (Exception ex){}
    }

    @Test
    public void joinGamesUnauthorized() {
        try {
            serverFacade.clear();
            AuthData userData = serverFacade.register("Tyler", "the", "best");
            serverFacade.createGame(userData.authToken(), "Agame");
            Map<String, Object> gameList = serverFacade.listGames(userData.authToken());
            ArrayList<ReturnGameData> gameDatas = (ArrayList<ReturnGameData>) gameList.get("games");
            ReturnGameData gameData = gameDatas.get(0);
            assertThrows(Exception.class, () -> serverFacade.joinGame("token", gameData.gameID(), ChessGame.TeamColor.WHITE));
        } catch (Exception ex){

        }

    }
}
