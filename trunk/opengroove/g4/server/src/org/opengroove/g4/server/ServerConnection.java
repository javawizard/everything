package org.opengroove.g4.server;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;

public class ServerConnection
{
    private Socket socket;
    private InputStream socketIn;
    private OutputStream socketOut;
    private ObjectInputStream in;
    private ObjectOutputStream out;
}
