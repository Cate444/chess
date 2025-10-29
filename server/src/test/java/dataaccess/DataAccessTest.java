package dataaccess;

import datamodel.UserData;
import org.junit.jupiter.api.Test;
import server.Server;

import static org.junit.jupiter.api.Assertions.*;

class DataAccessTest {


    @Test
    void clear() throws Exception{
        UserDataAccess db = new MemoryUserDataAccess();
        db.createUser(new UserData("joe", "j@j.com", "passThisWord"));
        db.clear();
        assertNull(db.getUser("joe"));
    }

    @Test
    void clearWrong() throws Exception{
        UserDataAccess userDataAccess;
        GameDataAccess gameDataAccess;
        try {
            DatabaseManager.createDatabase();
            userDataAccess = new SQLUserDataAccess();
            gameDataAccess = new SQLGameDataAccess();
        }catch (Exception ex){
            userDataAccess = new MemoryUserDataAccess();
            gameDataAccess = new MemoryGameDataAccess();
        }
        assertDoesNotThrow(userDataAccess.clear());
        assertDoesNotThrow(gameDataAccess.clear());

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