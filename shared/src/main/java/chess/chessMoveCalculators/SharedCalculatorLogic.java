package chess.chessMoveCalculators;

import chess.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class SharedCalculatorLogic implements PieceMovesCalculator{

    public int[][] allowedDirectionalMoves(){
        return null;
    }

    public int[][] allowedFixedMoves(){
        return null;
    }


    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition){
        List<ChessMove> moves = new ArrayList<>();
        ChessPiece piece = board.getPiece(myPosition);
        if (piece == null) return moves;

        ChessGame.TeamColor team = piece.getTeamColor();

        int[][] directionalMoves = allowedDirectionalMoves();
        if (directionalMoves != null){
            for (int[] movementDir: directionalMoves){

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
        }

        int[][] fixedMoves = allowedFixedMoves();
        if (fixedMoves != null) {
            for (int[] movementDir: fixedMoves){
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
        }
        return moves;
    }
}
