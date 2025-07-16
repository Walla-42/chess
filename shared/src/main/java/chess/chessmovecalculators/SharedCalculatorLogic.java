package chess.chessmovecalculators;

import chess.*;

import java.util.HashSet;

/**
 * Class built to house the shared move calculator logic for all pieces on the chessboard except the pawn.
 */
public abstract class SharedCalculatorLogic implements PieceMovesCalculator{

    public int[][] allowedDirectionalMoves(){
        return null;
    }

    public int[][] allowedFixedMoves(){
        return null;
    }

    /**
     * Implements the base code shared by all piece calculators. Has two parts - fixed moves and directional moves.
     * fixed moves are functional moves shared by King, and Knights. Directional moves are functional moves shared by
     * Queen, Bishop, and Rook, where pieces can move over long linear distances. Depending on what set of move
     * parameters are returned determines the calculator function used.
     *
     * @param board chessboard
     * @param myPosition  position of chess piece on chessboard
     * @return collection of chess moves calculated by the chess piece calculator
     *
     */
    public HashSet<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition){
        HashSet<ChessMove> moves = new HashSet<>();
        ChessPiece piece = board.getPiece(myPosition);
        if (piece == null) {return moves;}

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
                        break;
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
