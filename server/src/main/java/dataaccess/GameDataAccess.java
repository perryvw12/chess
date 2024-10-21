package dataaccess;
import chess.ChessGame;
import model.GameData;
import java.util.ArrayList;
import java.util.HashMap;

public interface GameDataAccess {
    HashMap<String, Integer> createGame(String gameName) throws DataAccessException;

    GameData getGame(int gameID) throws DataAccessException;

    ArrayList<GameData> listGames() throws DataAccessException;

    void updateGame(int gameID, GameData updatedGame) throws DataAccessException;

    void clearGames() throws DataAccessException;
}
