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
    public HashSet<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition){

        // Basic movement based on color:
        ChessPiece piece = board.getPiece(myPosition);
        ChessGame.TeamColor team = piece.getTeamColor();

        HashSet<ChessMove> moves = new HashSet<>();

        int startRow = (team == ChessGame.TeamColor.WHITE) ? 2 : 7;
        int promotionRow = (team == ChessGame.TeamColor.WHITE) ? 8 : 1;

        int pieceMovement = (team == ChessGame.TeamColor.WHITE) ? 1 : -1;

        int row = myPosition.getRow();

        // Forward Moves:
        if (board.isInBounds(row + pieceMovement, myPosition.getColumn())){
            ChessPosition newChessPosition = new ChessPosition(row + pieceMovement, myPosition.getColumn());
            ChessPiece target = board.getPiece(newChessPosition);

            if (target == null){
                chessPiecePromotion(myPosition, moves, promotionRow, newChessPosition);

                if (startRow == row){
                    if (board.isInBounds(row + pieceMovement*2, myPosition.getColumn())){
                        newChessPosition = new ChessPosition(row + pieceMovement*2, myPosition.getColumn());
                        target = board.getPiece(newChessPosition);

                        if (target == null){
                            moves.add(new ChessMove(myPosition, newChessPosition));
                        }
                    }
                }
            }
        }

        // Diagonal Attack Moves:
        int[] attackColumns = {-1,1};
        for (int attackMove : attackColumns){
            if (board.isInBounds(myPosition.getRow() + pieceMovement, myPosition.getColumn() + attackMove)){
                ChessPosition newAttackPosition = new ChessPosition(row + pieceMovement, myPosition.getColumn()+attackMove);
                ChessPiece attackTarget = board.getPiece(newAttackPosition);
                if (attackTarget == null) continue;

                if (attackTarget.getTeamColor() != team){
                    chessPiecePromotion(myPosition, moves, promotionRow, newAttackPosition);

                }
            }
        }
        return moves;
    }

    private void chessPiecePromotion(ChessPosition myPosition, HashSet<ChessMove> moves, int promotionRow, ChessPosition newChessPosition) {
        if (newChessPosition.getRow() == promotionRow){
            moves.add(new ChessMove(myPosition, newChessPosition, ChessPiece.PieceType.QUEEN));
            moves.add(new ChessMove(myPosition, newChessPosition, ChessPiece.PieceType.ROOK));
            moves.add(new ChessMove(myPosition, newChessPosition, ChessPiece.PieceType.BISHOP));
            moves.add(new ChessMove(myPosition, newChessPosition, ChessPiece.PieceType.KNIGHT));
        } else {
            moves.add(new ChessMove(myPosition, newChessPosition));
        }
    }
}