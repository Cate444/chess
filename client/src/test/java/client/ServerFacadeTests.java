package client;

import chess.ChessGame;
import datamodel.AuthData;
import datamodel.ReturnGameData;
import datamodel.UserData;
import org.junit.jupiter.api.*;
import server.Server;
import server.ServerFacade;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;


public class ServerFacadeTests {

    private static Server server;
    private static final ServerFacade serverFacade = new ServerFacade("http://localhost:8080");

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
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
        assertDoesNotThrow(serverFacade::clear);
    }

    @Test
    public void clearBad() throws Exception{
        AuthData userData = serverFacade.register("u", "e", "p");
        serverFacade.logout(userData.authToken());
        serverFacade.login("u", "p");
        assertDoesNotThrow(serverFacade::clear);
    }

    @Test
    public void register() throws Exception{
        serverFacade.clear();
        AuthData userData = serverFacade.register("u", "e", "p");
        assertEquals( "u", userData.username());
    }

    @Test
    public void registerAgain() throws Exception{
        serverFacade.clear();
        serverFacade.register("Tyler", "the", "best");
        assertThrows(java.lang.Exception.class, ()-> serverFacade.register("Tyler", "the", "best"));
    }

    @Test
    public void logout() throws Exception{
        serverFacade.clear();
        AuthData userData = serverFacade.register("Tyler", "the", "best");
        assertDoesNotThrow(()->serverFacade.logout(userData.authToken()));
    }

    @Test
    public void logoutWrongAuthToken() throws Exception{
        serverFacade.clear();
        AuthData userData = serverFacade.register("Tyler", "the", "best");
        assertThrows(java.lang.Exception.class, ()->serverFacade.logout(userData.username()));
    }

    @Test
    public void login() throws Exception{
        serverFacade.clear();
        AuthData userData = serverFacade.register("Tyler", "the", "best");
        serverFacade.logout(userData.authToken());
        assertDoesNotThrow(()->serverFacade.login("Tyler", "best"));
    }

    @Test
    public void loginNonexistentUser() throws Exception{
        serverFacade.clear();
        assertThrows(java.lang.Exception.class,()->serverFacade.login("Tyler", "best"));
    }

    @Test
    public void createGame() throws Exception{
        serverFacade.clear();
        AuthData userData = serverFacade.register("Tyler", "the", "best");
        assertDoesNotThrow(()-> serverFacade.createGame(userData.authToken(), "Agame"));
    }

    @Test
    public void createBadGame() throws Exception{
        serverFacade.clear();
        assertThrows(Exception.class,()-> serverFacade.createGame("athToken", "Agame"));
    }

    @Test
    public void listGame() throws Exception{
        serverFacade.clear();
        AuthData userData = serverFacade.register("Tyler", "the", "best");
        Map<String, Object> gameList = serverFacade.listGames(userData.authToken());
        assertEquals(1, gameList.size());
    }

    @Test
    public void listGamesUnauthorized() throws Exception{
        serverFacade.clear();
        assertThrows(Exception.class ,()->serverFacade.listGames("token"));
    }

    @Test
    public void joinGame() throws Exception{
        serverFacade.clear();
        AuthData userData = serverFacade.register("Tyler", "the", "best");
        serverFacade.createGame(userData.authToken(), "Agame");
        Map<String, Object> gameList = serverFacade.listGames(userData.authToken());
        ArrayList<ReturnGameData> gameDatas = (ArrayList<ReturnGameData>) gameList.get("games");
        ReturnGameData gameData = gameDatas.get(0);
        assertDoesNotThrow(()->serverFacade.joinGame(userData.authToken(), gameData.gameID(), ChessGame.TeamColor.WHITE));
    }

    @Test
    public void joinGamesUnauthorized() throws Exception{
        serverFacade.clear();
        AuthData userData = serverFacade.register("Tyler", "the", "best");
        serverFacade.createGame(userData.authToken(), "Agame");
        Map<String, Object> gameList = serverFacade.listGames(userData.authToken());
        ArrayList<ReturnGameData> gameDatas = (ArrayList<ReturnGameData>) gameList.get("games");
        ReturnGameData gameData = gameDatas.get(0);
        assertThrows(Exception.class ,()->serverFacade.joinGame("token", gameData.gameID(), ChessGame.TeamColor.WHITE));
    }
}
