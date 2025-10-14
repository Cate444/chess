package dataaccess;

import datamodel.UserData;

import java.util.HashMap;

public class MemoryDataAccess implements DataAccess{
    private final HashMap<String, UserData> users = new HashMap<>();
    private final HashMap<String, String> authTokenUserDataHashMap = new HashMap<>();

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
        System.out.println(users.get(username));
        return users.get(username);
    }

    public String getAuthToken(String username){
        return "xyz";
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
