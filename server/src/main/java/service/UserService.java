package service;

import dataaccess.UserDataAccess;
import datamodel.AuthData;
import datamodel.UserData;

public class UserService {
    private final UserDataAccess dataAccess;

    public UserService(UserDataAccess dataAccess){
        this.dataAccess = dataAccess;
    }

    public AuthData register(UserData user) throws Exception{
        if(user.password() == null ){
            throw new Exception("no password");
        } if (user.username() == null){
            throw new Exception("no username");
        }
        dataAccess.createUser(user);
        return new AuthData(user.username(), dataAccess.createAuthToken(user));
    }

    public AuthData login(UserData user) throws Exception{
        if (user.username() == null || user.password() == null){
            throw new Exception("bad request");
        }
        return new AuthData(user.username(), dataAccess.createAuthToken(user));
    }

    public void logout(String authToken) throws Exception{
        try {
            dataAccess.logout(authToken);
        } catch (Exception ex){
            throw ex;
        }
    }

    public void clear() throws Exception {
        try {
            dataAccess.clear();
        } catch (Exception ex){
            throw ex;
        }
    }
}

