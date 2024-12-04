package websocket.commands;

import chess.ChessGame;
import chess.ChessMove;


public class MakeMoveCommand extends UserGameCommand{
    private final ChessMove chessMove;

    public MakeMoveCommand(CommandType commandType, String authToken, Integer gameID, ChessGame.TeamColor playerColor, ChessMove chessMove) {
        super(commandType, authToken, gameID, playerColor);
        this.chessMove = chessMove;
    }

    public ChessMove getChessMove() {return chessMove;}
}
