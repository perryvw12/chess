package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;
import model.UserData;
import service.ServiceException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import static dataaccess.DataAccess.executeUpdate;

public class SQLGameDAO implements GameDataAccess{
    Random idGenerator = new Random();

    @Override
    public HashMap<String, Integer> createGame(String gameName) throws DataAccessException, ServiceException {
        int gameID = idGenerator.nextInt(1000);
        var gameData = new GameData(gameID, null, null, gameName, new ChessGame());
        var statement = "INSERT INTO gameData (gameID, json) VALUES (?, ?)";
        executeUpdate(statement, gameID, new Gson().toJson(gameData));
        HashMap<String, Integer> createGameResult = new HashMap<>();
        createGameResult.put("gameID", gameID);
        return createGameResult;
    }

    @Override
    public GameData getGame(int gameID) throws ServiceException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT gameID, json FROM gameData WHERE gameID=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setInt(1, gameID);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return new Gson().fromJson(rs.getString("json"), GameData.class);
                    }
                }
            }
        } catch (Exception e) {
            throw new ServiceException(500, "Unable to read data");
        }
        return null;
    }

    @Override
    public ArrayList<GameData> listGames() throws DataAccessException, ServiceException {
        var results = new ArrayList<GameData>();
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT gameID, json FROM gameData";
            try (var ps = conn.prepareStatement(statement)) {
                try (var rs = ps.executeQuery()) {
                    while (rs.next()) {
                        results.add(new Gson().fromJson(rs.getString("json"), GameData.class));
                    }
                }
            }
        } catch (Exception e) {
            throw new ServiceException(500, "Unable to read data");
        }
        return results;
    }

    @Override
    public void updateGame(int gameID, GameData updatedGame) throws DataAccessException, ServiceException {
        var statement = "UPDATE gameData SET json=? WHERE gameID=?";
        executeUpdate(statement, new Gson().toJson(updatedGame), gameID);
    }

    @Override
    public void clearGames() throws ServiceException {
        var statement = "TRUNCATE TABLE gameData";
        executeUpdate(statement);
    }
}
