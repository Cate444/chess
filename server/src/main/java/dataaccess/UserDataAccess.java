package dataaccess;

import datamodel.*;

public interface UserDataAccess {
    void clear();
    void createUser(UserData userData);
    UserData getUser(String username);
    String createAuthToken(String username);
    void logout(String authToken) throws Exception;
    String authenticate(String authToken) throws Exception;
}
