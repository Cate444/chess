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
    private static ServerFacade SERVER_FACADE;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        SERVER_FACADE = new ServerFacade("http://localhost:" + port);
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
            SERVER_FACADE.clear();
            Assertions.assertTrue(true);
        } catch (Exception ex) {
            System.out.println(ex);
            Assertions.assertFalse(true);
        }
    }

    @Test
    public void clearBad(){
        try {
        AuthData userData = SERVER_FACADE.register("u", "e", "p");
        SERVER_FACADE.logout(userData.authToken());
        SERVER_FACADE.login("u", "p");
        assertDoesNotThrow(SERVER_FACADE::clear);
        } catch (Exception ex){
            System.out.println(ex);
        }
    }

    @Test
    public void register(){
        try {
        SERVER_FACADE.clear();
        AuthData userData = SERVER_FACADE.register("u", "e", "p");
        assertEquals( "u", userData.username());
        } catch (Exception ex){}
    }

    @Test
    public void registerAgain() {
        try {
        SERVER_FACADE.clear();
        SERVER_FACADE.register("Tyler", "the", "best");
        assertThrows(java.lang.Exception.class, ()-> SERVER_FACADE.register("Tyler", "the", "best"));
        } catch (Exception ex){}
    }

    @Test
    public void logout() {
        try {
        SERVER_FACADE.clear();
        AuthData userData = SERVER_FACADE.register("Tyler", "the", "best");
        assertDoesNotThrow(()-> SERVER_FACADE.logout(userData.authToken()));
        } catch (Exception ex){}
    }

    @Test
    public void logoutWrongAuthToken() {
        try {
        SERVER_FACADE.clear();
        AuthData userData = SERVER_FACADE.register("Tyler", "the", "best");
        assertThrows(java.lang.Exception.class, ()-> SERVER_FACADE.logout(userData.username()));
        } catch (Exception ex){}
    }

    @Test
    public void login() {
        try {
        SERVER_FACADE.clear();
        AuthData userData = SERVER_FACADE.register("Tyler", "the", "best");
        SERVER_FACADE.logout(userData.authToken());
        assertDoesNotThrow(()-> SERVER_FACADE.login("Tyler", "best"));
        } catch (Exception ex){}
    }

    @Test
    public void loginNonexistentUser(){
       try {
           SERVER_FACADE.clear();
           assertThrows(java.lang.Exception.class, () -> SERVER_FACADE.login("Tyler", "best"));
       } catch (Exception ex){}
    }

    @Test
    public void createGame() {
        try {
            SERVER_FACADE.clear();
            AuthData userData = SERVER_FACADE.register("Tyler", "the", "best");
            assertDoesNotThrow(()-> SERVER_FACADE.createGame(userData.authToken(), "Agame"));
        } catch (Exception e) {
        }

    }

    @Test
    public void createBadGame() {
        try {
            SERVER_FACADE.clear();
            assertThrows(Exception.class, () -> SERVER_FACADE.createGame("athToken", "Agame"));
        } catch (Exception e) {
        }
    }

    @Test
    public void listGame() {
        try {
            SERVER_FACADE.clear();
            AuthData userData = SERVER_FACADE.register("Tyler", "the", "best");
            Map<String, Object> gameList = SERVER_FACADE.listGames(userData.authToken());
            assertEquals(1, gameList.size());
        } catch (Exception ex){}

    }

    @Test
    public void listGamesUnauthorized() {
        try {
        SERVER_FACADE.clear();
        assertThrows(Exception.class ,()-> SERVER_FACADE.listGames("token"));
    } catch (Exception ex){}
    }

    @Test
    public void joinGame() {
        try {
            SERVER_FACADE.clear();
            AuthData userData = SERVER_FACADE.register("Tyler", "the", "best");
            SERVER_FACADE.createGame(userData.authToken(), "Agame");
            Map<String, Object> gameList = SERVER_FACADE.listGames(userData.authToken());
            ArrayList<ReturnGameData> gameDatas = (ArrayList<ReturnGameData>) gameList.get("games");
            ReturnGameData gameData = gameDatas.get(0);
            assertDoesNotThrow(() -> SERVER_FACADE.joinGame(userData.authToken(), gameData.gameID(), ChessGame.TeamColor.WHITE));
        } catch (Exception ex){}
    }

    @Test
    public void joinGamesUnauthorized() {
        try {
            SERVER_FACADE.clear();
            AuthData userData = SERVER_FACADE.register("Tyler", "the", "best");
            SERVER_FACADE.createGame(userData.authToken(), "Agame");
            Map<String, Object> gameList = SERVER_FACADE.listGames(userData.authToken());
            ArrayList<ReturnGameData> gameDatas = (ArrayList<ReturnGameData>) gameList.get("games");
            ReturnGameData gameData = gameDatas.get(0);
            assertThrows(Exception.class, () -> SERVER_FACADE.joinGame("token", gameData.gameID(), ChessGame.TeamColor.WHITE));
        } catch (Exception ex){

        }

    }
}
