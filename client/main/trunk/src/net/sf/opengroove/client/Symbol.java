package net.sf.opengroove.client;

/**
 * A bunch of symbols that I've found that I think could be used in portions of
 * OpenGroove. the toString() method on each symbol returns a string with the
 * symbol itself, so you can just embed symbol enum constants directly within
 * strings.
 * 
 * @author Alexander Boyd
 * 
 */
public enum Symbol
{
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
    SMILE(0x263a), TEL("℡"), BOX_PLUS("⊞"), BOX_MINUS("⊟"),
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
    POUNDS("℔"), DEGREES_CELSIUS("℃"), DEGREES_FAHRENHEIT(
        "℉"),
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
    CHESS_BLACK_KING(0x265a);
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
}
