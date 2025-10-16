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
        } if (user.username() == null){
            throw new Exception("no username");

        } if(dataAccess.getUser(user.username()) != null){
            throw new Exception("Already exists");
        }
        dataAccess.createUser(user);
        return new AuthData(user.username(), generateAuthToken());
    }

    public AuthData login(UserData user) throws Exception{
        if (user.username() == null || user.password() == null){
            throw new Exception("bad request");
        }
        UserData userdata = dataAccess.getUser(user.username());
        if (userdata == null || !userdata.password().equals(user.password())){
            throw new Exception("user doesnt exist");
        }
        //String authToken = dataAccess.getAuthToken(user.username());
        return new AuthData(user.username(), generateAuthToken());
    }

    private String generateAuthToken(){
        return "xyz";
    }

}
