package service;

import chess.ChessGame;
import dataaccess.DataAccess;
import dataaccess.MemoryDataAccess;
import datamodel.GameName;
import datamodel.JoinInfo;
import datamodel.UserData;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GameServiceTest {

    @Test
    void joinGame() throws Exception {
        DataAccess db = new MemoryDataAccess();
        UserService userService = new UserService(db);
        GameService gameService = new GameService(db);
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
        DataAccess db = new MemoryDataAccess();
        UserService userService = new UserService(db);
        GameService gameService = new GameService(db);
        var user = new UserData("joe", "j@j.com", "passThisWord");
        userService.register(user);
        var authData = userService.login(user);
        int gameID = gameService.createGame(authData.authToken(), new GameName("aGame"));
        gameService.joinGame(authData.authToken(), new JoinInfo(ChessGame.TeamColor.BLACK, gameID));
        assertThrows(Exception.class, () -> gameService.joinGame(authData.authToken(), new JoinInfo(ChessGame.TeamColor.BLACK, gameID)));
    }

    @Test
    void createGame() throws Exception {
        DataAccess db = new MemoryDataAccess();
        UserService userService = new UserService(db);
        GameService gameService = new GameService(db);
        var user = new UserData("Eva", "Eva@faith.com", "theBabes");
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