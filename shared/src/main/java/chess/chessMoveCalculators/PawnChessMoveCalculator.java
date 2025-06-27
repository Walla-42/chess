package chess.chessMoveCalculators;

import chess.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class PawnChessMoveCalculator implements PieceMovesCalculator {

    /**
     * if pawn is at starting position then it can move forward 2
     * if pawn is not at starting position then it can move forward 1
     * if there is a target in the diagonals {1,1} or {1,-1} then there is a valid move
     *
     * if team == white then promotionRow = 8 else promotinoRow = 1
     * if team == white then the startRow = 2 else startRow = 7
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        List<ChessMove> moves = new ArrayList<>();

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

        int[] attackColumns = {1, -1};
        // diagonal attack moves

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

    public void addPromotions(ChessPosition currPosition, ChessPosition newPosition, int promotionRow, List<ChessMove> ChessMoves) {
        if (newPosition.getRow() == promotionRow) {
            ChessPiece.PieceType[] promotions = new ChessPiece.PieceType[]{ChessPiece.PieceType.ROOK, ChessPiece.PieceType.KNIGHT, ChessPiece.PieceType.QUEEN, ChessPiece.PieceType.BISHOP};
            for (ChessPiece.PieceType chessPiece : promotions) {
                ChessMoves.add(new ChessMove(currPosition, newPosition, chessPiece));
            }
        } else {
            ChessMoves.add(new ChessMove(currPosition, newPosition));
        }
    }
}