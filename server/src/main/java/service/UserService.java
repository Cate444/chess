package service;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
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
        return new AuthData(user.username(), dataAccess.createAuthToken(user.username()));
    }

    public AuthData login(UserData user) throws Exception{
        if (user.username() == null || user.password() == null){
            throw new Exception("bad request");
        }
        UserData userdata = dataAccess.getUser(user.username());
        if (userdata == null || !userdata.password().equals(user.password())){
            throw new Exception("user doesnt exist");
        }
        return new AuthData(user.username(), dataAccess.createAuthToken(user.username()));
    }

    public void logout(String authToken) throws Exception{
        System.out.println(authToken);
        try {
            dataAccess.logout(authToken);
        } catch (Exception ex){
            throw ex;
        }
    }

    public int createGame(String authToken){
        return 1;
    }

    public void clear(){
        dataAccess.clear();
    }
}
