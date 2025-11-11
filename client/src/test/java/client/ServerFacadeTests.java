package client;

import datamodel.AuthData;
import datamodel.UserData;
import org.junit.jupiter.api.*;
import server.Server;
import server.ServerFacade;

import static org.junit.jupiter.api.Assertions.*;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade serverFacade = new ServerFacade("http://localhost:8080");
    private Class<? extends Throwable> Exception;

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
    public void clear() throws Exception{
        assertDoesNotThrow(()-> serverFacade.clear());
    }
    @Test
    public void clearBad() throws Exception{
        AuthData userData = serverFacade.register("u", "e", "p");
        serverFacade.logout(userData.authToken());
        serverFacade.login("u", "p");
        assertDoesNotThrow(()-> serverFacade.clear());
    }

    @Test
    public void register() throws Exception{
        serverFacade.clear();
        AuthData userData = serverFacade.register("u", "e", "p");
        assertEquals(userData.username(), "u");
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

}
