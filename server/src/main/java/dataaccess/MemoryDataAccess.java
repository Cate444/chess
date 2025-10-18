package dataaccess;

import datamodel.UserData;

import java.util.HashMap;

import java.util.UUID;

public class MemoryDataAccess implements DataAccess{
    private final HashMap<String, UserData> users = new HashMap<>();
    private final HashMap<String, String> authTokenUserMap = new HashMap<>();

    @Override
    public void clear() {
        users.clear();
    }

    @Override
    public void createUser(UserData user) {
        users.put(user.username(), user);
    }

    @Override
    public UserData getUser(String username) {
        return users.get(username);
    }

    public String getAuthToken(String username){
        return authTokenUserMap.get(username);
    }

    @Override
    public String createAuthToken(String username){
        String authToken = UUID.randomUUID().toString();
        authTokenUserMap.put(username, authToken);
        return authToken;
    }

    @Override
    public Boolean authenticate(UserData user) {
        UserData userData = users.get(user);
        if (user.password() == userData.password()){
            return true;
        }
        return false;
    }
}
