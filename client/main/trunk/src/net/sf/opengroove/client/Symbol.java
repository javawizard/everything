package net.sf.opengroove.client;

/**
 * A bunch of symbols that I've found that I think could be used in portions of
 * OpenGroove. the toString() method on each symbol returns a string with the
 * symbol itself, so you can just embed symbol enum constants directly within
 * strings. The string returned from toString() is guaranteed to only have one
 * symbol as encoded in UTF-8, although there is no guarantee that it will use
 * only one byte. In fact, almost all of the symbols here use two or more bytes
 * when encoded with UTF-8.
 * 
 * @author Alexander Boyd
 * 
 */
public enum Symbol
{
    POUND_STERLING("£"),
    /**
     * The character &uarr;, an up-arrow.
     */
    UP("↑"),
    /**
     * The character &larr;, a left-arrow.
     */
    LEFT("←"),
    /**
     * The character &rarr;, a right-arrow.
     */
    RIGHT("→"),
    /**
     * The character &darr;, a down-arrow.
     */
    DOWN("↓"),
    /**
     * The character &#x2610;, a checkbox with no markings on it.
     */
    CHECKBOX(0x2610),
    /**
     * The character &#x2611;, a checkbox with a check mark on it.
     */
    CHECKBOX_CHECK(0x2611),
    /**
     * The character &#x2612;, a checkbox with an X on it.
     */
    CHECKBOX_X(0x2612),
    /**
     * The character &#x263a;, a face with a smile on it.
     */
    SMILE(0x263a),
    /**
     * The character ℡, a character used to indicate a telephone number will
     * follow.
     */
    TEL("℡"),
    /**
     * The character ⊞, a box with a + sign in it.
     */
    BOX_PLUS("⊞"),
    /**
     * The character ⊟, a box with a - sign in it.
     */
    BOX_MINUS("⊟"),
    /**
     * The character ①, a circle with the number 1 inside it.
     */
    CIRCLE_1("①"),
    /**
     * The character Ⓐ, a circle with the letter A inside it.
     */
    CIRCLE_A("Ⓐ"),
    /**
     * The character ☏, a telephone icon.
     */
    TELEPHONE("☏"),
    /**
     * The character ➩, a right-arrow with a shadow under it.
     */
    RIGHT_3D("➩"),
    /**
     * The character ❄, a snowflake.
     */
    SNOWFLAKE("❄"),
    /**
     * The character ✉, an envelope
     */
    ENVELOPE("✉"),
    /**
     * The character ☀, the sun.
     */
    SUN("☀"),
    /**
     * The character ☁, some clouds
     */
    CLOUDS("☁"),
    /**
     * The character ☂, an umbrella
     */
    UMBRELLA("☂"),
    /**
     * The character ☃, a snowman
     */
    SNOWMAN("☃"),
    /**
     * The character &#x2020;, a cross.
     */
    CROSS("†"),
    /**
     * The character &#x2021;, a double-cross.
     */
    DOUBLE_CROSS("‡"),
    /**
     * The character &#x2114;, the pound sign. This should not be confused with
     * {@link #POUNDS_STERLING}.
     */
    POUNDS("℔"),
    /**
     * The character ℃, the degrees celsius symbol.
     */
    DEGREES_CELSIUS("℃"),
    /**
     * The character ℉, the degrees fahrenheit symbol
     */
    DEGREES_FAHRENHEIT("℉"),
    /**
     * The character &#x2654;, a white chess king.
     */
    CHESS_WHITE_KING(0x2654),
    /**
     * The character &#x2655;, a white chess queen.
     */
    CHESS_WHITE_QUEEN(0x2655),
    /**
     * The character &#x2656;, a white chess rook.
     */
    CHESS_WHITE_ROOK(0x2656),
    /**
     * The character &#x2657;, a white chess bishop.
     */
    CHESS_WHITE_BISHOP(0x2657),
    /**
     * The character &#x2658;, a white chess knight.
     */
    CHESS_WHITE_KNIGHT(0x2658),
    /**
     * The character &#x2659;, a white chess pawn.
     */
    CHESS_WHITE_PAWN(0x2659),
    /**
     * The character &#x265a;, a black chess king.
     */
    CHESS_BLACK_KING(0x265a),
    /**
     * The character &#x265b;, a black chess queen.
     */
    CHESS_BLACK_QUEEN(0x265b),
    /**
     * The character &#x265c;, a black chess rook.
     */
    CHESS_BLACK_ROOK(0x265c),
    /**
     * The character &#x265d;, a black chess bishop.
     */
    CHESS_BLACK_BISHOP(0x265d),
    /**
     * The character &#x265e;, a black chess knight.
     */
    CHESS_BLACK_KNIGHT(0x265e),
    /**
     * The character &#x265f;, a black chess pawn.
     */
    CHESS_BLACK_PAWN(0x265f);
    private String value;
    
    private Symbol(String value)
    {
        this.value = value;
    }
    
    private Symbol(int value)
    {
        this.value = "" + (char) value;
    }
    
    public String toString()
    {
        return value;
    }
    
    /**
     * Used for debugging purposes. Prints a table to stdout containing each
     * character and it's code point.
     */
    public static void printTable()
    {
        for (Symbol symbol : values())
        {
            System.out.println("Symbol "
                + symbol.toString()
                + " : 0x"
                + Integer.toHexString((int) symbol
                    .toString().charAt(0)) + " 0d"
                + ((int) symbol.toString().charAt(0)));
        }
    }
    
    public static void main(String[] args)
    {
        printTable();
    }
}
