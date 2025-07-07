package chess;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;

import static chess.ChessBoard.availableTeamMoves;

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
        gameBoard.resetBoard();
        setTeamTurn(TeamColor.WHITE);
    }

    /**
     * Method to check if a team has at least one move that will allow a player to escape check.
     * holds shared logic of isInCheckmate and isInStalemate methods.
     *
     * @param teamColor color of team that is currently playing
     * @return boolean true if a player cant escape check, false if a player can escape check.
     */
    private boolean cantEscapeCheck(TeamColor teamColor){
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition fromPos = new ChessPosition(row, col);
                ChessPiece piece = gameBoard.getPiece(fromPos);

                if (piece == null || piece.getTeamColor() != teamColor) {
                    continue;
                }

                Collection<ChessMove> moves = validMoves(fromPos);
                for (ChessMove move : moves) {
                    ChessBoard boardCopy = new ChessBoard(gameBoard);

                    ChessPiece movedPiece = piece;
                    if (move.promotionPiece != null) {
                        movedPiece = new ChessPiece(teamColor, move.promotionPiece);
                    }
                    boardCopy.addPiece(move.getEndPosition(), movedPiece);
                    boardCopy.addPiece(fromPos, null);

                    ChessGame testGame = new ChessGame();
                    testGame.setBoard(boardCopy);

                    if (!testGame.isInCheck(teamColor)) {
                        return false;
                    }
                }
            }
        }
        return true;
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

        Collection<ChessMove> allowedMoves = piece.pieceMoves(gameBoard, startPosition);

        Collection<ChessMove> legalMoves = new HashSet<>();
        for (ChessMove move : allowedMoves) {
            ChessBoard boardCopy = new ChessBoard(gameBoard);

            boardCopy.addPiece(move.getEndPosition(), piece);
            boardCopy.addPiece(startPosition, null);

            ChessGame testGame = new ChessGame();
            testGame.setBoard(boardCopy);

            if (!testGame.isInCheck(piece.getTeamColor())) {
                legalMoves.add(move);
            }
        }
        return legalMoves;
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
        ChessGame.TeamColor turn = getTeamTurn();
        ChessBoard chessBoard = getBoard();
        ChessPiece piece = chessBoard.getPiece(start);
        if (piece == null) throw new InvalidMoveException("Must select a valid piece");

        ChessGame.TeamColor team = piece.getTeamColor();
        if (team != turn) throw new InvalidMoveException("Not Your Turn");
        Collection<ChessMove> validMoves = validMoves(start);

        if (validMoves.contains(move)){

            if (move.promotionPiece != null){
                 piece = new ChessPiece(team, move.promotionPiece);
            }
            chessBoard.addPiece(end, piece);
            chessBoard.addPiece(start, null);

            updateTurn();
        } else {
            throw new InvalidMoveException("That move is not allowed for this piece.");
        }
    }

    /**
     * A function for updating the current team
     */
    public void updateTurn(){
        if (getTeamTurn() == TeamColor.BLACK){
            setTeamTurn(TeamColor.WHITE);
        } else {
            setTeamTurn(TeamColor.BLACK);
        }
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor){

        ChessPosition kingPosition = ChessBoard.findKing(teamColor, gameBoard);
        TeamColor enemy = (teamColor == TeamColor.WHITE) ? TeamColor.BLACK : TeamColor.WHITE;

        Collection<ChessMove> availableEnemyMoves = availableTeamMoves(enemy, gameBoard);
        for (ChessMove chessMove : availableEnemyMoves){
            ChessPosition endPosition = chessMove.getEndPosition();
            if (endPosition.equals(kingPosition)){
                return true;
            }
        }
        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        return (isInCheck(teamColor) && cantEscapeCheck(teamColor));
    }


    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        return (!isInCheck(teamColor) && cantEscapeCheck(teamColor));
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
