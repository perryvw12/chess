package dataaccess;

import com.google.gson.Gson;
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
    public UserData getUser(String username) throws DataAccessException, ServiceException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT username, json FROM userData WHERE username=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, username);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return new Gson().fromJson(rs.getString("json"), UserData.class);
                    }
                }
            }
        } catch (Exception e) {
            throw new ServiceException(500, "Unable to read data");
        }
        return null;
    }

    @Override
    public void clearUsers() throws DataAccessException {

    }
}
