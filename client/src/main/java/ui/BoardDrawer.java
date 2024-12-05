package ui;

import chess.*;

import java.util.Collection;

public class BoardDrawer {

    public BoardDrawer() {}

    public static String drawBoard(ChessGame game, ChessPosition chessPosition, boolean isWhitePerspective) {
        ChessBoard board = game.getBoard();
        Collection<ChessMove> validMoves = null;
        if (chessPosition != null) {
            validMoves = game.validMoves(chessPosition);
        }

        StringBuilder drawBoard = new StringBuilder(EscapeSequences.ERASE_SCREEN);

        String letters = getColumnLabels(isWhitePerspective);
        drawBoard.append(letters);

        int startRow = isWhitePerspective ? 8 : 1;
        int rowIncrement = isWhitePerspective ? -1 : 1;

        for (int i = 0; i < 8; i++) {
            int rowLabel = startRow + rowIncrement * i;
            drawBoard.append(rowLabel).append(" ");
            drawRow(drawBoard, board, validMoves, chessPosition, rowLabel, isWhitePerspective);
            drawBoard.append(" ").append(rowLabel).append("\n");
        }

        drawBoard.append(letters);
        return drawBoard.toString();
    }

    private static void drawRow(StringBuilder drawBoard, ChessBoard board, Collection<ChessMove> validMoves,
                                ChessPosition chessPosition, int rowLabel, boolean isWhitePerspective) {
        int startCol = isWhitePerspective ? 1 : 8;
        int colIncrement = isWhitePerspective ? 1 : -1;

        for (int col = startCol; (isWhitePerspective ? col <= 8 : col >= 1); col += colIncrement) {
            String bgColor = ((rowLabel + col) % 2 == 0) ? EscapeSequences.SET_BG_COLOR_LIGHT_GREY : EscapeSequences.SET_BG_COLOR_DARK_GREY;
            ChessPosition position = new ChessPosition(rowLabel, col);

            if (validMoves != null) {
                for (ChessMove move : validMoves) {
                    if (position.equals(move.getEndPosition())) {
                        bgColor = ((rowLabel + col) % 2 == 0) ? EscapeSequences.SET_BG_COLOR_GREEN : EscapeSequences.SET_BG_COLOR_DARK_GREEN;
                        break;
                    }
                }
                if (position.equals(chessPosition)) {
                    bgColor = EscapeSequences.SET_BG_COLOR_HIGHLIGHT;
                }
            }

            ChessPiece piece = board.getPiece(position);
            String pieceSymbol = (piece != null) ? getPieceSymbol(piece) : EscapeSequences.EMPTY;

            drawBoard.append(bgColor).append(pieceSymbol).append(EscapeSequences.RESET_BG_COLOR);
        }
    }

    private static String getColumnLabels(boolean isWhitePerspective) {
        return isWhitePerspective
                ? String.format("%sa  \u2009b  \u2009\u2009c  \u2009\u2009d  \u2009\u2009e  " +
                "\u2009\u2009f  \u2009\u2009g  \u2009\u2009h %s\n", EscapeSequences.EMPTY, EscapeSequences.EMPTY)
                : String.format("%sh  \u2009g  \u2009\u2009f  \u2009\u2009e  \u2009\u2009d  \u2009\u2009c  " +
                "\u2009\u2009b  \u2009\u2009a %s\n", EscapeSequences.EMPTY, EscapeSequences.EMPTY);
    }

    private static String getPieceSymbol(ChessPiece piece) {
        if (piece.getTeamColor() == ChessGame.TeamColor.BLACK) {
            return switch (piece.getPieceType()) {
                case QUEEN -> EscapeSequences.BLACK_QUEEN;
                case KING -> EscapeSequences.BLACK_KING;
                case BISHOP -> EscapeSequences.BLACK_BISHOP;
                case ROOK -> EscapeSequences.BLACK_ROOK;
                case KNIGHT -> EscapeSequences.BLACK_KNIGHT;
                case PAWN -> EscapeSequences.BLACK_PAWN;
            };
        } else {
            return switch (piece.getPieceType()) {
                case QUEEN -> EscapeSequences.WHITE_QUEEN;
                case KING -> EscapeSequences.WHITE_KING;
                case BISHOP -> EscapeSequences.WHITE_BISHOP;
                case ROOK -> EscapeSequences.WHITE_ROOK;
                case KNIGHT -> EscapeSequences.WHITE_KNIGHT;
                case PAWN -> EscapeSequences.WHITE_PAWN;
            };
        }
    }

    public static String drawBoardWhite(ChessGame game, ChessPosition chessPosition) {
        return drawBoard(game, chessPosition, true);
    }

    public static String drawBoardBlack(ChessGame game, ChessPosition chessPosition) {
        return drawBoard(game, chessPosition, false);
    }
}
