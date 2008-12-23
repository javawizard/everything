package org.bzflag.jzapi;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * A program that generates bindings for the fields of a c++ class, using the
 * binding types in JZSimpleBinder. It creates native getters for all methods
 * who's type supports output, and native setters for all methods who's type
 * supports input. It issues notes on which types were bound (and which, of
 * getters and setters, were bound), and which types were skipped.
 * 
 * @author Alexander Boyd
 * 
 */
public class JZSimpleClassBinder
{
    
    /**
     * @param args
     */
    public static void main(String[] args)
    {
        private static final String targetClassName =
            "org/bzflag/jzapi/internal/SimpleBind";
        private static StringWriter dataOutputRegisterNatives =
            new StringWriter();
        private static StringWriter dataOutputJavaMethods =
            new StringWriter();
        private static StringWriter dataOutputNativeMethods =
            new StringWriter();
        private static PrintWriter outputRegisterNatives =
            new PrintWriter(dataOutputRegisterNatives);
        private static PrintWriter outputJavaMethods =
            new PrintWriter(dataOutputJavaMethods);
        private static PrintWriter outputNativeMethods =
            new PrintWriter(dataOutputNativeMethods);
    }
    
}
