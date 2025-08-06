package ui;

import chess.*;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
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

    /**
     * Prints the current state of a chess game board to the given output stream, with appropriate orientation and coloring.
     *
     * @param game  the {@code ChessGame} object containing the board state
     * @param color the perspective to render from; if "white", shows white at the bottom, otherwise black
     * @param out   the PrintStream to print the board to (supports UTF-8)
     */
    public static void printGameBoard(ChessGame game, String color, PrintStream out, Collection<ChessPosition> highlight) {
        ChessBoard board = game.getBoard();
        highlight = highlight != null ? highlight : new ArrayList<>();

        boolean whitePerspective = color != null && color.equalsIgnoreCase("white");

        int rowStart = whitePerspective ? 8 : 1;
        int rowEnd = whitePerspective ? 0 : 9;
        int rowStep = whitePerspective ? -1 : 1;

        int colStart = whitePerspective ? 0 : 7;
        int colEnd = whitePerspective ? 8 : -1;
        int colStep = whitePerspective ? 1 : -1;

        printLetters(out, whitePerspective);
        out.println();

        for (int row = rowStart; row != rowEnd; row += rowStep) {
            for (int line = 0; line < SQUARE_HEIGHT; line++) {
                printNumbers(out, line, row);

                for (int col = colStart; col != colEnd; col += colStep) {
                    boolean isDarkSquare = (row + col) % 2 == 0;
                    String bgColor = isDarkSquare ? BLACK_BACKGROUND : WHITE_BACKGROUND;
                    ChessPosition position = new ChessPosition(row, col + 1);
                    
                    if (highlight.contains(position)) {
                        if (bgColor.equals(BLACK_BACKGROUND)) {
                            bgColor = SET_BG_COLOR_GREEN;
                        } else {
                            bgColor = SET_BG_COLOR_LIGHT_GREEN;
                        }
                    }

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

    /**
     * Prints the column labels (letters a–h or h–a depending on perspective) above and below the board.
     *
     * @param out              the {@code PrintStream} to print to
     * @param whitePerspective true if rendering from white's perspective, false for black's perspective
     */
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
    }

    /**
     * Prints the row labels (numbers 1–8 or 8–1 depending on perspective) alongside the board.
     *
     * @param out  the PrintSteam to print to
     * @param line the line index within the square height
     * @param row  the current row number
     */
    private static void printNumbers(PrintStream out, int line, int row) {
        out.print(BORDER_BACKGROUND + BORDER_TEXT);
        if (line == SQUARE_HEIGHT / 2) {
            out.printf(" %d ", row);
        } else {
            out.print("   ");
        }
        out.print(RESET);
    }

    /**
     * Returns the Unicode character representing the given chess piece.
     *
     * @param piece the {@code ChessPiece} to represent
     * @return a string with the Unicode symbol for the piece, or an empty string if the square is empty
     */
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


