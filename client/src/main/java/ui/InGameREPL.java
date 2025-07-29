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
    private final String red = EscapeSequences.SET_TEXT_COLOR_RED;
    private static final String ERASE_SCREEN = EscapeSequences.ERASE_SCREEN;


    public InGameREPL(ServerFacade server, ClientSession session, GameData game, String color) {
        this.server = server;
        this.session = session;
        this.color = color;
        this.gameName = game.gameName();
        this.chessGame = game.game();

        System.out.print(ERASE_SCREEN);
        System.out.flush();

        printWelcome();

        PrintStream out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        GameBoardPrinter.printGameBoard(game.game(), color, out);

    }

    public boolean run() {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.print("[" + green + session.getUsername() + reset + "] >>> ");
            String[] userInput = scanner.nextLine().trim().split("\\s+");
            if (userInput.length == 0) {
                System.out.println("please type a valid command or 'help' for more information");
            }
            String command = userInput[0].toLowerCase();

            switch (command) {
                case "help" -> printHelp();
                case "exit" -> {
                    System.out.print(ERASE_SCREEN);
                    System.out.flush();
                    return true;
                }
                default -> System.out.println("Unknown command.");
            }
        }
    }

    private void printHelp() {
        System.out.println("\t" + blue + "exit " + red + "- to exit gameboard display" + reset);
        System.out.println("\t" + blue + "help " + red + "- display possible commands" + reset);
    }

    private void printWelcome() {
        System.out.println(blue + "You have joined " + green + gameName + blue + " as " + green +
                color + blue + "!" + " type " + green + "'help'" + blue + " for more commands." + reset);
    }
}