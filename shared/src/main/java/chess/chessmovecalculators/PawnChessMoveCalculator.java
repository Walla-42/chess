package chess.chessmovecalculators;

import chess.*;

import java.util.HashSet;

public class PawnChessMoveCalculator implements PieceMovesCalculator {

    @Override
    public HashSet<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        ChessPiece piece = board.getPiece(myPosition);
        ChessGame.TeamColor team = piece.getTeamColor();

        HashSet<ChessMove> moves = new HashSet<>();

        int startRow = (team == ChessGame.TeamColor.WHITE) ? 2 : 7;
        int promotionRow = (team == ChessGame.TeamColor.WHITE) ? 8 : 1;
        int pieceMovement = (team == ChessGame.TeamColor.WHITE) ? 1 : -1;

        addForwardMoves(board, myPosition, moves, pieceMovement, startRow, promotionRow);
        addDiagonalAttacks(board, myPosition, team, moves, pieceMovement, promotionRow);

        return moves;
    }

    private void addForwardMoves(ChessBoard board, ChessPosition myPosition, HashSet<ChessMove> moves,
                                 int pieceMovement, int startRow, int promotionRow) {
        int row = myPosition.getRow();
        int col = myPosition.getColumn();
        int oneStepRow = row + pieceMovement;
        int twoStepRow = row + 2 * pieceMovement;

        if (board.isInBounds(oneStepRow, col) && board.getPiece(new ChessPosition(oneStepRow, col)) == null) {
            ChessPosition newChessPosition = new ChessPosition(oneStepRow, col);
            chessPiecePromotion(myPosition, moves, promotionRow, newChessPosition);

            if (row == startRow && board.isInBounds(twoStepRow, col)
                    && board.getPiece(new ChessPosition(twoStepRow, col)) == null) {
                moves.add(new ChessMove(myPosition, new ChessPosition(twoStepRow, col)));
            }
        }
    }

    private void addDiagonalAttacks(ChessBoard board, ChessPosition myPosition, ChessGame.TeamColor team,
                                    HashSet<ChessMove> moves, int pieceMovement, int promotionRow) {
        int row = myPosition.getRow();
        int col = myPosition.getColumn();

        for (int attackMove : new int[]{-1, 1}) {
            int targetCol = col + attackMove;
            int targetRow = row + pieceMovement;

            if (!board.isInBounds(targetRow, targetCol)) continue;

            ChessPosition newAttackPosition = new ChessPosition(targetRow, targetCol);
            ChessPiece attackTarget = board.getPiece(newAttackPosition);

            if (attackTarget != null && attackTarget.getTeamColor() != team) {
                chessPiecePromotion(myPosition, moves, promotionRow, newAttackPosition);
            }
        }
    }

    private void chessPiecePromotion(ChessPosition myPosition, HashSet<ChessMove> moves, int promotionRow, ChessPosition newChessPosition) {
        if (newChessPosition.getRow() == promotionRow) {
            moves.add(new ChessMove(myPosition, newChessPosition, ChessPiece.pieceType.QUEEN));
            moves.add(new ChessMove(myPosition, newChessPosition, ChessPiece.pieceType.ROOK));
            moves.add(new ChessMove(myPosition, newChessPosition, ChessPiece.pieceType.BISHOP));
            moves.add(new ChessMove(myPosition, newChessPosition, ChessPiece.pieceType.KNIGHT));
        } else {
            moves.add(new ChessMove(myPosition, newChessPosition));
        }
    }
}
