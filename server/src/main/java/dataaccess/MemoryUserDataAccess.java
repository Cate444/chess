package dataaccess;

import datamodel.*;
import org.junit.jupiter.api.function.Executable;

import java.util.*;

public class MemoryUserDataAccess implements UserDataAccess {
    private final HashMap<String, UserData> users = new HashMap<>();
    private final HashMap<String, String> authTokenUserMap = new HashMap<>();

    @Override
    public Executable clear() {
        users.clear();
        authTokenUserMap.clear();
        return null;
    }

    @Override
    public void createUser(UserData user) {
        if (users.containsValue(user.username())){
            users.put(user.username(), user);
        }
    }

    @Override
    public String getUser(String authToken) throws Exception{
        return authTokenUserMap.get(authToken);
    }

    @Override
    public String createAuthToken(UserData userData){
        String authToken = UUID.randomUUID().toString();
        authTokenUserMap.put(authToken, userData.username());
        return authToken;
    }

    @Override
    public void logout(String authToken) throws Exception {
        if (!authTokenUserMap.containsKey(authToken)){
            throw new DataAccessException("Unauthorized");
        }
        authTokenUserMap.remove(authToken);
    }

    @Override
    public String authenticate(String authToken) throws Exception{
        if (authTokenUserMap.containsKey(authToken)){
            return authTokenUserMap.get(authToken);
        } else {
            throw new Exception("Unauthorized");
        }
    }
}
