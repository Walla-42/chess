package ui;

import java.util.Scanner;

import model.GameData;
import requests.*;
import responses.*;
import server.ClientSession;
import server.ServerFacade;

import static ui.EscapeSequences.*;

public class LoginREPL {
    private ServerFacade server;
    private ClientSession session;

    private static final String BLUE = SET_TEXT_COLOR_BLUE;
    private static final String RESET = RESET_TEXT_COLOR;
    private static final String RED = SET_TEXT_COLOR_RED;
    private static final String YELLOW = SET_TEXT_COLOR_YELLOW;
    private static final String GREEN = SET_TEXT_COLOR_GREEN;
    private static final String ERASE_SCREEN = EscapeSequences.ERASE_SCREEN;
    ;


    public LoginREPL(ServerFacade server, ClientSession session) {
        this.server = server;
        this.session = session;

        System.out.print(ERASE_SCREEN);
        System.out.flush();

        printWelcome();
    }

    public boolean run() {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.print("[" + SET_TEXT_COLOR_GREEN + session.getUsername() + RESET_TEXT_COLOR + "] >>> ");
            String[] userInput = scanner.nextLine().trim().split("\\s+");

            if (userInput.length == 0) {
                System.out.println("please type a valid command or 'help' for more information");
            }
            String command = userInput[0].toLowerCase();

            switch (command) {
                case "help" -> printHelp();
                case "logout" -> {
                    logoutSequence();
                    return false;
                }
                case "list" -> listGamesSequence();
                case "join" -> joinGameSequence(userInput);
                case "create" -> createGameSequence(userInput);
                case "observe" -> observerSequence(userInput);
                case "quit" -> {
                    logoutSequence();
                    return true;
                }
                default -> printBasicMessage(RED, "Invalid command. ", " 'help' ", "for list of commands.");
            }
        }
    }

    private void printHelp() {
        printCommandFormat("create <NAME> ", "- to create a game");
        printCommandFormat("list ", "- to list all games");
        printCommandFormat("join <ID> [WHITE|BLACK] ", "- to join a game");
        printCommandFormat("observe <ID> ", "- to observe a game");
        printCommandFormat("logout ", "- to logout");
        printCommandFormat("quit ", "- to quit the application");
        printCommandFormat("help ", "- display possible commands");
    }

    private void printWelcome() {
        System.out.println(BLUE + "Welcome " + SET_TEXT_COLOR_GREEN + session.getUsername()
                + BLUE + "!" + " type " + SET_TEXT_COLOR_GREEN + "'help'" +
                BLUE + " for more commands." + RESET_TEXT_COLOR);
    }

    private void logoutSequence() {
        LogoutRequestBody logoutRequest = new LogoutRequestBody();
        server.logoutCall(logoutRequest, session.getAuthToken());

        System.out.println(YELLOW + "Logging " + GREEN + session.getUsername() + YELLOW + " out. " + RESET);
        session.clearSession();
    }

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

            // Join Game Success
            System.out.println(YELLOW + "Joining game " + GREEN + userFacingGameID + YELLOW +
                    " as " + GREEN + playerColor + RESET);
            new InGameREPL(server, session, chessGame, playerColor).run();

            printBasicMessage(YELLOW, "You have successfully exited game view. ", "'help'",
                    " for list of available commands.");


        } catch (NumberFormatException e) {
            printBasicMessage(RED, "Error: Invalid Game ID: ", " 'list' ", "to view available games.");

        } catch (Throwable e) {
            printCatchMessage(e);
        }
    }

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
            new InGameREPL(server, session, chessGame, "white").run();

            // Success Message
            printBasicMessage(YELLOW, "You have successfully exited game view. ", "'help'",
                    " for list of available commands.");

        } catch (NumberFormatException e) {
            printBasicMessage(RED, "Error: Invalid Game ID: ", " 'list' ", "to view available games.");
        } catch (Throwable e) {
            printCatchMessage(e);
        }

    }

    private void createGameSequence(String[] userInput) {
        if (userInput.length != 2) {
            printUsageError("create", "<GAME NAME>");
            return;
        }

        String gameName = userInput[1];

        CreateGameRequestBody request = new CreateGameRequestBody(gameName);
        server.createGameCall(request, session.getAuthToken());

        printSystemMessage("Successfully created a game with name: ", gameName);
    }


    // messages:
    private void printCommandFormat(String usage, String description) {
        System.out.println(BLUE + usage + RED + description + RESET);
    }

    private void printUsageError(String command, String usage) {
        System.out.println(RED + "Error: Invalid input for " + GREEN + command +
                RED + ". Usage: " + RESET + "'" + command + " " + usage +
                "'." + "Type " + GREEN + "'help'" + RESET + " for more information.");
    }

    private void printBasicMessage(String MessageColor, String message, String guideCommand, String helpMessage) {
        System.out.println(MessageColor + message + RESET + " Type" + GREEN + guideCommand +
                RESET + helpMessage);
    }

    private void printSystemMessage(String message, String variable) {
        System.out.println(YELLOW + message + GREEN + variable + RESET);
    }

    private void printCatchMessage(Throwable e) {
        var msg = e.getMessage();
        System.out.print(RED + msg + "\n" + RESET);
    }

}


