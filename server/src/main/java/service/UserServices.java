package service;


import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import model.AuthData;
import model.UserData;

public class UserServices {
    DataAccess dataAccess;

    public UserServices(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public Object registerUser(UserData newUser) throws DataAccessException {
        if(dataAccess.userDataAccess.getUser(newUser.username()) == null) {
            return "403";
        } else {
            dataAccess.userDataAccess.createUser(newUser);
            return dataAccess.authDataAccess.createAuth(newUser.username());
        }

    }
}
