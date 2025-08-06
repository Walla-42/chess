package ui;

import java.util.Scanner;

import model.GameData;
import requests.*;
import responses.*;
import server.ClientSession;
import server.ServerFacade;

import static ui.EscapeSequences.*;

public class LoginREPL {
    private final ServerFacade server;
    private final ClientSession session;

    private static final String BLUE = SET_TEXT_COLOR_BLUE;
    private static final String RESET = RESET_TEXT_COLOR;
    private static final String RED = SET_TEXT_COLOR_RED;
    private static final String YELLOW = SET_TEXT_COLOR_YELLOW;
    private static final String GREEN = SET_TEXT_COLOR_GREEN;
    private static final String ERASE_SCREEN = EscapeSequences.ERASE_SCREEN;

    public LoginREPL(ServerFacade server, ClientSession session) {
        this.server = server;
        this.session = session;

        System.out.print(ERASE_SCREEN);
        System.out.flush();

        printWelcome();
    }

    /**
     * Starts and runs the REPL loop for logged-in user interactions
     *
     * @return true if the main application should terminate; false otherwise.
     * Accepts commands {@code help}, {@code logout}, {@code list}, {@code join}, {@code create},
     * {@code observe} and {@code quit}
     */
    public boolean run() {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.print("[" + SET_TEXT_COLOR_GREEN + session.getUsername() + RESET_TEXT_COLOR + "] >>> ");
            String[] userInput = scanner.nextLine().trim().split("\\s+");

            if (userInput.length == 0) {
                System.out.println("Enter a valid command or type 'help' for more information");
            }
            String command = userInput[0].toLowerCase();

            switch (command) {
                case "help" -> printHelp();
                case "logout" -> {
                    return logoutSequence(false);
                }
                case "list" -> listGamesSequence();
                case "join" -> joinGameSequence(userInput);
                case "create" -> createGameSequence(userInput);
                case "observe" -> observerSequence(userInput);
                case "quit" -> {
                    return logoutSequence(true);
                }
                default -> printBasicMessage(RED, "Invalid command. ", " 'help' ", "for list of commands.");
            }
        }
    }

    // helper methods:

    /**
     * helper method to print help menu to the command line.
     */
    private void printHelp() {
        printCommandFormat("create <NAME> ", "- to create a game");
        printCommandFormat("list ", "- to list all games");
        printCommandFormat("join <ID> [WHITE|BLACK] ", "- to join a game");
        printCommandFormat("observe <ID> ", "- to observe a game");
        printCommandFormat("logout ", "- to logout");
        printCommandFormat("quit ", "- to quit the application");
        printCommandFormat("help ", "- display possible commands");
    }


    /**
     * Helper method for logging out a user from the server and clearing the client session.
     */
    private boolean logoutSequence(boolean quitLogout) {
        LogoutRequestBody logoutRequest = new LogoutRequestBody();
        server.logoutCall(logoutRequest, session.getAuthToken());

        System.out.println(YELLOW + "Logging " + GREEN + session.getUsername() + YELLOW + " out. " + RESET);
        session.clearSession();
        return quitLogout;
    }

    /**
     * Helper method for listing all available games on the users server. Refreshes clients session mappings.
     */
    private void listGamesSequence() {
        ListGamesResponseBody response = server.listGamesCall(session.getAuthToken());

        System.out.printf("\t%s%-8s%-25s%-20s%-20s%s%n", YELLOW, "Game ID",
                "Game Name", "White Username", "Black Username", RESET);

        session.clearMap();
        int gameID = 0;
        for (GameData gameData : response.games()) {
            gameID += 1;
            String gameName = gameData.gameName();
            String blackUsername = gameData.blackUsername();
            String whiteUsername = gameData.whiteUsername();

            session.addGameDataMapValue(gameID, gameData);
            session.addLookupMapValue(gameID, gameData.gameID());
            System.out.printf("\t%s%-8d%s%-25s%-20s%-20s%s%n", YELLOW, gameID,
                    BLUE, gameName, whiteUsername, blackUsername, RESET);
        }
    }

    /**
     * Helper method for joining a game based off of user input to the client.
     * Uses client session to gather correct game information.
     *
     * @param userInput String[] of client command input
     */
    private void joinGameSequence(String[] userInput) {
        if (userInput.length != 3) {
            printUsageError("join", "<GAME ID> [WHITE|BLACK]");
            return;
        }

        try {
            String playerColor = userInput[2].toLowerCase();
            Integer userFacingGameID = Integer.parseInt(userInput[1]);
            Integer gameID = session.getGameID(userFacingGameID);
            GameData chessGame = session.getChessGame(userFacingGameID);

            if (gameID == null) {
                printBasicMessage(RED, "Error: Invalid Game ID: ", " 'list' ", "to view available games.");
                return;
            }

            JoinGameRequestBody request = new JoinGameRequestBody(playerColor, gameID);
            server.joinGameCall(request, session.getAuthToken());

            session.setCurrentGame(gameID);

            // Join Game Success
            System.out.println(YELLOW + "Joining game " + GREEN + userFacingGameID + YELLOW +
                    " as " + GREEN + playerColor + RESET);

            session.setUserRole(ClientSession.UserRole.PLAYER);

            new InGameREPL(server, session, chessGame, playerColor).run();

            printBasicMessage(YELLOW, "You have successfully exited game view. ", "'help'",
                    " for list of available commands.");

        } catch (NumberFormatException e) {
            printBasicMessage(RED, "Error: Invalid Game ID: ", " 'list' ", "to view available games.");

        } catch (Throwable e) {
            printCatchMessage(e);
        }
    }

    /**
     * Helper method for observing a game based off of user input to the client.
     *
     * @param userInput String[] of client command input
     */
    private void observerSequence(String[] userInput) {
        if (userInput.length != 2) {
            printUsageError("observe", "<GAME ID>");
            return;
        }

        try {
            Integer userFacingGameID = Integer.parseInt(userInput[1]);
            Integer gameID = session.getGameID(userFacingGameID);
            GameData chessGame = session.getChessGame(userFacingGameID);

            if (gameID == null) {
                printBasicMessage(RED, "Error: Invalid Game ID: ", " 'list' ", "to view available games.");
                return;
            }

            // Enter inGame REPL
            session.setCurrentGame(gameID);

            System.out.println(YELLOW + "Joining game " + GREEN + userFacingGameID + YELLOW +
                    " as " + GREEN + "observer" + RESET);

            session.setUserRole(ClientSession.UserRole.OBSERVER);
            new InGameREPL(server, session, chessGame, "white").run();
            printBasicMessage(YELLOW, "You have successfully exited game view. ", "'help'",
                    " for list of available commands.");

        } catch (NumberFormatException e) {
            printBasicMessage(RED, "Error: Invalid Game ID: ", " 'list' ", "to view available games.");
        } catch (Throwable e) {
            printCatchMessage(e);
        }
    }

    /**
     * Helper method for creating a game based off of user input to the client.
     *
     * @param userInput String[] of client command input
     */
    private void createGameSequence(String[] userInput) {
        if (userInput.length != 2) {
            printUsageError("create", "<GAME NAME>");
            return;
        }

        String gameName = userInput[1];

        CreateGameRequestBody request = new CreateGameRequestBody(gameName);
        server.createGameCall(request, session.getAuthToken());

        System.out.println(YELLOW + "Successfully created a game with name: " + GREEN + gameName + RESET);
    }


    // messages:

    /**
     * Prints the welcome message when the REPL is initialized.
     */
    private void printWelcome() {
        System.out.println(BLUE + "Welcome " + SET_TEXT_COLOR_GREEN + session.getUsername()
                + BLUE + "!" + " type " + SET_TEXT_COLOR_GREEN + "'help'" +
                BLUE + " for more commands." + RESET_TEXT_COLOR);
    }

    /**
     * Prints the format and description for a given command.
     *
     * @param usage       The syntax of the command.
     * @param description A short description of the command's purpose.
     */
    private void printCommandFormat(String usage, String description) {
        System.out.println(BLUE + usage + RED + description + RESET);
    }

    /**
     * Prints an error message indicating incorrect command usage.
     *
     * @param command The command that was used incorrectly.
     * @param usage   The correct usage for the command.
     */
    private void printUsageError(String command, String usage) {
        System.out.println(RED + "Error: Invalid input for " + GREEN + command +
                RED + ". Usage: " + RESET + "'" + command + " " + usage +
                "'." + "Type " + GREEN + "'help'" + RESET + " for more information.");
    }

    /**
     * Prints a simple system message with optional guidance.
     *
     * @param messageColor The ANSI color for the message.
     * @param message      The main content of the message.
     * @param guideCommand A suggestion or command for help.
     * @param helpMessage  Additional guidance.
     */
    private void printBasicMessage(String messageColor, String message, String guideCommand, String helpMessage) {
        System.out.println(messageColor + message + RESET + " Type" + GREEN + guideCommand +
                RESET + helpMessage);
    }

    /**
     * Prints the message from an exception in red.
     *
     * @param e The thrown exception.
     */
    private void printCatchMessage(Throwable e) {
        var msg = e.getMessage();
        System.out.print(RED + msg + "\n" + RESET);
    }

}


