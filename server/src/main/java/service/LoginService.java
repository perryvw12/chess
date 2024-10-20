package service;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import model.AuthData;

import java.util.Objects;

public class LoginService {
    DataAccess dataAccess;

    public LoginService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public AuthData loginUser(String username, String password) throws DataAccessException, ServiceException {
       var userData = dataAccess.userDataAccess.getUser(username);
       if(userData == null) {
           throw new ServiceException(401, "Error: unauthorized");
       } else if(!Objects.equals(userData.password(), password)) {
           throw new ServiceException(401, "Error: unauthorized");
       } else {
           return dataAccess.authDataAccess.createAuth(userData.username());
       }
    }
}
