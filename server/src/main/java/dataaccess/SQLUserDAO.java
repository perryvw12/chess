package dataaccess;

import model.UserData;
import org.mindrot.jbcrypt.BCrypt;
import service.ServiceException;

import java.sql.SQLException;

import static dataaccess.DataAccess.executeUpdate;

public class SQLUserDAO implements  UserDataAccess{
    @Override
    public void createUser(UserData user) throws DataAccessException, ServiceException, SQLException {
        var hashedPass = BCrypt.hashpw(user.password(), BCrypt.gensalt());
        var statement = "INSERT INTO userData (username, password, email) VALUES (?, ?, ?)";
        executeUpdate(statement, user.username(), hashedPass, user.email());
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        return null;
    }

    @Override
    public void clearUsers() throws DataAccessException {

    }
}
