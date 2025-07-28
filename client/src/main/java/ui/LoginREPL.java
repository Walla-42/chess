package ui;

import java.util.Scanner;

import model.GamesObject;
import requests.*;
import responses.*;
import server.ClientSession;
import server.ServerFacade;

public class LoginREPL {
    private ServerFacade server;
    private ClientSession session;

    private final String blue = EscapeSequences.SET_TEXT_COLOR_BLUE;
    private final String reset = EscapeSequences.RESET_TEXT_COLOR;
    private final String red = EscapeSequences.SET_TEXT_COLOR_RED;
    private final String yellow = EscapeSequences.SET_TEXT_COLOR_YELLOW;
    private final String green = EscapeSequences.SET_TEXT_COLOR_GREEN;


    public LoginREPL(ServerFacade server, ClientSession session) {
        this.server = server;
        this.session = session;

        printWelcome();
    }

    public void run() {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.print("[" + green + session.getUsername() + reset + "] >>> ");
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
                    LogoutRequestBody logoutRequest = new LogoutRequestBody();
                    server.logoutCall(logoutRequest, session.getAuthToken());

                    System.out.println(yellow + "Logging " + green + session.getUsername() + yellow + " out. " + reset);
                    session.clearSession();
                    return;
                case "list":
                    ListGamesResponseBody response = server.listGamesCall(session.getAuthToken());

                    System.out.printf("\t%s%-8s%-25s%-20s%-20s%s%n", yellow, "Game ID",
                            "Game Name", "White Username", "Black Username", reset);

                    int gameID = 0;
                    for (GamesObject gameObject : response.games()) {
                        gameID += 1;
                        String gameName = gameObject.gameName();
                        String blackUsername = gameObject.blackUsername();
                        String whiteUsername = gameObject.whiteUsername();
                        System.out.printf("\t%s%-8d%s%-25s%-20s%-20s%s%n", yellow, gameID,
                                blue, gameName, whiteUsername, blackUsername, reset);
                    }
                    break;

                case "join":
                    // put join game call here
                    new LoginREPL(server, session).run();
                    break;
                case "create":
                    if (userInput.length != 2) {
                        System.out.println(red + "Invalid input for create. " + reset + "Type " + green + "'help'" + reset + " for more information.");
                        break;
                    }

                    String gameName = userInput[1];

                    CreateGameRequestBody request = new CreateGameRequestBody(gameName);
                    server.createGameCall(request, session.getAuthToken());

                    System.out.println(yellow + "Successfully create a game with name: " + green + gameName + reset);
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
        System.out.println(blue + "Welcome " + green + session.getUsername() + blue + "!" + " type " + red + "'help'" + blue + " for more commands." + reset);
    }
}
