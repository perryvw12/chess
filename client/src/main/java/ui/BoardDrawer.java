package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;

import java.util.Vector;

public class BoardDrawer {

    public BoardDrawer() {}

    public static String drawBoardWhite(ChessGame game) {
        ChessBoard board = game.getBoard();
        StringBuilder drawBoard = new StringBuilder(EscapeSequences.ERASE_SCREEN);
        String letters = String.format("%sa  \u2009b  \u2009\u2009c  \u2009\u2009d  \u2009\u2009e  \u2009\u2009f  \u2009\u2009g  \u2009\u2009h %s\n", EscapeSequences.EMPTY, EscapeSequences.EMPTY);
        drawBoard.append(letters);

        for (int row = 0; row < 8; row++) {
            drawBoard.append(8 - row).append(" ");

            for (int col = 0; col < 8; col++) {
                String bgColor = ((row + col) % 2 == 0) ? EscapeSequences.SET_BG_COLOR_LIGHT_GREY : EscapeSequences.SET_BG_COLOR_DARK_GREY;
                String pieceSymbol = EscapeSequences.EMPTY;

                ChessPiece piece = board.getPiece(new ChessPosition(row+1, col+1));
                if(piece != null) {
                    if (piece.getTeamColor() == ChessGame.TeamColor.BLACK) {
                        switch (piece.getPieceType()) {
                            case QUEEN -> pieceSymbol = EscapeSequences.BLACK_QUEEN;
                            case KING -> pieceSymbol = EscapeSequences.BLACK_KING;
                            case BISHOP -> pieceSymbol = EscapeSequences.BLACK_BISHOP;
                            case ROOK -> pieceSymbol = EscapeSequences.BLACK_ROOK;
                            case KNIGHT -> pieceSymbol = EscapeSequences.BLACK_KNIGHT;
                            case PAWN -> pieceSymbol = EscapeSequences.BLACK_PAWN;
                        }
                    } else {
                        switch (piece.getPieceType()) {
                            case QUEEN -> pieceSymbol = EscapeSequences.WHITE_QUEEN;
                            case KING -> pieceSymbol = EscapeSequences.WHITE_KING;
                            case BISHOP -> pieceSymbol = EscapeSequences.WHITE_BISHOP;
                            case ROOK -> pieceSymbol = EscapeSequences.WHITE_ROOK;
                            case KNIGHT -> pieceSymbol = EscapeSequences.WHITE_KNIGHT;
                            case PAWN -> pieceSymbol = EscapeSequences.WHITE_PAWN;
                        }
                    }
                }

                drawBoard.append(bgColor).append(pieceSymbol).append(EscapeSequences.RESET_BG_COLOR);
            }

            // Rank label on the right side
            drawBoard.append(" ").append(8 - row).append("\n");
        }

        // Bottom file labels
        drawBoard.append(letters);

        return drawBoard.toString();
    }
}
