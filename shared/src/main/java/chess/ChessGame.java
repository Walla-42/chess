package chess;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    private ChessGame.TeamColor teamTurn;
    private ChessBoard gameBoard;

    public ChessGame() {
        gameBoard = new ChessBoard();
        setTeamTurn(TeamColor.WHITE);
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return teamTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        teamTurn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece piece = gameBoard.getPiece(startPosition);
        ChessGame.TeamColor team = piece.getTeamColor();
        Collection<ChessMove> validMove = piece.pieceMoves(gameBoard, startPosition);

        if (isInCheck(team)){
            if (piece.getPieceType() != ChessPiece.PieceType.KING) {
                validMove.clear();
            }
            return validMove;
        } else {
            return validMove;
        }
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPosition start = move.startPosition;
        ChessPosition end = move.endPosition;
        ChessBoard chessBoard = getBoard();
        ChessPiece piece = chessBoard.getPiece(start);
        if (piece == null) throw new InvalidMoveException();

        ChessGame.TeamColor team = piece.getTeamColor();
        Collection<ChessMove> validMoves = validMoves(start);

        // make move
        if (validMoves.contains(move)){
            if (move.promotionPiece != null){
                 piece = new ChessPiece(team, move.promotionPiece);
            }
            chessBoard.addPiece(end, piece);
            chessBoard.addPiece(start, null);

            // Update Team Turn
            updateTurn();
        } else {
            throw new InvalidMoveException();
        }
    }

    public void updateTurn(){
        if (getTeamTurn() == TeamColor.BLACK){
            setTeamTurn(TeamColor.WHITE);
        } else {
            setTeamTurn(TeamColor.BLACK);
        }
    }

    public Collection<ChessMove> availableTeamMoves(TeamColor teamColor){
        Collection<ChessMove> availableMoves = new HashSet<>();
        for (int i = 1; i <= 8; i++){
            for (int j = 1; j <= 8; j++){
                ChessPosition newPosition = new ChessPosition(i, j);
                ChessPiece piece = gameBoard.getPiece(newPosition);
                availableMoves.addAll(piece.pieceMoves(gameBoard, newPosition));
            }
        }
        return availableMoves;
    }
    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.gameBoard = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return this.gameBoard;
    }


    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return teamTurn == chessGame.teamTurn && Objects.equals(gameBoard, chessGame.gameBoard);
    }

    @Override
    public int hashCode() {
        return Objects.hash(teamTurn, gameBoard);
    }
}
