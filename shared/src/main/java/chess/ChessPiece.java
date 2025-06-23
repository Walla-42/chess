package chess;

import java.util.Collection;
import chess.chessMoveCalculators.*;


/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private final ChessGame.TeamColor Color;
    private final PieceType Type;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.Color = pieceColor;
        this.Type = type;
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
        return Color;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return Type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        // might need separate calculators for each piece type -> Look up how to build classes for each.
        return switch (Type) {
//            case KING ->;
//            case QUEEN->;
//            case KNIGHT->;
//            case ROOK->;
//            case PAWN->;
            case BISHOP -> bishopChessMoveCalculator.getMoves();
            default -> throw new RuntimeException("Unable to fetch chess piece moves");
        };
    }
}
