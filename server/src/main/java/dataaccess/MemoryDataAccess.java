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


    @Override
    public String createAuthToken(String username){
        String authToken = UUID.randomUUID().toString();
        authTokenUserMap.put(authToken, username);
        return authToken;
    }

    @Override
    public void logout(String authToken) throws Exception {
        if (!authTokenUserMap.containsKey(authToken)){
            throw new DataAccessException("Unauthorized");
        }
        authTokenUserMap.remove(authToken);
    }

}
