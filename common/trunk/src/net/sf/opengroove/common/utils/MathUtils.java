package net.sf.opengroove.common.utils;

import java.math.BigInteger;
import java.math.MutableBigInteger;

/**
 * This class holds utilities related to math. This class essentially contains
 * algorithms and stuff
 * 
 * @author Alexander Boyd
 * 
 */
public class MathUtils
{
    /**
     * Converts a BigInteger to a String, using the chars specified as the
     * digits to encode. The first character is treated as the least, and the
     * last character as the most. If the number specified is 0, then the output
     * will consist of the first character in the string only. For example,
     * calling this method with <code>chars</code> equal to "0123456789" would
     * have the same result as calling <code>number.toString()</code> (IE it
     * results in the base 10 representation of the number).<br/><br/>
     * 
     * The algorithm used tends to be considerably less efficient than that
     * provided by {@link BigInteger#toString()} or
     * {@link BigInteger#toString(int)}, so those methods should be used where
     * possible.
     * 
     * @param number
     * @param chars
     * @return
     */
    public String toString(BigInteger number, String chars)
    {
        StringBuilder buffer = new StringBuilder();
        /*
         * TODO: this algorithm is not very efficient, but I didn't want to
         * spend a ton of time on it for now
         */
    }
}
