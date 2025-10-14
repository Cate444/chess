package service;

import datamodel.AuthData;
import datamodel.UserData;

public class UserService {

    public AuthData register(UserData user){
        return new AuthData(user.username(), generateAuthToken());
    }

    private String generateAuthToken(){
        return "xyz";
    }

}
