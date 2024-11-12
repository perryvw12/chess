package ui;

import chess.ChessBoard;
import chess.ChessGame;

public class BoardDrawer {

    public BoardDrawer() {}

    static String drawBoard(ChessGame game) {
        ChessBoard board = game.getBoard();
        return board.toString();
    }
}
