package dataaccess;

import datamodel.*;

public interface DataAccess {
    void clear();
    void createUser(UserData userData);
    UserData getUser(String username);
    String createAuthToken(String username);
    void logout(String AuthToken) throws Exception;
}
