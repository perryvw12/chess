package dataaccess;
import chess.ChessGame;
import model.GameData;
import java.util.ArrayList;

public interface GameDataAccess {
    String createGame(GameData gameData) throws DataAccessException;

    GameData getGame(String gameID) throws DataAccessException;

    ArrayList<GameData> listGames() throws DataAccessException;

    boolean updateGame(String gameID, ChessGame game) throws DataAccessException;
}
