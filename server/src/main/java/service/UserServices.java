package service;


import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import model.AuthData;
import model.UserData;

import java.util.Objects;

public class UserServices {
    DataAccess dataAccess;

    public UserServices(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public Object registerUser(UserData newUser) throws DataAccessException {
        if(Objects.equals(newUser.username(), null) | Objects.equals(newUser.password(), null) | Objects.equals(newUser.email(), null)) {
            return "400";
        }
        if(dataAccess.userDataAccess.getUser(newUser.username()) != null) {
            return "403";
        } else {
            dataAccess.userDataAccess.createUser(newUser);
            return dataAccess.authDataAccess.createAuth(newUser.username());
        }
    }

    public void deleteAll() throws DataAccessException {
        dataAccess.userDataAccess.deleteUsers();
    }
}
