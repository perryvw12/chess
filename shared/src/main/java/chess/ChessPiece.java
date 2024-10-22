package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {
    private final ChessGame.TeamColor pieceColor;
    private final ChessPiece.PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;

    }

    //copy constructor
    public ChessPiece(ChessPiece piece) {
        this.pieceColor = piece.pieceColor;
        this.type = piece.type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    //A helper function that adds specified moves to a collection of chess moves//
    private void moveAdder(ChessBoard board, ChessPosition myPosition, int rowMove, int colMove, boolean repetitive, Collection<ChessMove> moves) {
        int row = myPosition.getRow();
        int col = myPosition.getColumn();

        if (!repetitive) {
            row += rowMove;
            col += colMove;

            if (row < 1 || row > 8 || col < 1 || col > 8) {
                return;
            }

            ChessPosition newPosition = new ChessPosition(row, col);
            ChessPiece pieceAtNewPosition = board.getPiece(newPosition);
            if (pieceAtNewPosition == null) {
                moves.add(new ChessMove(myPosition, newPosition, null));
            } else {
                if (pieceAtNewPosition.getTeamColor() != pieceColor) {
                    moves.add(new ChessMove(myPosition, newPosition, null));
                }
            }
        } else {
            while (true) {
                row += rowMove;
                col += colMove;
                if (row < 1 || row > 8 || col < 1 || col > 8) {
                    break;
                }

                ChessPosition newPosition = new ChessPosition(row, col);
                ChessPiece pieceAtNewPosition = board.getPiece(newPosition);

                if (pieceAtNewPosition == null) {
                    moves.add(new ChessMove(myPosition, newPosition, null));
                } else {
                    if (pieceAtNewPosition.getTeamColor() != pieceColor) {
                        moves.add(new ChessMove(myPosition, newPosition, null));
                        break;
                    }
                    break;
                }
            }
        }
    }

    private void pawnMover(ChessBoard board, ChessPosition myPosition, int direction, Collection<ChessMove> moves) {
        int row = myPosition.getRow();
        int col = myPosition.getColumn();
        int newRow = row + direction;

        ChessPosition newPos = new ChessPosition(newRow, col);
        ChessPiece pieceAtNewPos = board.getPiece(newPos);

        if (pieceAtNewPos == null) {
            //move 1 space
            if (newRow == 8 || newRow == 1) {
                for (PieceType pieces : PieceType.values()) {
                    if (pieces == PieceType.KING || pieces == PieceType.PAWN) {
                        continue;
                    } else {
                        moves.add(new ChessMove(myPosition, newPos, pieces));
                    }
                }
                return;
            } else {
                moves.add(new ChessMove(myPosition, newPos, null));
            }

            //move 2 spaces
            ChessPosition doubleMove = new ChessPosition((newRow + direction), col);
            ChessPiece pieceAtDouble = board.getPiece(doubleMove);
            if ((direction == 1 & row == 2) || (direction == -1 & row == 7)) {
                if (pieceAtDouble == null) {
                    moves.add(new ChessMove(myPosition, doubleMove, null));
                }
            }
        }
    }

    private void pawnAttack(ChessBoard board, ChessPosition myPosition, int rowMove, int colMove, Collection<ChessMove> moves) {
        int row = myPosition.getRow();
        int col = myPosition.getColumn();

        ChessPosition newPos = new ChessPosition(row + rowMove, col + colMove);

        if (newPos.getColumn() >= 1 & newPos.getColumn() <= 8) {
            ChessPiece pieceAtPos = board.getPiece(newPos);

            if (pieceAtPos != null) {
                if (pieceAtPos.getTeamColor() != pieceColor) {
                    if (newPos.getRow() == 8 || newPos.getRow() == 1) {
                        for (PieceType pieces : PieceType.values()) {
                            if (pieces == PieceType.KING || pieces == PieceType.PAWN) {
                                continue;
                            } else {
                                moves.add(new ChessMove(myPosition, newPos, pieces));
                            }
                        }
                    } else {
                        moves.add(new ChessMove(myPosition, newPos, null));
                    }
                }
            }
        }
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();

        switch (type) {
            case PieceType.BISHOP: {
                //move up right
                moveAdder(board, myPosition, 1, 1, true, moves);
                //move up left
                moveAdder(board, myPosition, 1, -1, true, moves);
                //move down right
                moveAdder(board, myPosition, -1, 1, true, moves);
                //move down left
                moveAdder(board, myPosition, -1, -1, true, moves);
                break;
            }

            case PieceType.KING: {
                //move right
                moveAdder(board, myPosition, 0, 1, false, moves);
                //move left
                moveAdder(board, myPosition, 0, -1, false, moves);
                //move up
                moveAdder(board, myPosition, 1, 0, false, moves);
                //move down
                moveAdder(board, myPosition, -1, 0, false, moves);
                //move up right
                moveAdder(board, myPosition, 1, 1, false, moves);
                //move up left
                moveAdder(board, myPosition, 1, -1, false, moves);
                //move down right
                moveAdder(board, myPosition, -1, 1, false, moves);
                //move down left
                moveAdder(board, myPosition, -1, -1, false, moves);
                break;
            }
            case PieceType.KNIGHT: {
                moveAdder(board, myPosition, 2, -1, false, moves);
                moveAdder(board, myPosition, 2, 1, false, moves);
                moveAdder(board, myPosition, -2, -1, false, moves);
                moveAdder(board, myPosition, -2, 1, false, moves);
                moveAdder(board, myPosition, -1, 2, false, moves);
                moveAdder(board, myPosition, 1, 2, false, moves);
                moveAdder(board, myPosition, -1, -2, false, moves);
                moveAdder(board, myPosition, 1, -2, false, moves);
                break;
            }

            case PieceType.PAWN: {
                //moves for white pawn
                if (pieceColor == ChessGame.TeamColor.WHITE) {
                    pawnMover(board, myPosition, 1, moves);
                    pawnAttack(board, myPosition, 1, 1, moves);
                    pawnAttack(board, myPosition, 1, -1, moves);
                }

                //moves for black pawn
                if (pieceColor == ChessGame.TeamColor.BLACK) {
                    pawnMover(board, myPosition, -1, moves);
                    pawnAttack(board, myPosition, -1, 1, moves);
                    pawnAttack(board, myPosition, -1, -1, moves);
                }
                break;
            }
            case PieceType.QUEEN: {
                //move right
                moveAdder(board, myPosition, 0, 1, true, moves);
                //move left
                moveAdder(board, myPosition, 0, -1, true, moves);
                //move up
                moveAdder(board, myPosition, 1, 0, true, moves);
                //move down
                moveAdder(board, myPosition, -1, 0, true, moves);
                //move up right
                moveAdder(board, myPosition, 1, 1, true, moves);
                //move up left
                moveAdder(board, myPosition, 1, -1, true, moves);
                //move down right
                moveAdder(board, myPosition, -1, 1, true, moves);
                //move down left
                moveAdder(board, myPosition, -1, -1, true, moves);
                break;
            }

            case PieceType.ROOK: {
                //move right
                moveAdder(board, myPosition, 0, 1, true, moves);
                //move left
                moveAdder(board, myPosition, 0, -1, true, moves);
                //move up
                moveAdder(board, myPosition, 1, 0, true, moves);
                //move down
                moveAdder(board, myPosition, -1, 0, true, moves);
                break;
            }
        }
        return moves;
    }

    @Override
    public String toString() {
        return "ChessPiece{" +
                "pieceColor=" + pieceColor +
                ", type=" + type +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessPiece that = (ChessPiece) o;
        return pieceColor == that.pieceColor && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type);
    }
}
