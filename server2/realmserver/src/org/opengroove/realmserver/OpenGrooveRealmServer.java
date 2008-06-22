package org.opengroove.realmserver;

import java.io.File;
import java.sql.Connection;

public class OpenGrooveRealmServer
{
    private Connection db;
    
    private File dbFolder = new File("appdata/db");
    
    /**
     * @param args
     */
    public static void main(String[] args)
    {
        /*
         * Configuration that needs to be gathered before we can get up and
         * running:
         * 
         * Database type (use internal or connect to external), coming later
         * 
         * Database url, username, and password if external, see above, coming
         * later
         * 
         * web admin username and password
         * 
         * 
         */
    }
    
}
