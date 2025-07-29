package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Objects;

import static ui.EscapeSequences.*;

public class GameBoardPrinter {

    private static final int SQUARE_WIDTH = 3;
    private static final int SQUARE_HEIGHT = 1;

    private static final String WHITE_BACKGROUND = SET_BG_COLOR_TAN;
    private static final String BLACK_BACKGROUND = SET_BG_COLOR_BRONZE;
    private static final String RESET = RESET_ALL;
    private static final String BORDER_BACKGROUND = SET_BG_COLOR_AVOCADO;
    private static final String BORDER_TEXT = SET_TEXT_COLOR_WHITE;

    public static void printGameBoard(ChessGame game, String color, PrintStream out) {
        ChessBoard board = game.getBoard();

        boolean whitePerspective = color != null && color.equalsIgnoreCase("white");

        int rowStart = whitePerspective ? 8 : 1;
        int rowEnd = whitePerspective ? 0 : 9;
        int rowStep = whitePerspective ? -1 : 1;

        int colStart = whitePerspective ? 0 : 7;
        int colEnd = whitePerspective ? 8 : -1;
        int colStep = whitePerspective ? 1 : -1;

        printLetters(out, whitePerspective);

        for (int row = rowStart; row != rowEnd; row += rowStep) {
            for (int line = 0; line < SQUARE_HEIGHT; line++) {
                printNumbers(out, line, row);

                for (int col = colStart; col != colEnd; col += colStep) {
                    boolean isDarkSquare = (row + col) % 2 == 0;
                    String bgColor = isDarkSquare ? BLACK_BACKGROUND : WHITE_BACKGROUND;

                    ChessPosition position = new ChessPosition(row, col + 1);
                    ChessPiece piece = board.getPiece(position);
                    String symbol = getPieceSymbol(piece);

                    if (line == SQUARE_HEIGHT / 2) {
                        int padding = ((SQUARE_WIDTH - 1) / 2) - 1;
                        out.print(bgColor + " ".repeat(padding) + symbol + " ".repeat(SQUARE_WIDTH - padding - 3) + RESET);
                    } else {
                        out.print(bgColor + " ".repeat(SQUARE_WIDTH) + RESET);
                    }
                }

                printNumbers(out, line, row);
                out.println();
            }
        }

        printLetters(out, whitePerspective);
        out.println("\n");
    }

    private static void printLetters(PrintStream out, boolean whitePerspective) {
        int padding = (SQUARE_WIDTH - 1) / 2;
        out.print(BORDER_BACKGROUND + BORDER_TEXT + "  ");
        for (int col = 0; col < 8; col++) {
            out.print(" ".repeat(padding + 1));

            if (whitePerspective) {
                out.print((char) ('a' + col));
                out.print(" ".repeat(SQUARE_WIDTH - padding - 2));
            } else {
                out.print((char) ('h' - col));
                out.print(" ".repeat(SQUARE_WIDTH - padding - 2));
            }

        }
        out.print("    " + RESET);
        out.println();
    }

    private static void printNumbers(PrintStream out, int line, int row) {
        out.print(BORDER_BACKGROUND + BORDER_TEXT);
        if (line == SQUARE_HEIGHT / 2) {
            out.printf(" %d ", row);
        } else {
            out.print("   ");
        }
        out.print(RESET);
    }

    private static String getPieceSymbol(ChessPiece piece) {
        if (piece == null) {
            return EMPTY;
        }
        ChessPiece.PieceType type = piece.getPieceType();
        boolean isWhite = piece.getTeamColor() == ChessGame.TeamColor.WHITE;

        return switch (type) {
            case KING -> isWhite ? WHITE_KING : BLACK_KING;
            case QUEEN -> isWhite ? WHITE_QUEEN : BLACK_QUEEN;
            case ROOK -> isWhite ? WHITE_ROOK : BLACK_ROOK;
            case BISHOP -> isWhite ? WHITE_BISHOP : BLACK_BISHOP;
            case KNIGHT -> isWhite ? WHITE_KNIGHT : BLACK_KNIGHT;
            case PAWN -> isWhite ? WHITE_PAWN : BLACK_PAWN;
        };
    }
}


