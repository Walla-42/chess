import chess.*;
import server.ClientSession;
import server.ServerFacade;
import ui.LogoutREPL;

public class Main {
    public static void main(String[] args) {
        String serverURL = "https://localhost:8080";
//        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
//        System.out.println("♕ 240 Chess Client: " + piece);
        ClientSession session = new ClientSession();
        ServerFacade server = new ServerFacade(serverURL);

        LogoutREPL preloginUI = new LogoutREPL(server, session);
        preloginUI.run();
    }
}