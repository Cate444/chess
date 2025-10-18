package dataaccess;

import datamodel.*;

public interface DataAccess {
    void clear();
    void createUser(UserData userData);
    UserData getUser(String username);
    String getAuthToken(String username);
    Boolean authenticate(UserData user);
    String createAuthToken(String username);

}
