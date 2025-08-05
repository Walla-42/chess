package ui;

import requests.LoginRequestBody;
import requests.RegisterRequestBody;
import responses.*;
import server.ClientSession;
import server.ServerFacade;

import java.util.Scanner;

import static ui.EscapeSequences.*;

public class LogoutREPL {
    private final ServerFacade server;
    private final ClientSession session;

    private static final String BLUE = SET_TEXT_COLOR_BLUE;
    private static final String RESET = RESET_TEXT_COLOR;
    private static final String RED = SET_TEXT_COLOR_RED;
    private static final String ENTRY_EMOJI = CROSSED_SWORDS;
    private static final String CROWN = EscapeSequences.CROWN;
    private static final String GREEN = SET_TEXT_COLOR_GREEN;
    private static final String YELLOW = SET_TEXT_COLOR_YELLOW;

    public LogoutREPL(ServerFacade server, ClientSession session) {
        this.server = server;
        this.session = session;

        printWelcome();
    }

    /**
     * Starts the REPL loop, allowing user to input commands while logged out.
     * Accepts commands: {@code register}, {@code login}, {@code help}, and {@code quit}.
     */
    public void run() {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.print("[Logged Out] >>> ");
            String[] userInput = scanner.nextLine().trim().split("\\s+");
            if (userInput.length == 0) {
                System.out.println(RED + "Invalid input. " + RESET + "Type " +
                        GREEN + "'help'" + RESET + " for a list of commands.");
            }
            String command = userInput[0].toLowerCase();


            switch (command) {
                case "help" -> printHelp();
                case "register" -> {
                    boolean quit = registerSequence(userInput);
                    if (quit) {
                        return;
                    }
                }
                case "login" -> {
                    boolean quit = loginSequence(userInput);
                    if (quit) {
                        return;
                    }
                }
                case "quit" -> {
                    System.out.println(YELLOW + "Thank you for playing! Exiting now..." + RESET);
                    return;
                }
                default -> System.out.println(RED + "Invalid command. " + RESET + "Type " +
                        GREEN + "'help'" + RESET + " for list of commands.");
            }
        }
    }

    /**
     * helper method to print help menu to the command line.
     */
    private void printHelp() {
        System.out.println("\t" + BLUE + "register <USERNAME> <PASSWORD> <EMAIL> " + RED + "- to create an account");
        System.out.println("\t" + BLUE + "login <USERNAME> <PASSWORD> " + RED + "- to play chess");
        System.out.println("\t" + BLUE + "quit " + RED + "- to exit application");
        System.out.println("\t" + BLUE + "help " + RED + "- display possible commands" + RESET);
    }

    /**
     * Prints the welcome getErrorMessage to the console.
     */
    private void printWelcome() {
        System.out.println(ENTRY_EMOJI + CROWN + BLUE + " Welcome to 240 Chess! Type " +
                GREEN + "'help'" + BLUE + " to get started " + RESET + CROWN + ENTRY_EMOJI);
    }

    /**
     * Attempts to register a new user given user command line input.
     *
     * @param userInput A string array containing the command and user credentials.
     * @return true if the user exits after registration and login, false otherwise.
     */
    private boolean registerSequence(String[] userInput) {
        if (userInput.length != 4) {
            System.out.println(RED + "Invalid input for register. " + RESET + "Type " +
                    GREEN + "'help'" + RESET + " for more information.");
            return false;
        }

        String username = userInput[1];
        String password = userInput[2];
        String email = userInput[3];

        try {
            RegisterRequestBody request = new RegisterRequestBody(username, password, email);
            RegisterResponseBody response = server.registerCall(request);

            session.setAuthToken(response.authToken());
            session.setUsername(response.username());

            boolean quit = new LoginREPL(server, session).run();
            if (quit) {
                System.out.println(YELLOW + "Thank you for playing! Exiting now..." + RESET);
                return true;
            }

        } catch (Throwable e) {
            var msg = e.getMessage();
            System.out.print(RED + msg + "\n" + RESET);
        }
        return false;
    }

    /**
     * Attempts to log in an existing user.
     *
     * @param userInput A string array containing the command and user credentials.
     * @return true if the user exits after login, false otherwise.
     */
    private boolean loginSequence(String[] userInput) {
        if (userInput.length != 3) {
            System.out.println(RED + "Invalid input for login. " + RESET + "Type " +
                    GREEN + "'help'" + RESET + " for more information.");
            return false;
        }

        String username = userInput[1];
        String password = userInput[2];

        try {
            LoginRequestBody request = new LoginRequestBody(username, password);
            LoginResponseBody response = server.loginCall(request);

            session.setUsername(response.username());
            session.setAuthToken(response.authToken());

            boolean quit = new LoginREPL(server, session).run();
            if (quit) {
                System.out.println(YELLOW + "Thank you for playing! Exiting now..." + RESET);
                return true;
            }

        } catch (Throwable e) {
            var msg = e.getMessage();
            System.out.print(RED + msg + "\n" + RESET);
        }
        return false;
    }
}
