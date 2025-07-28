package ui;

import requests.LoginRequestBody;
import requests.RegisterRequestBody;
import responses.*;
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
    private final String green = EscapeSequences.SET_TEXT_COLOR_GREEN;
    private final String yellow = EscapeSequences.SET_TEXT_COLOR_YELLOW;

    public LogoutREPL(ServerFacade server, ClientSession session) {
        this.server = server;
        this.session = session;

        printWelcome();
    }


    public void run() {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            String username;
            String password;

            System.out.print("[Logged Out] >>> ");
            String[] userInput = scanner.nextLine().trim().split("\\s+");
            if (userInput.length == 0) {
                System.out.println(red + "Invalid input. " + reset + "Type " + green + "'help'" + reset + " for a list of commands.");
            }
            String command = userInput[0].toLowerCase();


            switch (command) {
                case "help":
                    printHelp();
                    break;
                case "register":
                    if (userInput.length != 4) {
                        System.out.println(red + "Invalid input for register. " + reset + "Type " + green + "'help'" + reset + " for more information.");
                        break;
                    }

                    username = userInput[1];
                    password = userInput[2];
                    String email = userInput[3];

                    try {
                        RegisterRequestBody request = new RegisterRequestBody(username, password, email);
                        RegisterResponseBody response = server.registerCall(request);

                        session.setAuthToken(response.authToken());
                        session.setUsername(response.username());

                        boolean quit = new LoginREPL(server, session).run();
                        if (quit) {
                            System.out.println(yellow + "Thank you for playing! Exiting now..." + reset);
                            return;
                        }

                    } catch (Throwable e) {
                        var msg = e.getMessage();
                        System.out.print(red + msg + "\n" + reset);
                    }
                    break;

                case "login":
                    if (userInput.length != 3) {
                        System.out.println(red + "Invalid input for login. " + reset + "Type " + green + "'help'" + reset + " for more information.");
                        break;
                    }

                    username = userInput[1];
                    password = userInput[2];

                    try {
                        LoginRequestBody request = new LoginRequestBody(username, password);
                        LoginResponseBody response = server.loginCall(request);

                        session.setUsername(response.username());
                        session.setAuthToken(response.authToken());

                        boolean quit = new LoginREPL(server, session).run();
                        if (quit) {
                            System.out.println(yellow + "Thank you for playing! Exiting now..." + reset);
                            return;
                        }

                    } catch (Throwable e) {
                        var msg = e.getMessage();
                        System.out.print(red + msg + "\n" + reset);
                    }
                    break;

                case "quit":
                    System.out.println(yellow + "Thank you for playing! Exiting now..." + reset);
                    return;
                default:
                    System.out.println(red + "Invalid command. " + reset + "Type " + green + "'help'" + reset + " for list of commands.");
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

        System.out.println(entryEmoji + crown + blue + " Welcome to 240 Chess! Type " + green + "'help'" + blue + " to get started " + reset + crown + entryEmoji);
    }
}
