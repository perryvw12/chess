package dataaccess;

import model.AuthData;
import service.ServiceException;

import java.util.Random;

import static dataaccess.DataAccess.executeUpdate;

public class SQLAuthDAO implements AuthDataAccess{
    Random idGenerator = new Random();

    @Override
    public AuthData createAuth(String username) throws DataAccessException, ServiceException {
        int authToken = idGenerator.nextInt(1000);
        var statement = "INSERT INTO authData (authToken, username) VALUES (?, ?)";
        executeUpdate(statement, authToken, username);
        return new AuthData(Integer.toString(authToken), username);
    }

    @Override
    public void deleteAuth(String authToken) throws ServiceException {
        var authTokenInt = Integer.parseInt(authToken);
        var statement = "DELETE from authData WHERE authToken=?";
        executeUpdate(statement, authTokenInt);
    }

    @Override
    public AuthData getAuth(String authToken) throws ServiceException {
        var authTokenInt = Integer.parseInt(authToken);
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT authToken, username FROM authData WHERE authToken=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setInt(1, authTokenInt);
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
