package dataaccess;

import model.AuthData;
import service.ServiceException;

public interface AuthDataAccess {
    AuthData createAuth(String username) throws DataAccessException, ServiceException;

    void deleteAuth(String authToken) throws DataAccessException, ServiceException;

    AuthData getAuth(String authToken) throws DataAccessException, ServiceException;

    void clearAuth() throws DataAccessException, ServiceException;
}
