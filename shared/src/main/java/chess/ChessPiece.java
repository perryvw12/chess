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
    private ChessGame.TeamColor pieceColor;
    private ChessPiece.PieceType type;

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

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<ChessMove>();

        switch (type) {
            case PieceType.BISHOP: {
                break;
            }

            case PieceType.KING: {
                //move left
                ChessPosition new_position = new ChessPosition(myPosition.getRow(), myPosition.getColumn() - 1);
                moves.add(new ChessMove(myPosition, new_position, null));

                //move left down
                new_position = new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn() - 1);
                moves.add(new ChessMove(myPosition, new_position, null));

                //move left up
                new_position = new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() - 1);
                moves.add(new ChessMove(myPosition, new_position, null));

                //move right
                new_position = new ChessPosition(myPosition.getRow(), myPosition.getColumn() + 1);
                moves.add(new ChessMove(myPosition, new_position, null));

                //move right down
                new_position = new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn() + 1);
                moves.add(new ChessMove(myPosition, new_position, null));

                //move right up
                new_position = new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() + 1);
                moves.add(new ChessMove(myPosition, new_position, null));

                //move up
                new_position = new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn());
                moves.add(new ChessMove(myPosition, new_position, null));

                //move down
                new_position = new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn());
                moves.add(new ChessMove(myPosition, new_position, null));
                break;
            }
            case PieceType.KNIGHT: {
                break;
            }

            case PieceType.PAWN: {
                //moves for white pawn
                if (pieceColor == ChessGame.TeamColor.WHITE) {
                    ChessPosition new_position = new ChessPosition((myPosition.getRow() + 1), myPosition.getColumn());
                    moves.add(new ChessMove(myPosition, new_position, null));

                    //checks if pawn is on original square
                    if(myPosition.getRow() == 2) {
                        new_position = new ChessPosition((myPosition.getRow() + 2), myPosition.getColumn());
                        moves.add(new ChessMove(myPosition, new_position, null));

                    }
                }

                //moves for black pawn
                if (pieceColor == ChessGame.TeamColor.BLACK) {
                    ChessPosition new_position = new ChessPosition((myPosition.getRow() - 1), myPosition.getColumn());
                    moves.add(new ChessMove(myPosition, new_position, null));

                    //checks if pawn is on original square
                    if(myPosition.getRow() == 7) {
                        new_position = new ChessPosition((myPosition.getRow() - 2), myPosition.getColumn());
                        moves.add(new ChessMove(myPosition, new_position, null));
                    }
                }
                break;
            }
            case PieceType.QUEEN: {
                break;
            }

            case PieceType.ROOK: {
                //vertical movement up
                for (int i = 1 + myPosition.getColumn(); i <= 8; i++) {
                    ChessPosition new_position = new ChessPosition(myPosition.getRow(), i);
                    moves.add(new ChessMove(myPosition, new_position, null));
                }
                //vertical movement down
                for (int i = myPosition.getColumn() - 1; i >= 1; i--) {
                    ChessPosition new_position = new ChessPosition(myPosition.getRow(), i);
                    moves.add(new ChessMove(myPosition, new_position, null));
                }
                //horizontal movement left
                for (int i = myPosition.getRow() - 1; i >= 1; i--) {
                    ChessPosition new_position = new ChessPosition(i, myPosition.getColumn());
                    moves.add(new ChessMove(myPosition, new_position, null));
                }
                //horizontal movement right
                for (int i = myPosition.getRow() + 1; i <= 8; i++) {
                    ChessPosition new_position = new ChessPosition(i, myPosition.getColumn());
                    moves.add(new ChessMove(myPosition, new_position, null));
                }
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
