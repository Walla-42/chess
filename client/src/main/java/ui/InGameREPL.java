package ui;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import chess.ChessGame;
import model.GameData;
import server.ClientSession;
import server.ServerFacade;

public class InGameREPL {
    private ServerFacade server;
    private ClientSession session;
    private final String color;
    private ChessGame chessGame;
    private String gameName;


    private final String blue = EscapeSequences.SET_TEXT_COLOR_BLUE;
    private final String reset = EscapeSequences.RESET_TEXT_COLOR;
    private final String green = EscapeSequences.SET_TEXT_COLOR_GREEN;


    public InGameREPL(ServerFacade server, ClientSession session, GameData game, String color) {
        this.server = server;
        this.session = session;
        this.color = color;
        this.gameName = game.gameName();
        this.chessGame = game.game();

        printWelcome();
        PrintStream out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        GameBoardPrinter.printGameBoard(game.game(), color, out);

    }

    public boolean run() {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            // Put Switch Casses here from user input
        }
    }

    private void printHelp() {
        throw new RuntimeException("not yet implemented");
    }

    private void printWelcome() {
        System.out.println(blue + "You have joined " + green + gameName + blue + " as " + green +
                color + "!" + blue + " type " + green + "'help'" + blue + " for more commands." + reset);
    }
}