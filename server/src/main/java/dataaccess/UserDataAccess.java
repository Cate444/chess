package dataaccess;

import datamodel.*;
import org.junit.jupiter.api.function.Executable;

public interface UserDataAccess {
    Executable clear() throws Exception;
    void createUser(UserData userData) throws Exception;
    UserData getUser(String username) throws Exception;
    String createAuthToken(String username) throws Exception;
    void logout(String authToken) throws Exception;
    String authenticate(String authToken) throws Exception;
}
