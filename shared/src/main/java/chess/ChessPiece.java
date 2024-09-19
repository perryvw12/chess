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
    private void move_adder (ChessBoard board, ChessPosition myPosition, int row_move, int col_move, boolean repetitive, Collection<ChessMove> moves) {
        int row = myPosition.getRow();
        int col = myPosition.getColumn();

        if (!repetitive) {
            row += row_move;
            col += col_move;

            if (row < 1 || row > 8 || col < 1 || col > 8) {
                return;
            }

            ChessPosition new_position = new ChessPosition(row, col);
            ChessPiece piece_at_new_position = board.getPiece(new_position);
            if(piece_at_new_position == null) {
                moves.add(new ChessMove(myPosition, new_position, null));
            } else {
                if (piece_at_new_position.getTeamColor() != pieceColor) {
                    moves.add(new ChessMove(myPosition, new_position, null));
                }
            }
        } else {
            while(true) {
                row += row_move;
                col += col_move;
                if (row < 1 || row > 8 || col < 1 || col > 8) {
                    break;
                }

                ChessPosition new_position = new ChessPosition(row, col);
                ChessPiece piece_at_new_position = board.getPiece(new_position);

                if (piece_at_new_position == null) {
                    moves.add(new ChessMove(myPosition, new_position, null));
                } else {
                    if (piece_at_new_position.getTeamColor() != pieceColor) {
                        moves.add(new ChessMove(myPosition, new_position, null));
                        break;
                    }
                    break;
                }
            }
        }
    }

    private void pawn_mover (ChessBoard board, ChessPosition myPosition, int direction,  Collection<ChessMove> moves) {
        int row = myPosition.getRow();
        int col = myPosition.getColumn();
        int new_row = row + direction;

        ChessPosition new_pos = new ChessPosition(new_row, col);
        ChessPiece piece_at_new_pos = board.getPiece(new_pos);

        if (piece_at_new_pos == null) {
            //move 1 space
            if(new_row == 8 || new_row == 1) {
                for(PieceType pieces : PieceType.values()) {
                    if (pieces == PieceType.KING || pieces == PieceType.PAWN) {
                        continue;
                    } else {
                        moves.add(new ChessMove(myPosition, new_pos, pieces));
                    }
                }
                return;
            } else {
                moves.add(new ChessMove(myPosition, new_pos, null));
            }

            //move 2 spaces
            ChessPosition double_move = new ChessPosition((new_row + direction), col);
            ChessPiece piece_at_double = board.getPiece(double_move);
            if((direction == 1 & row == 2) || (direction == -1 & row == 7)) {
                if (piece_at_double == null) {
                    moves.add(new ChessMove(myPosition, double_move, null));
                }
            }
        }
    }

    private void pawn_attack (ChessBoard board, ChessPosition myPosition, int row_move, int col_move, Collection<ChessMove> moves) {
        int row = myPosition.getRow();
        int col = myPosition.getColumn();

        ChessPosition new_pos = new ChessPosition(row + row_move, col + col_move);

        if(new_pos.getColumn() >= 1 & new_pos.getColumn() <= 8) {
            ChessPiece piece_at_pos = board.getPiece(new_pos);

            if(piece_at_pos != null) {
                if(piece_at_pos.getTeamColor() != pieceColor) {
                    if(new_pos.getRow() == 8 || new_pos.getRow() == 1) {
                        for (PieceType pieces : PieceType.values()) {
                            if (pieces == PieceType.KING || pieces == PieceType.PAWN) {
                                continue;
                            } else {
                                moves.add(new ChessMove(myPosition, new_pos, pieces));
                            }
                        }
                    } else {
                        moves.add(new ChessMove(myPosition, new_pos, null));
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
                move_adder(board, myPosition, 1, 1, true, moves);
                //move up left
                move_adder(board, myPosition, 1, -1, true, moves);
                //move down right
                move_adder(board, myPosition, -1, 1, true, moves);
                //move down left
                move_adder(board, myPosition, -1, -1, true, moves);
                break;
            }

            case PieceType.KING: {
                //move right
                move_adder(board, myPosition, 0, 1, false, moves);
                //move left
                move_adder(board, myPosition, 0, -1, false, moves);
                //move up
                move_adder(board, myPosition, 1, 0, false, moves);
                //move down
                move_adder(board, myPosition, -1, 0, false, moves);
                //move up right
                move_adder(board, myPosition, 1, 1, false, moves);
                //move up left
                move_adder(board, myPosition, 1, -1, false, moves);
                //move down right
                move_adder(board, myPosition, -1, 1, false, moves);
                //move down left
                move_adder(board, myPosition, -1, -1, false, moves);
                break;
            }
            case PieceType.KNIGHT: {
                move_adder(board, myPosition, 2, -1, false, moves);
                move_adder(board, myPosition, 2, 1, false, moves);
                move_adder(board, myPosition, -2, -1, false, moves);
                move_adder(board, myPosition, -2, 1, false, moves);
                move_adder(board, myPosition, -1, 2, false, moves);
                move_adder(board, myPosition, 1, 2, false, moves);
                move_adder(board, myPosition, -1, -2, false, moves);
                move_adder(board, myPosition, 1, -2, false, moves);
                break;
            }

            case PieceType.PAWN: {
                //moves for white pawn
                if (pieceColor == ChessGame.TeamColor.WHITE) {
                    pawn_mover(board, myPosition, 1,  moves);
                    pawn_attack(board, myPosition, 1, 1, moves);
                    pawn_attack(board, myPosition, 1, -1, moves);
                }

                //moves for black pawn
                if (pieceColor == ChessGame.TeamColor.BLACK) {
                    pawn_mover(board, myPosition, -1,  moves);
                    pawn_attack(board, myPosition, -1, 1, moves);
                    pawn_attack(board, myPosition, -1, -1, moves);
                }
                break;
            }
            case PieceType.QUEEN: {
                //move right
                move_adder(board, myPosition, 0, 1, true, moves);
                //move left
                move_adder(board, myPosition, 0, -1, true, moves);
                //move up
                move_adder(board, myPosition, 1, 0, true, moves);
                //move down
                move_adder(board, myPosition, -1, 0, true, moves);
                //move up right
                move_adder(board, myPosition, 1, 1, true, moves);
                //move up left
                move_adder(board, myPosition, 1, -1, true, moves);
                //move down right
                move_adder(board, myPosition, -1, 1, true, moves);
                //move down left
                move_adder(board, myPosition, -1, -1, true, moves);
                break;
            }

            case PieceType.ROOK: {
                //move right
                move_adder(board, myPosition, 0, 1, true, moves);
                //move left
                move_adder(board, myPosition, 0, -1, true, moves);
                //move up
                move_adder(board, myPosition, 1, 0, true, moves);
                //move down
                move_adder(board, myPosition, -1, 0, true, moves);
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
