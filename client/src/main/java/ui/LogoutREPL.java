package ui;

import server.ClientSession;
import server.ServerFacade;

import java.util.Scanner;

public class LogoutREPL {
    private final ServerFacade server;
    private final ClientSession session;
    private final String blue = EscapeSequences.SET_TEXT_COLOR_BLUE;
    private final String reset = EscapeSequences.RESET_TEXT_COLOR;
    private final String red = EscapeSequences.SET_TEXT_COLOR_RED;
    private final String entryEmoji = EscapeSequences.CROSSED_SWORDS;
    private final String crown = EscapeSequences.CROWN;

    public LogoutREPL(ServerFacade server, ClientSession session) {
        this.server = server;
        this.session = session;

        printWelcome();
    }


    public void run() {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.print("[Logged Out] >>> ");
            String command = scanner.nextLine().trim().toLowerCase();

            switch (command) {
                case "help":
                    printHelp();
                    break;
                case "login":
                    // put login call here
                    new LoginREPL(server, session).run();
                    break;
                case "register":
                    // put register call here
                    new LoginREPL(server, session).run();
                    break;
                case "quit":
                    System.out.println("Exiting...");
                    return;
                default:
                    System.out.println("Unknown command.");
            }
        }
    }

    public void printHelp() {
        System.out.println("\t" + blue + "register <USERNAME> <PASSWORD> <EMAIL> " + red + "- to create an account");
        System.out.println("\t" + blue + "login <USERNAME> <PASSWORD> " + red + "- to play chess");
        System.out.println("\t" + blue + "quit " + red + "- to exit application");
        System.out.println("\t" + blue + "help " + red + "- display possible commands" + reset);
    }

    public void printWelcome() {

        System.out.println(entryEmoji + crown + blue + " Welcome to 240 Chess. Type " + red + "'help'" + blue + " to get started " + reset + crown + entryEmoji);
    }
}
