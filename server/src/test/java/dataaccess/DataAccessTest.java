package dataaccess;

import chess.ChessGame;
import datamodel.GameName;
import datamodel.JoinInfo;
import datamodel.UserData;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.Server;
import service.GameService;
import service.UserService;

import java.sql.Connection;
import java.sql.ResultSet;

import static org.junit.jupiter.api.Assertions.*;

class DataAccessTest {

    @BeforeEach
    void setup() throws Exception {
        DatabaseManager.createDatabase();
        UserDataAccess userDataAccess = new SQLUserDataAccess();
        GameDataAccess gameDataAccess = new SQLGameDataAccess();
    }


    @Test
    void createThenClear() throws Exception{
        DatabaseManager.createDatabase();
        UserDataAccess userDataAccess = new SQLUserDataAccess();
        GameDataAccess gameDataAccess = new SQLGameDataAccess();

        UserData newUser = new UserData("newUser", "test", "test@gmail.com");
        UserData user1 = new UserData("user1", "password1", "user1@gmail.com");
        UserData user2 = new UserData("user2", "hello", "myemail@gmail.com");

        userDataAccess.createUser(newUser);
        userDataAccess.createUser(user1);
        userDataAccess.createUser(user2);

        userDataAccess.createAuthToken(newUser);
        userDataAccess.createAuthToken(user1);
        userDataAccess.createAuthToken(user2);

        assertDoesNotThrow(()->userDataAccess.clear());
    }

    @Test
    void clear() throws Exception{
        DatabaseManager.createDatabase();
        UserDataAccess userDataAccess = new SQLUserDataAccess();
        GameDataAccess gameDataAccess = new SQLGameDataAccess();

        var user = new UserData("joe", "j@j.com", "passThisWord");
        userDataAccess.createUser(user);
        userDataAccess.clear();
        gameDataAccess.clear();

        assertEquals(gameDataAccess.listGames().size(), 0);
    }

    @Test
    void createUser() throws Exception {
        DatabaseManager.createDatabase();
        UserDataAccess userDataAccess = new SQLUserDataAccess();
        GameDataAccess gameDataAccess = new SQLGameDataAccess();

        UserData user = new UserData("alice", "alice@example.com", "password123");
        userDataAccess.createUser(user);

        try (Connection conn = DatabaseManager.getConnection()) {
            var stmt = conn.prepareStatement("SELECT * FROM usersTable WHERE username = ?");
            stmt.setString(1, "alice");
            ResultSet rs = stmt.executeQuery();
            assertTrue(rs.next());
            assertEquals("alice@example.com", rs.getString("email"));
            assertNotEquals("password123", rs.getString("password"));
        }
        userDataAccess.clear();
        gameDataAccess.clear();
    }

    @Test
    void createUserTwice() throws Exception {
        DatabaseManager.createDatabase();
        UserDataAccess userDataAccess = new SQLUserDataAccess();
        GameDataAccess gameDataAccess = new SQLGameDataAccess();
        userDataAccess.clear();
        gameDataAccess.clear();

        UserData user = new UserData("Heather", "heather@gmail.com", "T+H");
        userDataAccess.createUser(user);
        assertThrows(Exception.class ,()-> userDataAccess.createUser(user));
        userDataAccess.clear();
        gameDataAccess.clear();
    }


    @Test
    void createAuthToken() throws Exception {
        DatabaseManager.createDatabase();
        UserDataAccess userDataAccess = new SQLUserDataAccess();
        GameDataAccess gameDataAccess = new SQLGameDataAccess();
        userDataAccess.clear();
        gameDataAccess.clear();

        UserData user = new UserData("Ty", "TYJ@gmail.com", "11/05");
        userDataAccess.createUser(user);
        String authToken = userDataAccess.createAuthToken(user);
        try (Connection conn = DatabaseManager.getConnection()) {
            var stmt = conn.prepareStatement("SELECT authToken FROM authTable WHERE username = ?");
            stmt.setString(1, "Ty");
            ResultSet rs = stmt.executeQuery();
            assertTrue(rs.next());
            assertEquals(authToken, rs.getString("authToken"));
        }
    }

    @Test
    void createAuthTokenTwice() throws Exception {
        DatabaseManager.createDatabase();
        UserDataAccess userDataAccess = new SQLUserDataAccess();
        GameDataAccess gameDataAccess = new SQLGameDataAccess();
        userDataAccess.clear();
        gameDataAccess.clear();

        UserData user = new UserData("Ty", "TYJ@gmail.com", "11/05");
        userDataAccess.createUser(user);
        userDataAccess.createAuthToken(user);
        try (Connection conn = DatabaseManager.getConnection()) {
            var stmt = conn.prepareStatement("SELECT authToken FROM authTable WHERE username = ?");
            stmt.setString(1, "Ty");
            ResultSet rs = stmt.executeQuery();
            assertTrue(rs.next());
            assertNotEquals(userDataAccess.createAuthToken(user), rs.getString("authToken"));
        }
    }

    @Test
    void logout() throws Exception {
        DatabaseManager.createDatabase();
        UserDataAccess userDataAccess = new SQLUserDataAccess();
        GameDataAccess gameDataAccess = new SQLGameDataAccess();
        userDataAccess.clear();
        gameDataAccess.clear();

        UserData user = new UserData("Ty", "TYJ@gmail.com", "11/05");
        userDataAccess.createUser(user);
        String authToken = userDataAccess.createAuthToken(user);
        userDataAccess.logout(authToken);
        try (Connection conn = DatabaseManager.getConnection()) {
            var stmt = conn.prepareStatement("SELECT authToken FROM authTable WHERE username = ?");
            stmt.setString(1, "Ty");
            ResultSet rs = stmt.executeQuery();
            assertTrue(!rs.next());
        }
    }

    @Test
    void logoutWrongAuthToken() throws Exception {
        DatabaseManager.createDatabase();
        UserDataAccess userDataAccess = new SQLUserDataAccess();
        GameDataAccess gameDataAccess = new SQLGameDataAccess();
        userDataAccess.clear();
        gameDataAccess.clear();

        UserData user = new UserData("Ty", "TYJ@gmail.com", "11/05");
        userDataAccess.createUser(user);
        String authToken = userDataAccess.createAuthToken(user);
        userDataAccess.logout(authToken);

        assertThrows(Exception.class, ()->userDataAccess.logout(authToken));
    }

}