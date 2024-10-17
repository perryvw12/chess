package service;


import dataaccess.DataAccessException;
import dataaccess.MemoryUserDAO;
import model.UserData;
import dataaccess.UserDataAccess;

public class UserServices {
    static UserDataAccess userDataAccess = new MemoryUserDAO();

    public static String registerUser(UserData newUser) throws DataAccessException {
        if(userDataAccess.getUser(newUser.username()) == null) {
            return "403";
        } else {
            userDataAccess.createUser(newUser);
            return "200";
        }

    }
}
