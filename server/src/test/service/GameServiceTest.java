package service;

import chess.ChessGame;
import dataaccess.GameDataAccess;
import dataaccess.MemoryGameDataAccess;
import dataaccess.UserDataAccess;
import dataaccess.MemoryUserDataAccess;
import datamodel.GameName;
import datamodel.JoinInfo;
import datamodel.ReturnGameData;
import datamodel.UserData;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class GameServiceTest {

    @Test
    void joinGame() throws Exception {
        UserDataAccess userDB = new MemoryUserDataAccess();
        GameDataAccess gameDB = new MemoryGameDataAccess();
        UserService userService = new UserService(userDB);
        GameService gameService = new GameService(gameDB, userDB);
        var user = new UserData("joe", "j@j.com", "passThisWord");
        userService.register(user);
        var authData = userService.login(user);
        int gameID = gameService.createGame(authData.authToken(), new GameName("aGame"));

        gameService.joinGame(authData.authToken(), new JoinInfo(ChessGame.TeamColor.BLACK, gameID));

        var otherUser = new UserData("Eva", "Eva@faith.com", "theBabes");
        userService.register(otherUser);
        var otherAuthData = userService.login(otherUser);
        assertDoesNotThrow(() -> gameService.joinGame(otherAuthData.authToken(), new JoinInfo(ChessGame.TeamColor.WHITE, gameID)));
    }

    @Test
    void joinGameBad() throws Exception {
        UserDataAccess userDB = new MemoryUserDataAccess();
        GameDataAccess gameDB = new MemoryGameDataAccess();
        UserService userService = new UserService(userDB);
        GameService gameService = new GameService(gameDB, userDB);
        var user = new UserData("joe", "j@j.com", "passThisWord");
        userService.register(user);
        var authData = userService.login(user);
        int gameID = gameService.createGame(authData.authToken(), new GameName("aGame"));
        gameService.joinGame(authData.authToken(), new JoinInfo(ChessGame.TeamColor.BLACK, gameID));
        assertThrows(Exception.class, () -> gameService.joinGame(authData.authToken(), new JoinInfo(ChessGame.TeamColor.BLACK, gameID)));
    }

    @Test
    void createGame() throws Exception {
        UserDataAccess userDB = new MemoryUserDataAccess();
        GameDataAccess gameDB = new MemoryGameDataAccess();
        UserService userService = new UserService(userDB);
        GameService gameService = new GameService(gameDB, userDB);
        var user = new UserData("Eva", "Eva@faith.com", "theBabes");
        userService.register(user);
        var authData = userService.login(user);
        int gameID = gameService.createGame(authData.authToken(), new GameName("aGame"));
        assertTrue(gameID > 0);
    }

    @Test
    void badDataCreateGame() throws Exception{
        UserDataAccess userDB = new MemoryUserDataAccess();
        GameDataAccess gameDB = new MemoryGameDataAccess();
        UserService userService = new UserService(userDB);
        GameService gameService = new GameService(gameDB, userDB);
        UserData user = new UserData("Eva", "Eva@faith.com", "theBabes");
        userService.register(user);
        var authData = userService.login(user);
        assertThrows(Exception.class,() -> gameService.createGame(authData.authToken(), new GameName(null)));
    }


    @Test
    void listGames() throws Exception {
        UserDataAccess userDB = new MemoryUserDataAccess();
        GameDataAccess gameDB = new MemoryGameDataAccess();
        UserService userService = new UserService(userDB);
        GameService gameService = new GameService(gameDB, userDB);
        var user = new UserData("Eva", "Eva@faith.com", "theBabes");
        userService.register(user);
        var authData = userService.login(user);
        int gameID = gameService.createGame(authData.authToken(), new GameName("aGame"));
        int gameID2 = gameService.createGame(authData.authToken(), new GameName("anotherGame"));
        ArrayList<ReturnGameData> games = gameService.listGames(authData.authToken());
        assertEquals(games.size(), 2);
    }

    @Test
    void unAuthorizedListGames() throws Exception {
        UserDataAccess userDB = new MemoryUserDataAccess();
        GameDataAccess gameDB = new MemoryGameDataAccess();
        UserService userService = new UserService(userDB);
        GameService gameService = new GameService(gameDB, userDB);
        var user = new UserData("Eva", "Eva@faith.com", "theBabes");
        userService.register(user);
        var authData = userService.login(user);
        assertThrows(Exception.class,() -> gameService.listGames(authData.username()));
    }
}