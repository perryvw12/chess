package dataaccess;

public class DataAccess {
    public UserDataAccess userDataAccess;
    public AuthDataAccess authDataAccess;
    public GameDataAccess gameDataAccess;

    public DataAccess(Implementation implementation) {
        if (implementation == Implementation.MEMORY) {
            userDataAccess = new MemoryUserDAO();
            authDataAccess = new MemoryAuthDAO();
            gameDataAccess = new MemoryGameDAO();
        }
    }

    public enum Implementation {MEMORY, SQL}
}
