package org.opengroove.g4.server;

import java.io.File;

public class G4Server
{
    private static File storageFolder;
    /**
     * The message store folder, Within here is one folder for each username,
     * within that is one folder for each computer, within that is one file per
     * message which is the serialized InboundMessage packet that should be sent
     * for the message. OutboundMessage packets to be sent to other servers will
     * be stored in a similar folder in the future.
     */
    private static File messageFolder;
    /**
     * The auth folder. This contains one folder per user. The user's folder
     * contains a file called password which is the user's password. It contains
     * a file (which is currently empty) for each computer called
     * "computer_NAME", where NAME is the name of the computer. In the future,
     * it will contain a file called roster whose format I haven't figured out
     * yet.
     */
    private static File authFolder;
    
    /**
     * @param args
     */
    public static void main(String[] args)
    {
        storageFolder = new File("storage");
        messageFolder = new File(storageFolder, "messages");
    }
    
}
