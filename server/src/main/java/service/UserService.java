package service;

import dataaccess.DataAccess;
import datamodel.AuthData;
import datamodel.UserData;

public class UserService {
    private final DataAccess dataAccess;

    public UserService(DataAccess dataAccess){
        this.dataAccess = dataAccess;
    }

    public AuthData register(UserData user) throws Exception{
        if(user.password() == null ){
            throw new Exception("no password");
        } if(dataAccess.getUser(user.username()) != null){
            throw new Exception("Already exists");
        }
        dataAccess.createUser(user);
        return new AuthData(user.username(), generateAuthToken());
    }

    public AuthData login(UserData user) throws Exception{
        if (dataAccess.getUser(user.username()) == null){
            throw new Exception("user doesn't exist");
        }
        Boolean authenticated = dataAccess.authenticate(user);
        if(!authenticated){
            throw new Exception("wrong password");
        }
        String authToken = dataAccess.getAuthToken(user.username());
        return new AuthData(user.username(), generateAuthToken());
    }

    private String generateAuthToken(){
        return "xyz";
    }

}
