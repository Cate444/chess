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
    private static final ServerFacade SERVER_FACADE = new ServerFacade("http://localhost:8080");

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
        assertDoesNotThrow(SERVER_FACADE::clear);
    }

    @Test
    public void clearBad() throws Exception{
        AuthData userData = SERVER_FACADE.register("u", "e", "p");
        SERVER_FACADE.logout(userData.authToken());
        SERVER_FACADE.login("u", "p");
        assertDoesNotThrow(SERVER_FACADE::clear);
    }

    @Test
    public void register() throws Exception{
        SERVER_FACADE.clear();
        AuthData userData = SERVER_FACADE.register("u", "e", "p");
        assertEquals( "u", userData.username());
    }

    @Test
    public void registerAgain() throws Exception{
        SERVER_FACADE.clear();
        SERVER_FACADE.register("Tyler", "the", "best");
        assertThrows(java.lang.Exception.class, ()-> SERVER_FACADE.register("Tyler", "the", "best"));
    }

    @Test
    public void logout() throws Exception{
        SERVER_FACADE.clear();
        AuthData userData = SERVER_FACADE.register("Tyler", "the", "best");
        assertDoesNotThrow(()-> SERVER_FACADE.logout(userData.authToken()));
    }

    @Test
    public void logoutWrongAuthToken() throws Exception{
        SERVER_FACADE.clear();
        AuthData userData = SERVER_FACADE.register("Tyler", "the", "best");
        assertThrows(java.lang.Exception.class, ()-> SERVER_FACADE.logout(userData.username()));
    }

    @Test
    public void login() throws Exception{
        SERVER_FACADE.clear();
        AuthData userData = SERVER_FACADE.register("Tyler", "the", "best");
        SERVER_FACADE.logout(userData.authToken());
        assertDoesNotThrow(()-> SERVER_FACADE.login("Tyler", "best"));
    }

    @Test
    public void loginNonexistentUser() throws Exception{
        SERVER_FACADE.clear();
        assertThrows(java.lang.Exception.class,()-> SERVER_FACADE.login("Tyler", "best"));
    }

    @Test
    public void createGame() throws Exception{
        SERVER_FACADE.clear();
        AuthData userData = SERVER_FACADE.register("Tyler", "the", "best");
        assertDoesNotThrow(()-> SERVER_FACADE.createGame(userData.authToken(), "Agame"));
    }

    @Test
    public void createBadGame() throws Exception{
        SERVER_FACADE.clear();
        assertThrows(Exception.class,()-> SERVER_FACADE.createGame("athToken", "Agame"));
    }

    @Test
    public void listGame() throws Exception{
        SERVER_FACADE.clear();
        AuthData userData = SERVER_FACADE.register("Tyler", "the", "best");
        Map<String, Object> gameList = SERVER_FACADE.listGames(userData.authToken());
        assertEquals(1, gameList.size());
    }

    @Test
    public void listGamesUnauthorized() throws Exception{
        SERVER_FACADE.clear();
        assertThrows(Exception.class ,()-> SERVER_FACADE.listGames("token"));
    }

    @Test
    public void joinGame() throws Exception{
        SERVER_FACADE.clear();
        AuthData userData = SERVER_FACADE.register("Tyler", "the", "best");
        SERVER_FACADE.createGame(userData.authToken(), "Agame");
        Map<String, Object> gameList = SERVER_FACADE.listGames(userData.authToken());
        ArrayList<ReturnGameData> gameDatas = (ArrayList<ReturnGameData>) gameList.get("games");
        ReturnGameData gameData = gameDatas.get(0);
        assertDoesNotThrow(()-> SERVER_FACADE.joinGame(userData.authToken(), gameData.gameID(), ChessGame.TeamColor.WHITE));
    }

    @Test
    public void joinGamesUnauthorized() throws Exception{
        SERVER_FACADE.clear();
        AuthData userData = SERVER_FACADE.register("Tyler", "the", "best");
        SERVER_FACADE.createGame(userData.authToken(), "Agame");
        Map<String, Object> gameList = SERVER_FACADE.listGames(userData.authToken());
        ArrayList<ReturnGameData> gameDatas = (ArrayList<ReturnGameData>) gameList.get("games");
        ReturnGameData gameData = gameDatas.get(0);
        assertThrows(Exception.class ,()-> SERVER_FACADE.joinGame("token", gameData.gameID(), ChessGame.TeamColor.WHITE));
    }
}
