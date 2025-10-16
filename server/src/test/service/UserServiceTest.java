package service;

import dataaccess.DataAccess;
import dataaccess.MemoryDataAccess;
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
    void registerInvalidUsername() {
        DataAccess db = new MemoryDataAccess();
        UserService service = new UserService(db);

        var user = new UserData(null, "j@j.com", "passThisWord");
        Exception exception = assertThrows(Exception.class, () -> {
            service.register(user);
        });
        assertEquals("no username", exception.getMessage());

        var user2 = new UserData("Jane", "j@j.com", null);
        Exception exception2 = assertThrows(Exception.class, () -> {
            service.register(user2);
        });
        assertEquals("no password", exception2.getMessage());
    }

    @Test
    void login() throws Exception {
        DataAccess db = new MemoryDataAccess();
        UserService service = new UserService(db);
        var user = new UserData("joe", "j@j.com", "passThisWord");
        var authData = service.register(user);
    }
}