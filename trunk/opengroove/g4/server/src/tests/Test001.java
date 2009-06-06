package tests;

import org.opengroove.g4.server.Command;

public class Test001
{
    
    /**
     * @param args
     */
    public static void main(String[] args)
    {
        System.out.println(Command.class.getMethods()[0].getParameterTypes()[0]
            .getName());
    }
    
}
