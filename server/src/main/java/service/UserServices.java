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

    public Object registerUser(UserData newUser) throws DataAccessException, ServiceException {
        if(Objects.equals(newUser.username(), null) | Objects.equals(newUser.password(), null) | Objects.equals(newUser.email(), null)) {
            throw new ServiceException(400, "Error: bad request");
        }
        if(dataAccess.userDataAccess.getUser(newUser.username()) != null) {
            throw new ServiceException(403, "Error: already taken");
        } else {
            dataAccess.userDataAccess.createUser(newUser);
            return dataAccess.authDataAccess.createAuth(newUser.username());
        }
    }

    public void deleteAll() throws DataAccessException {
        dataAccess.userDataAccess.deleteUsers();
    }
}
