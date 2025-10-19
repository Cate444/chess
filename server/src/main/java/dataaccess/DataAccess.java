package dataaccess;

import datamodel.*;

import java.util.HashSet;

public interface DataAccess {

    void clear();
    void createUser(UserData userData);
    UserData getUser(String username);
    String createAuthToken(String username);
    void logout(String AuthToken) throws Exception;
    String authenticate(String authToken) throws Exception;
    int createGame(GameName gameName);

    void join(JoinInfo joinInfo, String username) throws Exception;
}
