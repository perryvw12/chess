package dataaccess;

import org.eclipse.jetty.server.Authentication;

public class DataAccess {
    public UserDataAccess userDataAccess;
    public AuthDataAccess authDataAccess;
    public GameDataAccess gameDataAccess;

    public DataAccess(UserDataAccess userDataAccess, AuthDataAccess authDataAccess, GameDataAccess gameDataAccess) {
        this.userDataAccess = userDataAccess;
        this.authDataAccess = authDataAccess;
        this.gameDataAccess = gameDataAccess;
    }
}
