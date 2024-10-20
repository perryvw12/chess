package dataaccess;
import chess.ChessGame;
import model.GameData;
import java.util.ArrayList;
import java.util.HashMap;

public interface GameDataAccess {
    HashMap<String, Integer> createGame(String gameName) throws DataAccessException;

    GameData getGame(String gameID) throws DataAccessException;

    ArrayList<GameData> listGames() throws DataAccessException;

    boolean updateGame(String gameID, ChessGame game) throws DataAccessException;

    void clearGames() throws DataAccessException;
}
