package dataaccess;

import model.AuthData;
import exception.ServiceException;

import java.util.Random;

import static dataaccess.DataAccess.executeUpdate;

public class SQLAuthDAO implements AuthDataAccess{
    Random idGenerator = new Random();

    @Override
    public AuthData createAuth(String username) throws DataAccessException, ServiceException {
        var authToken = Integer.toString(idGenerator.nextInt());
        var statement = "INSERT INTO authData (authToken, username) VALUES (?, ?)";
        executeUpdate(statement, authToken, username);
        return new AuthData(authToken, username);
    }

    @Override
    public void deleteAuth(String authToken) throws ServiceException {
        var statement = "DELETE from authData WHERE authToken=?";
        executeUpdate(statement, authToken);
    }

    @Override
    public AuthData getAuth(String authToken) throws ServiceException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT authToken, username FROM authData WHERE authToken=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, authToken);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return new AuthData(authToken,  rs.getString("username"));
                    }
                }
            }
        } catch (Exception e) {
            throw new ServiceException(500, "Unable to read data");
        }
        return null;
    }

    @Override
    public void clearAuth() throws ServiceException {
        var statement = "TRUNCATE TABLE authData";
        executeUpdate(statement);
    }
}
