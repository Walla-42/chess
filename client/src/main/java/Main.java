import chess.*;

public class Main {
    public static void main(String[] args) {
        var piece = new ChessPiece(ChessGame.teamColor.WHITE, ChessPiece.pieceType.PAWN);
        System.out.println("♕ 240 Chess Client: " + piece);
    }
}