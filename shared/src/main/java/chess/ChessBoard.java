package chess;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {

    private ChessPiece[][] gameBoard;

    public ChessBoard() {
        gameBoard = new ChessPiece[8][8];
    }

    /**
     * copy constructor for ChessBoard Object
     *
     * @param board gameboard that is to be copied
     */
    public ChessBoard(ChessBoard board) {
        this.gameBoard = new ChessPiece[8][8];
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                ChessPiece piece = board.gameBoard[row][col];
                if (piece != null) {
                    this.gameBoard[row][col] = new ChessPiece(piece);
                } else {
                    this.gameBoard[row][col] = null;
                }
            }
        }
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        gameBoard[position.getRow()-1][position.getColumn()-1] = piece;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return gameBoard[position.getRow()-1][position.getColumn()-1];
    }

    /**
     * checks to see if a movement position is on the board
     *
     * @param row the row a piece is trying to move
     * @param col the column a piece is trying to move
     * @return true if position is on the board and false otherwise
     */
    public boolean isInBounds(int row, int col){
        return (row >= 1 && row <= 8) && (col >= 1 && col <= 8);
    }

    /**
     * A function for determining the available moves of all pieces of the opponent
     *
     * @param enemyColor color of the team current team
     * @return availableMoves array of available moves of the opposite team.
     */
    public static Collection<ChessMove> availableTeamMoves(ChessGame.teamColor enemyColor, ChessBoard board){
        Collection<ChessMove> availableMoves = new HashSet<>();
        for (int row = 1; row <= 8; row++){
            for (int col = 1; col <= 8; col++){
                ChessPosition newPosition = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(newPosition);
                if (piece != null && piece.getTeamColor() == enemyColor){
                    availableMoves.addAll(piece.pieceMoves(board, newPosition));
                }

            }
        }
        return availableMoves;
    }

    /**
     * Finds the position of the king on the chess board
     *
     * @param team current team
     * @param board current gameboard
     * @return king ChessPosition
     */
    public static ChessPosition findKing(ChessGame.teamColor team, ChessBoard board){
        for (int row = 1; row <= 8; row++){
            for (int col = 1; col <= 8; col++){
                ChessPosition newPosition = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(newPosition);
                if (piece != null && piece.getTeamColor() == team && piece.getPieceType() == ChessPiece.pieceType.KING){
                    return newPosition;
                }
            }
        }
        return null;
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        gameBoard = new ChessPiece[8][8];
        addPiece(new  ChessPosition(1,1), new ChessPiece(ChessGame.teamColor.WHITE, ChessPiece.pieceType.ROOK));
        addPiece(new  ChessPosition(1,2), new ChessPiece(ChessGame.teamColor.WHITE, ChessPiece.pieceType.KNIGHT));
        addPiece(new  ChessPosition(1,3), new ChessPiece(ChessGame.teamColor.WHITE, ChessPiece.pieceType.BISHOP));
        addPiece(new  ChessPosition(1,4), new ChessPiece(ChessGame.teamColor.WHITE, ChessPiece.pieceType.QUEEN));
        addPiece(new  ChessPosition(1,5), new ChessPiece(ChessGame.teamColor.WHITE, ChessPiece.pieceType.KING));
        addPiece(new  ChessPosition(1,6), new ChessPiece(ChessGame.teamColor.WHITE, ChessPiece.pieceType.BISHOP));
        addPiece(new  ChessPosition(1,7), new ChessPiece(ChessGame.teamColor.WHITE, ChessPiece.pieceType.KNIGHT));
        addPiece(new  ChessPosition(1,8), new ChessPiece(ChessGame.teamColor.WHITE, ChessPiece.pieceType.ROOK));

        for (int i = 1; i <= 8; i++){
            addPiece(new  ChessPosition(2,i), new ChessPiece(ChessGame.teamColor.WHITE, ChessPiece.pieceType.PAWN));
        }

        addPiece(new  ChessPosition(8,1), new ChessPiece(ChessGame.teamColor.BLACK, ChessPiece.pieceType.ROOK));
        addPiece(new  ChessPosition(8,2), new ChessPiece(ChessGame.teamColor.BLACK, ChessPiece.pieceType.KNIGHT));
        addPiece(new  ChessPosition(8,3), new ChessPiece(ChessGame.teamColor.BLACK, ChessPiece.pieceType.BISHOP));
        addPiece(new  ChessPosition(8,4), new ChessPiece(ChessGame.teamColor.BLACK, ChessPiece.pieceType.QUEEN));
        addPiece(new  ChessPosition(8,5), new ChessPiece(ChessGame.teamColor.BLACK, ChessPiece.pieceType.KING));
        addPiece(new  ChessPosition(8,6), new ChessPiece(ChessGame.teamColor.BLACK, ChessPiece.pieceType.BISHOP));
        addPiece(new  ChessPosition(8,7), new ChessPiece(ChessGame.teamColor.BLACK, ChessPiece.pieceType.KNIGHT));
        addPiece(new  ChessPosition(8,8), new ChessPiece(ChessGame.teamColor.BLACK, ChessPiece.pieceType.ROOK));

        for (int i = 1; i <= 8; i++){
            addPiece(new  ChessPosition(7,i), new ChessPiece(ChessGame.teamColor.BLACK, ChessPiece.pieceType.PAWN));
        }

    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessBoard that = (ChessBoard) o;
        return Objects.deepEquals(gameBoard, that.gameBoard);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(gameBoard);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int row = 7; row >= 0; row--) {
            for (int col = 0; col <= 7; col++) {
                ChessPiece piece = gameBoard[row][col];
                sb.append(piece == null ? "." : piece.toString().charAt(0));
                sb.append(" ");
            }
            sb.append("\n");
        }
        return sb.toString();
    }

}
