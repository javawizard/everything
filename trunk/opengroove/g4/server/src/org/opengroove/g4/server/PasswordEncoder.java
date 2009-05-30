package org.opengroove.g4.server;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import net.sf.opengroove.common.security.Hash;

public class PasswordEncoder
{
    
    /**
     * @param args
     */
    public static void main(String[] args) throws Throwable
    {
        System.out.println("Enter a password that you would like encoded.");
        String password =
            new BufferedReader(new InputStreamReader(System.in)).readLine();
        System.out.println(Hash.hash(password));
    }
    
}
