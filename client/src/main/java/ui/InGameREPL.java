package ui;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Scanner;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;
import exceptions.ResponseException;
import model.GameData;
import server.ClientSession;
import server.ServerFacade;
import server.websocket.NotificationHandler;
import server.websocket.WebSocketFacade;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import static ui.EscapeSequences.*;
import static websocket.messages.ServerMessage.ServerMessageType.*;

public class InGameREPL implements NotificationHandler {
    private final ServerFacade server;
    private final ClientSession clientSession;
    private final String color;
    private final ChessGame chessGame;
    private final String gameName;
    private WebSocketFacade ws;
    private PrintStream out;

    private final String BLUE = SET_TEXT_COLOR_BLUE;
    private final String RESET = RESET_TEXT_COLOR;
    private final String GREEN = SET_TEXT_COLOR_GREEN;
    private final String RED = SET_TEXT_COLOR_RED;
    private final String YELLOW = SET_TEXT_COLOR_YELLOW;
    private final String ERASE = ERASE_LINE;

    private static final Map<Character, Integer> columnMapping = Map.of(
            'a', 1,
            'b', 2,
            'c', 3,
            'd', 4,
            'e', 5,
            'f', 6,
            'g', 7,
            'h', 8
    );


    private static final String ERASE_SCREEN = EscapeSequences.ERASE_SCREEN;


    public InGameREPL(ServerFacade server, ClientSession clientSession, GameData game, String color) {
        this.server = server;
        this.clientSession = clientSession;
        this.color = color;
        this.gameName = game.gameName();
        this.chessGame = game.game();

        ws = new WebSocketFacade(server.getBaseURL(), this);

        try {
            ws.enterGame(clientSession.getAuthToken(), clientSession.getUserCurrentGame());
        } catch (ResponseException e) {
            System.out.println(RED + "WebSocket connection failed: " + e.getMessage() + RESET);
            return;
        }

        System.out.print(ERASE_SCREEN);
        System.out.flush();

        printWelcome();

        out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
    }

    /**
     * Starts the REPL loop, allowing the player to interact with the game session.
     *
     * @return true if the user chooses to quit the application, false if they just exit the game view.
     * Accepts commands {@code help}, {@code exit}, and {@code quit}
     */
    public boolean run() {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            printPrompt();
            String[] userInput = scanner.nextLine().trim().split("\\s+");
            if (userInput.length == 0) {
                System.out.println("Enter a valid command or type 'help' for more information");
            }
            String command = userInput[0].toLowerCase();

            switch (command) {
                case "help" -> printHelp();
                case "redraw" -> redrawChessBoard();
                case "leave" -> {
                    ws.onLeave(clientSession.getAuthToken(), clientSession.getUserCurrentGame());
                    clientSession.removeCurrentGame();
                    System.out.print(ERASE_SCREEN);
                    System.out.flush();
                    printBasicMessage(YELLOW, "You have successfully exited game view. ", "'help'",
                            " for list of available commands.");
                    return false;
                }
                case "move" -> makeMove(userInput);
                case "resign" -> {
                    if (clientSession.getUserRole() != ClientSession.User_Role.PLAYER) {
                        System.out.println(RED + "Error: Observer cannot resign. Type 'leave' to leave game.");
                        break;
                    }

                    String confirmation;
                    while (true) {
                        System.out.println("Are you sure you want to resign? (yes/no)");
                        printPrompt();
                        confirmation = scanner.nextLine().trim().toLowerCase();

                        if (confirmation.equals("yes")) {
                            ws.onResign(clientSession.getAuthToken(), clientSession.getUserCurrentGame());
                            break;
                        } else if (confirmation.equals("no")) {
                            System.out.println("Resignation canceled.");
                            break;
                        } else {
                            System.out.println("Error: Invalid response. Type 'yes' or 'no' to confirm.");
                        }
                    }
                }
                case "highlight" -> highlight(userInput);
                case "quit" -> {
                    ws.onLeave(clientSession.getAuthToken(), clientSession.getUserCurrentGame());
                    System.out.print(ERASE_SCREEN);
                    System.out.flush();
                    return true;
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
        System.out.println("\t" + BLUE + "help " + RED + "- display possible commands" + RESET);
        System.out.println("\t" + BLUE + "redraw " + RED + "- redraw the current game board" + RESET);
        System.out.println("\t" + BLUE + "leave " + RED + "- leave the current game" + RESET);
        System.out.println("\t" + BLUE + "move <start column> <start row> <end column> <end row> " +
                "[promotion{QUEEN|ROOK|BISHOP|KNIGHT}]" + RED + "- moves the specified game piece" + RESET);
        System.out.println("\t" + BLUE + "resign " + RED + "- forfeit the game. You still remain in game." + RESET);
        System.out.println("\t" + BLUE + "highlight <column> <row> " + RED + "- highlights possible moves for the " +
                "selected game piece" + RESET);
    }

    private void redrawChessBoard() {
        GameBoardPrinter.printGameBoard(clientSession.getGameBoard(), color, out);
    }

    private void makeMove(String[] userInput) {
        try {
            int startRow = Integer.parseInt(userInput[2]);
            int startCol = columnMapping.get(userInput[1]);
            int endRow = Integer.parseInt(userInput[4]);
            int endCol = columnMapping.get(userInput[3]);
            ChessPiece.PieceType promotion = null;
            ChessPosition startPosition = new ChessPosition(startRow, startCol);
            ChessPosition endPosition = new ChessPosition(endRow, endCol);

            ChessMove chessMove;

            if (userInput.length == 6) {
                promotion = ChessPiece.PieceType.valueOf(userInput[5].toUpperCase());
                chessMove = new ChessMove(startPosition, endPosition, promotion);
            } else {
                chessMove = new ChessMove(startPosition, endPosition);
            }

            ws.onMove(clientSession.getAuthToken(), clientSession.getUserCurrentGame(), chessMove);
        } catch (NumberFormatException e) {
            printBasicMessage(RED, "Error: Invalid move coordinate", "help", "for more information");
        } catch (Throwable e) {
            var msg = e.getMessage();
            System.out.print(RED + msg + "\n" + RESET);
        }

    }

    private void highlight(String[] userInput) {
        throw new RuntimeException("not yet implemented");
    }

    /**
     * helper method to print the welcome message to teh command line
     */
    private void printWelcome() {
        System.out.println(BLUE + "You have joined " + GREEN + gameName + BLUE + " as " + GREEN +
                color + BLUE + "!" + " type " + GREEN + "'help'" + BLUE + " for more commands." + RESET);
    }

    @Override
    public void notify(ServerMessage notification) {
        System.out.print("\r\u001B[2K");
        if (notification.getServerMessageType() == ERROR) {
            ErrorMessage errorMessage = (ErrorMessage) notification;
            System.out.println(RED + errorMessage.getErrorMessage() + RESET);
        } else if (notification.getServerMessageType() == LOAD_GAME) {
            LoadGameMessage loadGameMessage = (LoadGameMessage) notification;
            clientSession.updateGameBoard(loadGameMessage.getGame().game());
            GameBoardPrinter.printGameBoard(loadGameMessage.getGame().game(), color, out);
        } else if (notification.getServerMessageType() == NOTIFICATION) {
            NotificationMessage notificationMessage = (NotificationMessage) notification;
            System.out.println(YELLOW + notificationMessage.message() + RESET);
        } else {
            printBasicMessage(RED, "Error: Unable to notify due to invalid notification message", "help",
                    "for more options. You should also probably contact support :)");
        }
        printPrompt();
    }

    private void printPrompt() {
        System.out.print("[" + GREEN + clientSession.getUsername() + YELLOW + " (IN GAME) " + RESET + "] >>> ");
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
}