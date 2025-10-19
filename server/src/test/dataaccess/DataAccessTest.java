package dataaccess;

import datamodel.UserData;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DataAccessTest {

    @Test
    void clear() {
        UserDataAccess db = new MemoryUserDataAccess();
        db.createUser(new UserData("joe", "j@j.com", "passThisWord"));
        db.clear();
        assertNull(db.getUser("joe"));
    }

    @Test
    void createUser() {
        UserDataAccess db = new MemoryUserDataAccess();
        var user = new UserData("joe", "j@j.com", "passThisWord");
        db.createUser(user);
        assertEquals(user, db.getUser(user.username()));
    }

    @Test
    void getUser() {
    }

    @Test
    void getAuthToken() {
    }

    @Test
    void logout() throws Exception{
        UserDataAccess db = new MemoryUserDataAccess();
        UserData user = new UserData("joe", "j@j.com", "passThisWord");
        db.createUser(user);
        String authToken = db.createAuthToken(user.username());
        assertDoesNotThrow(() -> db.logout(authToken));
    }
}