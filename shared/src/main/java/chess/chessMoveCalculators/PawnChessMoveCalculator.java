package chess.chessMoveCalculators;

import chess.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;


/**
 * Pawn Move Logic class. Used for calculating possible moves on the gameBoard. Implements PieceMovesCalculator Template.
 */
public class PawnChessMoveCalculator implements PieceMovesCalculator {

    /**
     * method for calculating the moves available to a pawn on the gameBoard
     *
     * @param board gameBoard in which the pieces are located
     * @param myPosition position of the targeted gamePiece on the gameBoard
     * @return HashSet of all possible moves called moves
     */
    public HashSet<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        HashSet<ChessMove> moves = new HashSet<>();

        ChessPiece piece = board.getPiece(myPosition);
        if (piece == null) return moves;

        ChessGame.TeamColor team = piece.getTeamColor();

        int startRow = (team == ChessGame.TeamColor.WHITE) ? 2 : 7;
        int promotionRow = (team == ChessGame.TeamColor.WHITE) ? 8 : 1;
        int moveDirection = (team == ChessGame.TeamColor.WHITE) ? 1 : -1;

        int row = myPosition.getRow();

        // forward moves
        if (board.isInBounds(row + moveDirection, myPosition.getColumn())) {
            ChessPosition newChessPosition = new ChessPosition(row + moveDirection, myPosition.getColumn());
            ChessPiece target = board.getPiece(newChessPosition);

            if (target == null) {
                addPromotions(myPosition, newChessPosition, promotionRow, moves);
                if (row == startRow) {
                    newChessPosition = new ChessPosition(row + moveDirection * 2, myPosition.getColumn());
                    target = board.getPiece(newChessPosition);

                    if (target == null) {
                        moves.add(new ChessMove(myPosition, newChessPosition));
                    }
                }
            }

        }

        // diagonal attack moves
        int[] attackColumns = {1, -1};
        for (int column : attackColumns) {
            if (board.isInBounds(row + moveDirection, myPosition.getColumn() + column)) {
                ChessPosition newChessPosition = new ChessPosition(row + moveDirection, myPosition.getColumn() + column);
                ChessPiece target = board.getPiece(newChessPosition);

                if (target != null && target.getTeamColor() != team) {
                    addPromotions(myPosition, newChessPosition, promotionRow, moves);
                }
            }
        }
        return moves;
    }

    public void addPromotions(ChessPosition currPosition, ChessPosition newPosition, int promotionRow, HashSet<ChessMove> ChessMoves) {
        if (newPosition.getRow() == promotionRow) {
            ChessPiece.PieceType[] promotions = new ChessPiece.PieceType[]{ChessPiece.PieceType.ROOK,
                    ChessPiece.PieceType.KNIGHT, ChessPiece.PieceType.QUEEN, ChessPiece.PieceType.BISHOP};

            for (ChessPiece.PieceType chessPiece : promotions) {
                ChessMoves.add(new ChessMove(currPosition, newPosition, chessPiece));
            }

        } else {
            ChessMoves.add(new ChessMove(currPosition, newPosition));
        }
    }
}