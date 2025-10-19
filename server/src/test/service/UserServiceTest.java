package service;

import dataaccess.DataAccess;
import dataaccess.MemoryDataAccess;
import datamodel.GameName;
import datamodel.UserData;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {

   @Test
   void clear(){

   }

    @Test
    void register() throws Exception {
        DataAccess db = new MemoryDataAccess();
        UserService service = new UserService(db);
        var user = new UserData("joe", "j@j.com", "passThisWord");
        var authData = service.register(user);
        assertNotNull(authData);
        assertEquals(user.username(), authData.username());
        assertTrue(!authData.authToken().isEmpty());
    }

    @Test
    void registerInvalidUsername() throws Exception{
        DataAccess db = new MemoryDataAccess();
        UserService service = new UserService(db);

        var userWithNoName = new UserData(null, "j@j.com", "passThisWord");
        Exception exception = assertThrows(Exception.class, () -> {
            service.register(userWithNoName);
        });
        assertEquals("no username", exception.getMessage());

        var userWithNoPassword = new UserData("Jane", "j@j.com", null);
        Exception exception2 = assertThrows(Exception.class, () -> {
            service.register(userWithNoPassword);
        });
        assertEquals("no password", exception2.getMessage());


        var goodUser = new UserData("Jane", "j@j.com", "ThisIsAPassword");
        service.register(goodUser);
        var userAlreadyExists = new UserData("Jane", "j@j.com", "ThisIsAPassword");;
        Exception exception3 = assertThrows(Exception.class, () -> {
            service.register(userAlreadyExists);
        });
        assertEquals("Already exists", exception3.getMessage());
    }

    @Test
    void login() throws Exception {
        DataAccess db = new MemoryDataAccess();
        UserService service = new UserService(db);
        var user = new UserData("joe", "j@j.com", "passThisWord");
        service.register(user);
        var authData = service.login(user);
        assertEquals(user.username(), authData.username());
        assertFalse(authData.authToken().isEmpty());
    }

    @Test
    void loginInvalidUser() throws Exception {
        DataAccess db = new MemoryDataAccess();
        UserService service = new UserService(db);
        var userDoesntExist = new UserData("joe", "j@j.com", "passThisWord");
        Exception exception = assertThrows(Exception.class, () -> service.login(userDoesntExist));
        assertEquals("user doesnt exist", exception.getMessage());

       service.register(userDoesntExist);

        var userWithWrongPassword = new UserData("joe", "j@j.com", "notThisPassWord");
        assertThrows(Exception.class, () -> service.login(userWithWrongPassword));

    }

    @Test
    void logout() throws Exception{
        DataAccess db = new MemoryDataAccess();
        UserService service = new UserService(db);
        var user = new UserData("joe", "j@j.com", "passThisWord");
        service.register(user);
        var authData = service.login(user);
        assertDoesNotThrow(() -> service.logout(authData.authToken()));
    }

    @Test
    void logoutTwice() throws Exception{
        DataAccess db = new MemoryDataAccess();
        UserService service = new UserService(db);
        var user = new UserData("joe", "j@j.com", "passThisWord");
        service.register(user);
        var authData = service.login(user);
        service.logout(authData.authToken());
        assertThrows(Exception.class, () -> service.logout(authData.authToken()));
    }

    @Test
    void createGame() throws Exception {
        DataAccess db = new MemoryDataAccess();
        UserService userService = new UserService(db);
        GameService gameService = new GameService(db);
        var user = new UserData("joe", "j@j.com", "passThisWord");
        userService.register(user);
        var authData = userService.login(user);
        int gameID = gameService.createGame(authData.authToken(), new GameName("aGame"));
        assertTrue(gameID > 0);
    }

    @Test
    void badDataCreateGame() throws Exception{
        DataAccess db = new MemoryDataAccess();
        UserService userService = new UserService(db);
        GameService gameService = new GameService(db);
        UserData user = new UserData("Eva", "Eva@faith.com", "theBabes");
        userService.register(user);
        var authData = userService.login(user);
        assertThrows(Exception.class,() -> gameService.createGame(authData.authToken(), new GameName(null)));
    }

}