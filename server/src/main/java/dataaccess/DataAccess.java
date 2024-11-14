package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import exception.ServiceException;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;

import static java.sql.Types.NULL;


public class DataAccess {
    public UserDataAccess userDataAccess;
    public AuthDataAccess authDataAccess;
    public GameDataAccess gameDataAccess;

    public DataAccess(Implementation implementation) {
        if (implementation == Implementation.MEMORY) {
            userDataAccess = new MemoryUserDAO();
            authDataAccess = new MemoryAuthDAO();
            gameDataAccess = new MemoryGameDAO();
        } else if (implementation == Implementation.SQL) {
            userDataAccess = new SQLUserDAO();
            authDataAccess = new SQLAuthDAO();
            gameDataAccess = new SQLGameDAO();
            try {
                configureDatabase();
            } catch (Throwable ex) {
                System.out.printf("Unable to start server: %s%n", ex.getMessage());
            }
        }
    }

    private static final String[] CREATESTATEMENTS = {
            """
            CREATE TABLE IF NOT EXISTS authData (
            `authToken` varchar(256) NOT NULL,
            `username` varchar(256) NOT NULL,
            PRIMARY KEY (`authToken`)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """,
            """
            CREATE TABLE IF NOT EXISTS gameData (
            `gameID` int NOT NULL,
            `json` TEXT NOT NULL,
            PRIMARY KEY (`gameID`)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """,
            """
            CREATE TABLE IF NOT EXISTS userData (
            `username` varchar(256) NOT NULL,
            `password` varchar(256) NOT NULL,
            `json` TEXT DEFAULT NULL,
            PRIMARY KEY (`username`)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """
    };

    static int executeUpdate(String statement, Object... params) throws ServiceException {
        try (var conn = DatabaseManager.getConnection();
             var ps = conn.prepareStatement(statement, Statement.RETURN_GENERATED_KEYS)) {

            setParameters(ps, params);

            ps.executeUpdate();
            var rs = ps.getGeneratedKeys();
            return rs.next() ? rs.getInt(1) : 0;

        } catch (SQLException e) {
            throw new ServiceException(500, "Unable to update database");
        }
    }

    private static void setParameters(PreparedStatement ps, Object... params) throws SQLException {
        for (var i = 0; i < params.length; i++) {
            var param = params[i];
            switch (param) {
                case String p -> ps.setString(i + 1, p);
                case Integer p -> ps.setInt(i + 1, p);
                case ChessGame p -> ps.setString(i + 1, new Gson().toJson(p));
                case null -> ps.setNull(i + 1, Types.NULL);
                default -> throw new SQLException("Unsupported parameter type: " + param.getClass());
            }
        }
    }

    public static void configureDatabase() throws ServiceException {
        DatabaseManager.createDatabase();
        try (var conn = DatabaseManager.getConnection()) {
            for (var statement : CREATESTATEMENTS) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException ex) {
            throw new ServiceException(500, "Unable to configure database");
        }
    }

    public enum Implementation {MEMORY, SQL}
}
