package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import service.ServiceException;
import java.sql.SQLException;
import java.sql.Statement;
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
        }
    }

    private static final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS authData (
            `authToken` int NOT NULL,
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
        try(var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(statement, Statement.RETURN_GENERATED_KEYS)) {
                for (var i = 0; i < params.length; i++) {
                    var param = params[i];
                    switch (param) {
                        case String p -> ps.setString(i + 1, p);
                        case Integer p -> ps.setInt(i + 1, p);
                        case ChessGame p -> ps.setString(i + 1, new Gson().toJson(p));
                        case null -> ps.setNull(i + 1, NULL);
                        default -> {
                        }
                    }
                }
                ps.executeUpdate();

                var rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }
                return 0;
            }
        } catch (SQLException e) {
            throw new ServiceException(500, "unable to update database");
        }
    }

    public static void configureDatabase() throws ServiceException {
        DatabaseManager.createDatabase();
        try (var conn = DatabaseManager.getConnection()) {
            for (var statement : createStatements) {
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
