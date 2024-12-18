package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    private TeamColor turn = TeamColor.WHITE;
    private ChessBoard board = new ChessBoard();
    private boolean gameInProgress = true;

    public ChessGame() {
        board.resetBoard();
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return turn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        turn = team;
    }

    public boolean isGameInProgress() {
        return gameInProgress;
    }

    public void setGameInProgress(boolean gameInProgress) {
        this.gameInProgress = gameInProgress;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        Collection<ChessMove> valid = new ArrayList<>();
        ChessBoard tempBoard = new ChessBoard(getBoard());
        setBoard(tempBoard);
        ChessPiece piece = tempBoard.getPiece(startPosition);

        if(piece == null) {
            return null;
        }

        Collection<ChessMove> moves = piece.pieceMoves(tempBoard, startPosition);
        var team = piece.getTeamColor();
        for(ChessMove move : moves) {
            ChessPiece capturedPiece = null;
            if(tempBoard.getPiece(move.getEndPosition()) != null) {
                capturedPiece = tempBoard.getPiece(move.getEndPosition());
            }
            tempBoard.movePiece(move.getStartPosition(), move.getEndPosition());
            if(!isInCheck(team)) {
                valid.add(move);
            }
            tempBoard.movePiece(move.getEndPosition(), move.getStartPosition());
            if(capturedPiece != null) {
                tempBoard.addPiece(move.getEndPosition(), capturedPiece);
            }
        }
        setBoard(board);
        return valid;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPiece piece = getBoard().getPiece(move.getStartPosition());
        if(piece == null) {
            throw new InvalidMoveException();
        }
        if(piece.getTeamColor() != getTeamTurn()) {
            throw new InvalidMoveException();
        }
        Collection<ChessMove> validMoves = validMoves(move.getStartPosition());
        for(ChessMove valid : validMoves) {
            if(valid.equals(move)) {
                getBoard().movePiece(move.getStartPosition(), move.getEndPosition());
                if(move.getPromotionPiece() != null) {
                    getBoard().addPiece(move.getEndPosition(), new ChessPiece(piece.getTeamColor(), move.getPromotionPiece()));
                }
                if(getTeamTurn() == TeamColor.WHITE) {
                    setTeamTurn(TeamColor.BLACK);
                } else {
                    setTeamTurn(TeamColor.WHITE);
                }
                return;
            }
        }
        throw new InvalidMoveException();
    }

    private ChessPosition findMyKing(TeamColor teamColor) {
        ChessPosition pos;
        ChessPiece piece;
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                pos = new ChessPosition(row, col);
                piece = getBoard().getPiece(pos);

                if(piece == null) {
                    continue;
                }
                if (piece.getPieceType() == ChessPiece.PieceType.KING && piece.getTeamColor() == teamColor) {
                    return pos;
                }
            }
        }
        return null;
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition kingPos = findMyKing(teamColor);

        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition pos = new ChessPosition(row, col);
                ChessPiece piece = getBoard().getPiece(pos);

                if (piece == null) {
                    continue;
                }
                if (piece.getTeamColor() == teamColor) {
                    continue;
                }

                Collection<ChessMove> moveLst = piece.pieceMoves(getBoard(), pos);
                for(ChessMove move : moveLst) {
                    if(move.getEndPosition().equals(kingPos)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        if (isInCheck(teamColor)) {
            return getValidTeamMoves(teamColor).isEmpty();
        }
        return false;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        if (!isInCheck(teamColor)) {
            return getValidTeamMoves(teamColor).isEmpty();
        }
        return false;
    }

        private Collection<ChessMove> getValidTeamMoves(TeamColor teamColor) {
            var board = getBoard();
            Collection<ChessMove> validMoves = new ArrayList<>();

            for (int row = 1; row <= 8; row++) {
                for (int col = 1; col <= 8; col++) {
                    ChessPosition location = new ChessPosition(row, col);
                    ChessPiece piece = board.getPiece(location);
                    if (piece != null && piece.getTeamColor() == teamColor) {
                        validMoves.addAll(validMoves(location));
                    }
                }
            }

            return validMoves;
        }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {return true;}
        if (o == null || getClass() != o.getClass()) {return false;}
        ChessGame chessGame = (ChessGame) o;
        return turn == chessGame.turn && Objects.equals(board, chessGame.board);
    }

    @Override
    public int hashCode() {
        return Objects.hash(turn, board);
    }
}
