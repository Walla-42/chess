package chess.chessMoveCalculators;

import chess.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public class QueenChessMoveCalculator implements PieceMovesCalculator{
    private static final int[][] allowedMoves = {{-1,1}, {1,1}, {1,-1}, {-1,-1}, {-1,0}, {1,0}, {0,-1}, {0,1}};

    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition){
        List<ChessMove> moves = new ArrayList<>();
        ChessPiece piece = board.getPiece(myPosition);
        if (piece == null) return moves;

        ChessGame.TeamColor team = piece.getTeamColor();

        for (int[] movementDir: allowedMoves){
            int row = myPosition.getRow() + movementDir[0];
            int col = myPosition.getColumn() + movementDir[1];

            while (board.isInBounds(row, col)){
                ChessPosition newPiecePosition = new ChessPosition(row, col);
                ChessPiece target = board.getPiece(newPiecePosition);

                if (target == null){
                    moves.add(new ChessMove(myPosition, newPiecePosition));
                } else if (target.getTeamColor() != team){
                    moves.add(new ChessMove(myPosition, newPiecePosition));
                    break;
                } else {
                    break; // target position is occupied by piece of same team color
                }

                row += movementDir[0];
                col += movementDir[1];
            }
        }
        return moves;
    }
}
