package chess.chessMoveCalculators;

import chess.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public class KnightChessMoveCalculator implements PieceMovesCalculator{
    private static final int[][] allowedMoves = {{1,2}, {2,1}, {-1,2}, {-2,1}, {-2,-1}, {-1,-2}, {2,-1}, {1,-2}};

    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        List<ChessMove> moves = new ArrayList<>();
        ChessPiece piece = board.getPiece(myPosition);
        if (piece == null) return moves;

        ChessGame.TeamColor team = piece.getTeamColor();

        for (int[] movementDir: allowedMoves){
            int row = myPosition.getRow() + movementDir[0];
            int col = myPosition.getColumn() + movementDir[1];

            if (board.isInBounds(row, col)){
                ChessPosition newPiecePosition = new ChessPosition(row, col);
                ChessPiece target = board.getPiece(newPiecePosition);

                if (target == null){
                    moves.add(new ChessMove(myPosition, newPiecePosition));
                } else if (target.getTeamColor() != team){
                    moves.add(new ChessMove(myPosition, newPiecePosition));
                }
            }

        }
        return moves;
    }
}
