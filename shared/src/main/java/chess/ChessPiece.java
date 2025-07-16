package chess;

import java.util.Collection;
import java.util.Objects;

import chess.chessmovecalculators.*;


/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private final ChessGame.TeamColor color;
    private final PieceType type;

    public ChessPiece(ChessGame.TeamColor teamColor, PieceType pieceType) {
        this.color = teamColor;
        this.type = pieceType;
    }

    /**
     * Copy constructor for ChessPiece
     *
     * @param piece Piece that is being copied
     */
    public ChessPiece(ChessPiece piece){
        this.color = piece.getTeamColor();
        this.type = piece.getPieceType();
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
        return color;
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
        // might need separate calculators for each piece type -> Look up how to build classes for each.
        PieceMovesCalculator calculator = switch (type) {
            case KING -> new KingChessMoveCalculator();
            case QUEEN-> new QueenChessMoveCalculator();
            case KNIGHT-> new KnightChessMoveCalculator();
            case ROOK-> new RookChessMoveCalculator();
            case PAWN-> new PawnChessMoveCalculator();
            case BISHOP -> new BishopChessMoveCalculator();
        };
        return calculator.pieceMoves(board, myPosition);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        return color == that.color && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(color, type);
    }

    @Override
    public String toString() {
        char typeSymbol;
        switch (type){
            case KING -> typeSymbol = 'K';
            case QUEEN-> typeSymbol = 'Q';
            case KNIGHT-> typeSymbol = 'N';
            case ROOK-> typeSymbol = 'R';
            case PAWN-> typeSymbol = 'P';
            case BISHOP -> typeSymbol = 'B';
            default -> throw new RuntimeException("Unexpected piece on gameboard");
        }

        String stringValue;
        if (color == ChessGame.TeamColor.WHITE){
            stringValue = String.valueOf(typeSymbol);
        } else {
            stringValue = String.valueOf(Character.toLowerCase(typeSymbol));
        }
        return stringValue;
    }
}
