package ui;

import java.util.Scanner;

import server.ClientSession;
import server.ServerFacade;

public class LoginREPL {
    private ServerFacade server;
    private ClientSession session;

    private final String blue = EscapeSequences.SET_TEXT_COLOR_BLUE;
    private final String reset = EscapeSequences.RESET_TEXT_COLOR;
    private final String red = EscapeSequences.SET_TEXT_COLOR_RED;


    public LoginREPL(ServerFacade server, ClientSession session) {
        this.server = server;
        this.session = session;

        printWelcome();
    }

    public void run() {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.print("[Logged In] >>> ");
            String[] userInput = scanner.nextLine().trim().split("\\s+");
            if (userInput.length == 0) {
                System.out.println("please type a valid command or 'help' for more information");
            }
            String command = userInput[0].toLowerCase();

            switch (command) {
                case "help":
                    printHelp();
                    break;
                case "logout":
                    // put logout call here
                    System.out.println("Logging out...");
                    return;
                case "list":
                    // put list games call here
                    break;
                case "join":
                    // put join game call here
                    new LoginREPL(server, session).run();
                    break;
                case "create":
                    // put create game call here
                    break;
                case "observe":
                    break;
                case "quit":
                    break;
                default:
                    System.out.println("Unknown command.");
                    printHelp();
            }
        }
    }

    private void printHelp() {
        System.out.println("\t" + blue + "create <NAME> " + red + "- to create a game");
        System.out.println("\t" + blue + "list " + red + "- to list all games");
        System.out.println("\t" + blue + "join <ID> [WHITE|BLACK] " + red + "- to join a game");
        System.out.println("\t" + blue + "observe <ID> " + red + "- to observe a game");
        System.out.println("\t" + blue + "logout " + red + "- to logout");
        System.out.println("\t" + blue + "quit " + red + "- to quit the application");
        System.out.println("\t" + blue + "help " + red + "- display possible commands" + reset);
    }

    private void printWelcome() {
        System.out.println("Welcome " + session.getUsername() + "!" + blue + " type " + red + "'help'" + blue + " for more commands." + reset);
    }
}
