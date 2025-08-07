package ui;

/**
 * This class contains constants and functions relating to ANSI Escape Sequences that are useful in the Client display
 */
public class EscapeSequences {

    private static final String UNICODE_ESCAPE = "\u001b";
    private static final String ANSI_ESCAPE = "\033";

    public static final String ERASE_SCREEN = UNICODE_ESCAPE + "[2J" + UNICODE_ESCAPE + "[H";
    public static final String ERASE_LINE = UNICODE_ESCAPE + "[2K";

    public static final String RESET_ALL = UNICODE_ESCAPE + "[0m";

    // text effects
    public static final String SET_TEXT_BOLD = UNICODE_ESCAPE + "[1m";
    public static final String SET_TEXT_FAINT = UNICODE_ESCAPE + "[2m";
    public static final String RESET_TEXT_BOLD_FAINT = UNICODE_ESCAPE + "[22m";
    public static final String SET_TEXT_ITALIC = UNICODE_ESCAPE + "[3m";
    public static final String RESET_TEXT_ITALIC = UNICODE_ESCAPE + "[23m";
    public static final String SET_TEXT_UNDERLINE = UNICODE_ESCAPE + "[4m";
    public static final String RESET_TEXT_UNDERLINE = UNICODE_ESCAPE + "[24m";
    public static final String SET_TEXT_BLINKING = UNICODE_ESCAPE + "[5m";
    public static final String RESET_TEXT_BLINKING = UNICODE_ESCAPE + "[25m";

    private static final String SET_TEXT_COLOR = UNICODE_ESCAPE + "[38;5;";
    private static final String SET_BG_COLOR = UNICODE_ESCAPE + "[48;5;";

    // text colors
    public static final String SET_TEXT_COLOR_BLACK = SET_TEXT_COLOR + "0m";
    public static final String SET_TEXT_COLOR_LIGHT_GREY = SET_TEXT_COLOR + "242m";
    public static final String SET_TEXT_COLOR_DARK_GREY = SET_TEXT_COLOR + "235m";
    public static final String SET_TEXT_COLOR_RED = SET_TEXT_COLOR + "160m";
    public static final String SET_TEXT_COLOR_GREEN = SET_TEXT_COLOR + "46m";
    public static final String SET_TEXT_COLOR_YELLOW = SET_TEXT_COLOR + "226m";
    public static final String SET_TEXT_COLOR_BLUE = SET_TEXT_COLOR + "12m";
    public static final String SET_TEXT_COLOR_MAGENTA = SET_TEXT_COLOR + "5m";
    public static final String SET_TEXT_COLOR_WHITE = SET_TEXT_COLOR + "15m";
    public static final String SET_TEXT_COLOR_TAN = SET_TEXT_COLOR + "43m";
    public static final String SET_TEXT_COLOR_LIGHT_GREEN = SET_TEXT_COLOR + "[92m";
    public static final String RESET_TEXT_COLOR = UNICODE_ESCAPE + "[39m";


    // background colors
    public static final String SET_BG_COLOR_BLACK = SET_BG_COLOR + "0m";
    public static final String SET_BG_COLOR_LIGHT_GREY = SET_BG_COLOR + "242m";
    public static final String SET_BG_COLOR_DARK_GREY = SET_BG_COLOR + "235m";
    public static final String SET_BG_COLOR_RED = SET_BG_COLOR + "160m";
    public static final String SET_BG_COLOR_GREEN = SET_BG_COLOR + "46m";
    public static final String SET_BG_COLOR_LIGHT_GREEN = SET_BG_COLOR + "10m";
    public static final String SET_BG_COLOR_DARK_GREEN = SET_BG_COLOR + "22m";
    public static final String SET_BG_COLOR_YELLOW = SET_BG_COLOR + "226m";
    public static final String SET_BG_COLOR_BLUE = SET_BG_COLOR + "12m";
    public static final String SET_BG_COLOR_MAGENTA = SET_BG_COLOR + "5m";
    public static final String SET_BG_COLOR_WHITE = SET_BG_COLOR + "15m";
    public static final String SET_BG_COLOR_TAN = SET_BG_COLOR + "94m";
    public static final String SET_BG_COLOR_ASPARAGUS = SET_BG_COLOR + "107m";
    public static final String SET_BG_COLOR_BRONZE = SET_BG_COLOR + "137m";
    public static final String SET_BG_COLOR_POLISHED_PINE = SET_BG_COLOR + "72m";
    public static final String SET_BG_COLOR_AVOCADO = SET_BG_COLOR + "65m";
    public static final String SET_BG_COLOR_ORANGE = SET_BG_COLOR + "208m";
    public static final String RESET_BG_COLOR = UNICODE_ESCAPE + "[49m";


    // chess pieces
    public static final String BLACK_KING = " ♚ ";
    public static final String BLACK_QUEEN = " ♛ ";
    public static final String BLACK_BISHOP = " ♝ ";
    public static final String BLACK_KNIGHT = " ♞ ";
    public static final String BLACK_ROOK = " ♜ ";
    public static final String BLACK_PAWN = " ♟ ";
    public static final String WHITE_KING = " ♚ ";
    public static final String WHITE_QUEEN = " ♛ ";
    public static final String WHITE_BISHOP = " ♝ ";
    public static final String WHITE_KNIGHT = " ♞ ";
    public static final String WHITE_ROOK = " ♜ ";
    public static final String WHITE_PAWN = " ♟ ";
    public static final String EMPTY = " \u2003 ";

    //Emoji
    public static final String CROSSED_SWORDS = "\u2694\uFE0F";
    public static final String CROWN = "\uD83D\uDC51";


    public static String moveCursorToLocation(int x, int y) {
        return UNICODE_ESCAPE + "[" + y + ";" + x + "H";
    }
}
