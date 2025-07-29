package ui;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import chess.ChessGame;
import model.GameData;
import server.ClientSession;
import server.ServerFacade;

import static ui.EscapeSequences.*;

public class InGameREPL {
    private final ServerFacade server;
    private final ClientSession session;
    private final String color;
    private final ChessGame chessGame;
    private final String gameName;


    private final String BLUE = SET_TEXT_COLOR_BLUE;
    private final String RESET = RESET_TEXT_COLOR;
    private final String GREEN = SET_TEXT_COLOR_GREEN;
    private final String RED = SET_TEXT_COLOR_RED;
    private static final String ERASE_SCREEN = EscapeSequences.ERASE_SCREEN;


    public InGameREPL(ServerFacade server, ClientSession session, GameData game, String color) {
        this.server = server; // This will be used to track if a user is in a game
        this.session = session;
        this.color = color;
        this.gameName = game.gameName();
        this.chessGame = game.game();

        System.out.print(ERASE_SCREEN);
        System.out.flush();

        printWelcome();

        PrintStream out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        GameBoardPrinter.printGameBoard(chessGame, color, out);

    }

    public boolean run() {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.print("[" + GREEN + session.getUsername() + RESET + "] >>> ");
            String[] userInput = scanner.nextLine().trim().split("\\s+");
            if (userInput.length == 0) {
                System.out.println("Enter a valid command or type 'help' for more information");
            }
            String command = userInput[0].toLowerCase();

            switch (command) {
                case "help" -> printHelp();
                case "exit" -> {
                    System.out.print(ERASE_SCREEN);
                    System.out.flush();
                    return false;
                }
                case "quit" -> {
                    return true;
                }
                default -> System.out.println(RED + "Invalid command. " + RESET + "Type " +
                        GREEN + "'help'" + RESET + " for list of commands.");
            }
        }
    }

    private void printHelp() {
        System.out.println("\t" + BLUE + "exit " + RED + "- to exit gameboard display" + RESET);
        System.out.println("\t" + BLUE + "quit " + RED + "- to exit the application" + RESET);
        System.out.println("\t" + BLUE + "help " + RED + "- display possible commands" + RESET);
    }

    private void printWelcome() {
        System.out.println(BLUE + "You have joined " + GREEN + gameName + BLUE + " as " + GREEN +
                color + BLUE + "!" + " type " + GREEN + "'help'" + BLUE + " for more commands." + RESET);
    }
}