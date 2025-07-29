package ui;

import java.util.Scanner;

import model.GameData;
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
                case "logout" -> {
                    logoutSequence();
                    return false;
                }
                case "list" -> listGamesSequence();
                case "join" -> joinGameSequence(userInput);
                case "create" -> createGameSequence(userInput);
                case "observe" -> observerSequence();
                case "quit" -> {
                    logoutSequence();
                    return true;
                }
                default -> System.out.println("Unknown command.");
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
        System.out.println(blue + "Welcome " + green + session.getUsername() + blue + "!" + " type " +
                green + "'help'" + blue + " for more commands." + reset);
    }

    private void logoutSequence() {
        LogoutRequestBody logoutRequest = new LogoutRequestBody();
        server.logoutCall(logoutRequest, session.getAuthToken());

        System.out.println(yellow + "Logging " + green + session.getUsername() + yellow + " out. " + reset);
        session.clearSession();
    }

    private void listGamesSequence() {
        ListGamesResponseBody response = server.listGamesCall(session.getAuthToken());

        System.out.printf("\t%s%-8s%-25s%-20s%-20s%s%n", yellow, "Game ID",
                "Game Name", "White Username", "Black Username", reset);

        session.clearMap();
        int gameID = 0;
        for (GameData gameData : response.games()) {
            gameID += 1;
            String gameName = gameData.gameName();
            String blackUsername = gameData.blackUsername();
            String whiteUsername = gameData.whiteUsername();

            session.addGameDataMapValue(gameID, gameData);
            session.addLookupMapValue(gameID, gameData.gameID());
            System.out.printf("\t%s%-8d%s%-25s%-20s%-20s%s%n", yellow, gameID,
                    blue, gameName, whiteUsername, blackUsername, reset);
        }
    }

    private void joinGameSequence(String[] userInput) {
        if (userInput.length != 3) {
            System.out.println(red + "Invalid input for join. " +
                    reset + "Type " + green + "'help'" + reset + " for more information.");
            return;
        }

        try {
            String playerColor = userInput[2].toLowerCase();
            int userFacingGameID = Integer.parseInt(userInput[1]);

            Integer gameID = session.getGameID(userFacingGameID);

            GameData chessGame = session.getChessGame(userFacingGameID);

            if (gameID == null) {
                System.out.println(red + "Error: Invalid Game ID." + blue + " Type 'list' to view available games.");
                return;
            }
            JoinGameRequestBody request = new JoinGameRequestBody(playerColor, gameID);
            System.out.println(request);
            server.joinGameCall(request, session.getAuthToken());

            System.out.println(yellow + "Joining game " + green + userFacingGameID + yellow +
                    " as " + green + playerColor + reset);
            new InGameREPL(server, session, chessGame, playerColor).run();

        } catch (Throwable e) {
            var msg = e.getMessage();
            System.out.print(red + msg + "\n" + reset);
        }
    }

    private void observerSequence() {
        throw new RuntimeException("not yet implemented");
    }

    private void createGameSequence(String[] userInput) {
        if (userInput.length != 2) {
            System.out.println(red + "Invalid input for create. " + reset
                    + "Type " + green + "'help'" + reset + " for more information.");
            return;
        }

        String gameName = userInput[1];

        CreateGameRequestBody request = new CreateGameRequestBody(gameName);
        server.createGameCall(request, session.getAuthToken());

        System.out.println(yellow + "Successfully create a game with name: " + green + gameName + reset);
    }
}


