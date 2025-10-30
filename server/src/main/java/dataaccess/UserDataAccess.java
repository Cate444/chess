package dataaccess;

import datamodel.*;
import org.junit.jupiter.api.function.Executable;

public interface UserDataAccess {
    Executable clear() throws Exception;
    void createUser(UserData userData) throws Exception;
//    UserData getUser(UserData userData) throws Exception;
    String createAuthToken(UserData userData) throws Exception;
    void logout(String authToken) throws Exception;
    String authenticate(String authToken) throws Exception;
}
